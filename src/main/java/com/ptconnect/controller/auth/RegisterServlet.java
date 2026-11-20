package com.ptconnect.controller.auth;

import com.ptconnect.model.dao.DBContext;
import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.dao.UserDAO;
import com.ptconnect.model.dao.WalletDAO;
import com.ptconnect.model.entity.User;
import com.ptconnect.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/common/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String role = "PT".equalsIgnoreCase(req.getParameter("role")) ? "PT" : "STUDENT";

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(role);
        user.setStatus("PT".equals(role) ? "PENDING" : "ACTIVE");

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            UserDAO userDAO = new UserDAO(conn);
            WalletDAO walletDAO = new WalletDAO(conn);
            PTProfileDAO ptProfileDAO = new PTProfileDAO(conn);

            Long userId = userDAO.register(user);
            if (userId == null) {
                conn.rollback();
                req.setAttribute("error", "Email hoặc số điện thoại đã tồn tại trong hệ thống!");
                req.getRequestDispatcher("/WEB-INF/views/common/register.jsp").forward(req, resp);
                return;
            }

            boolean walletOk = walletDAO.createWallet(userId);
            boolean profileOk = !"PT".equals(role) || ptProfileDAO.createDefaultProfile(userId);

            if (walletOk && profileOk) {
                conn.commit();
                resp.sendRedirect(req.getContextPath() + "/login?registered=1");
            } else {
                conn.rollback();
                req.setAttribute("error", "Không thể hoàn tất đăng ký tài khoản.");
                req.getRequestDispatcher("/WEB-INF/views/common/register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new ServletException("Lỗi đăng ký tài khoản", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
