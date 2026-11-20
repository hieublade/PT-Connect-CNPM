package com.ptconnect.controller.admin;

import com.ptconnect.model.dao.UserDAO;
import com.ptconnect.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "UserManagementServlet", urlPatterns = {"/user-management"})
public class UserManagementServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = req.getParameter("role");
        ArrayList<User> listUsers = (role != null && !role.isBlank() && !"ALL".equalsIgnoreCase(role))
                ? userDAO.getUserByRole(role)
                : userDAO.getAllUsers();

        req.setAttribute("roleFilter", role != null ? role : "ALL");
        req.setAttribute("listUsers", listUsers);
        req.setAttribute("countFound", listUsers.size());
        req.getRequestDispatcher("/WEB-INF/views/admin/user-management.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentAdmin = (User) req.getSession().getAttribute("currentUser");
        Long userId = Long.parseLong(req.getParameter("userId"));
        String action = req.getParameter("action");

        if (currentAdmin != null && currentAdmin.getId().equals(userId)) {
            resp.sendRedirect(req.getContextPath() + "/user-management?err=self_lock");
            return;
        }

        String status = "UNLOCK".equalsIgnoreCase(action) ? "ACTIVE" : "SUSPENDED";
        userDAO.updateStatus(userId, status);
        resp.sendRedirect(req.getContextPath() + "/user-management?msg=updated");
    }
}
