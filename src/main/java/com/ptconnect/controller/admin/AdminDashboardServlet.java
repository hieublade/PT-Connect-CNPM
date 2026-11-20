package com.ptconnect.controller.admin;

import com.ptconnect.model.dao.BookingDAO;
import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.dao.PaymentDAO;
import com.ptconnect.model.dao.UserDAO;
import com.ptconnect.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin-dashboard"})
public class AdminDashboardServlet extends HttpServlet {
    private UserDAO userDAO;
    private PTProfileDAO ptProfileDAO;
    private BookingDAO bookingDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        ptProfileDAO = new PTProfileDAO();
        bookingDAO = new BookingDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        req.setAttribute("u", u);
        req.setAttribute("totalStudent", userDAO.countByRole("STUDENT"));
        req.setAttribute("totalPT", userDAO.countByRole("PT"));
        req.setAttribute("pendingPTCount", ptProfileDAO.getPendingPTProfiles().size());
        req.setAttribute("totalBooking", bookingDAO.getBooking().size());
        req.setAttribute("totalRevenue", paymentDAO.sumRevenue());
        req.getRequestDispatcher("/WEB-INF/views/admin/admin-dashboard.jsp").forward(req, resp);
    }
}
