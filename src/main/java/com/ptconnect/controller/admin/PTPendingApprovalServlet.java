package com.ptconnect.controller.admin;

import com.ptconnect.model.dao.DBContext;
import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.dao.UserDAO;
import com.ptconnect.model.entity.PTProfile;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "PTPendingApprovalServlet", urlPatterns = {"/pt-pending-approval"})
public class PTPendingApprovalServlet extends HttpServlet {
    private UserDAO userDAO;
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        ptProfileDAO = new PTProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ArrayList<PTProfile> listPendingPTs = ptProfileDAO.getPendingPTProfiles();
        req.setAttribute("listPendingPTs", listPendingPTs);
        req.getRequestDispatcher("/WEB-INF/views/admin/pt-pending-approval.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        Long profileId = Long.parseLong(req.getParameter("profileId"));

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            PTProfileDAO txProfileDAO = new PTProfileDAO(conn);
            UserDAO txUserDAO = new UserDAO(conn);

            if ("REJECT".equalsIgnoreCase(action)) {
                if (txProfileDAO.reject(profileId)) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } else {
                Long ptId = Long.parseLong(req.getParameter("ptId"));
                if (txProfileDAO.approve(profileId) && txUserDAO.active(ptId)) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            }
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new ServletException("Lỗi phê duyệt hồ sơ PT", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
        resp.sendRedirect(req.getContextPath() + "/pt-pending-approval?msg=done");
    }
}
