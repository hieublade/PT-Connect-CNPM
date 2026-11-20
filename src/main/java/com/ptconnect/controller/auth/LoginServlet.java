package com.ptconnect.controller.auth;

import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.dao.UserDAO;
import com.ptconnect.model.dao.WalletDAO;
import com.ptconnect.model.entity.PTProfile;
import com.ptconnect.model.entity.User;
import com.ptconnect.model.entity.Wallet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login", ""})
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;
    private WalletDAO walletDAO;
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        walletDAO = new WalletDAO();
        ptProfileDAO = new PTProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            redirectByRole(req, resp, (User) session.getAttribute("currentUser"));
            return;
        }
        if ("suspended".equals(req.getParameter("error"))) {
            req.setAttribute("error", "Tài khoản của bạn đã bị tạm ngưng (SUSPENDED). Vui lòng liên hệ Quản trị viên!");
        }
        req.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        User user = userDAO.login(email, password);
        if (user == null) {
            req.setAttribute("error", "Email hoặc mật khẩu không chính xác!");
            req.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(req, resp);
            return;
        }

        if ("SUSPENDED".equals(user.getStatus())) {
            req.setAttribute("error", "Tài khoản của bạn đã bị tạm ngưng (SUSPENDED). Vui lòng liên hệ Quản trị viên!");
            req.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("currentUser", user);
        session.setAttribute("userId", user.getId());

        Wallet w = walletDAO.getWallet(user.getId());
        if (w != null) {
            session.setAttribute("walletId", w.getId());
        }
        if ("PT".equals(user.getRole())) {
            PTProfile pt = ptProfileDAO.getProfileByUserId(user.getId());
            if (pt != null) {
                session.setAttribute("ptId", pt.getId());
            }
        }
        redirectByRole(req, resp, user);
    }

    private void redirectByRole(HttpServletRequest req, HttpServletResponse resp, User u) throws IOException {
        String ctx = req.getContextPath();
        switch (u.getRole()) {
            case "PT": resp.sendRedirect(ctx + "/pt-home"); break;
            case "ADMIN": resp.sendRedirect(ctx + "/admin-dashboard"); break;
            default: resp.sendRedirect(ctx + "/student-home"); break;
        }
    }
}
