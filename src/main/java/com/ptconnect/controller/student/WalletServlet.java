package com.ptconnect.controller.student;

import com.ptconnect.model.dao.DBContext;
import com.ptconnect.model.dao.PaymentDAO;
import com.ptconnect.model.dao.WalletDAO;
import com.ptconnect.model.entity.Payment;
import com.ptconnect.model.entity.User;
import com.ptconnect.model.entity.Wallet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@WebServlet(name = "WalletServlet", urlPatterns = {"/wallet"})
public class WalletServlet extends HttpServlet {
    private WalletDAO walletDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() {
        walletDAO = new WalletDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Wallet wallet = walletDAO.getWallet(u.getId());
        ArrayList<Payment> listTransaction = wallet != null ? paymentDAO.getPayments(wallet.getId()) : new ArrayList<>();

        req.setAttribute("wallet", wallet);
        req.setAttribute("listTransaction", listTransaction);
        req.getRequestDispatcher("/WEB-INF/views/student/wallet.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));
        String paymentMethod = req.getParameter("paymentMethod");

        if (amount.compareTo(BigDecimal.valueOf(10000)) < 0 || amount.compareTo(BigDecimal.valueOf(100000000)) > 0) {
            resp.sendRedirect(req.getContextPath() + "/wallet?error=amount");
            return;
        }

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            WalletDAO txWalletDAO = new WalletDAO(conn);
            PaymentDAO txPaymentDAO = new PaymentDAO(conn);
            Wallet wallet = txWalletDAO.getWallet(u.getId());

            if (wallet != null) {
                Payment p = new Payment();
                p.setWalletId(wallet.getId());
                p.setAmount(amount);
                p.setPaymentType("TOPUP");
                p.setPaymentMethod(paymentMethod != null ? paymentMethod : "BANK_TRANSFER");
                p.setStatus("SUCCESS");
                p.setTransactionRef("TXN_TOPUP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                p.setCreatedAt(LocalDateTime.now());

                if (txPaymentDAO.createPayment(p) && txWalletDAO.getCredit(wallet.getId(), amount)) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            }
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new ServletException("Lỗi nạp tiền vào ví", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
        resp.sendRedirect(req.getContextPath() + "/wallet?success=1");
    }
}
