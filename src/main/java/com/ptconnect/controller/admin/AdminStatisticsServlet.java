package com.ptconnect.controller.admin;

import com.ptconnect.model.dao.*;
import com.ptconnect.model.entity.Booking;
import com.ptconnect.model.entity.Cancellation;
import com.ptconnect.model.entity.PTProfile;
import com.ptconnect.model.entity.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet(name = "AdminStatisticsServlet", urlPatterns = {"/admin-statistics"})
public class AdminStatisticsServlet extends HttpServlet {
    private UserDAO userDAO;
    private BookingDAO bookingDAO;
    private PaymentDAO paymentDAO;
    private CancellationDAO cancellationDAO;
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        bookingDAO = new BookingDAO();
        paymentDAO = new PaymentDAO();
        cancellationDAO = new CancellationDAO();
        ptProfileDAO = new PTProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int totalStudent = userDAO.countByRole("STUDENT");
        int totalPT = userDAO.countByRole("PT");
        int pendingPTCount = ptProfileDAO.getPendingPTProfiles().size();
        ArrayList<Booking> listBooking = bookingDAO.getBooking();
        int totalBooking = listBooking.size();
        BigDecimal totalRevenue = paymentDAO.sumRevenue();
        ArrayList<Payment> listPayment = paymentDAO.getPaymentAnalytic();
        ArrayList<Cancellation> listCancellation = cancellationDAO.getCancellation();

        // Aggregate data for 5 Plotly charts
        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        statusCounts.put("PENDING", 0);
        statusCounts.put("CONFIRMED", 0);
        statusCounts.put("COMPLETED", 0);
        statusCounts.put("CANCELLED", 0);

        Map<Integer, Integer> hourCounts = new TreeMap<>();
        Map<String, Integer> ptCompletedCounts = new HashMap<>();
        Map<String, Integer> monthlyBookings = new TreeMap<>();
        DateTimeFormatter ymFmt = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Booking b : listBooking) {
            statusCounts.merge(b.getStatus(), 1, Integer::sum);
            if (b.getSlotStartTime() != null) {
                hourCounts.merge(b.getSlotStartTime().getHour(), 1, Integer::sum);
                monthlyBookings.merge(b.getSlotStartTime().format(ymFmt), 1, Integer::sum);
            }
            if ("COMPLETED".equals(b.getStatus()) && b.getPtName() != null) {
                ptCompletedCounts.merge(b.getPtName(), 1, Integer::sum);
            }
        }

        Map<String, BigDecimal> monthlyRevenue = new TreeMap<>();
        for (Payment p : listPayment) {
            if (p.getCreatedAt() != null) {
                String ym = p.getCreatedAt().format(ymFmt);
                monthlyRevenue.merge(ym, p.getAmount(), BigDecimal::add);
            }
        }

        Map<String, Integer> cancelByRole = new LinkedHashMap<>();
        cancelByRole.put("STUDENT", 0);
        cancelByRole.put("PT", 0);
        for (Cancellation c : listCancellation) {
            cancelByRole.merge(c.getCancelledByRole(), 1, Integer::sum);
        }

        req.setAttribute("totalStudent", totalStudent);
        req.setAttribute("totalPT", totalPT);
        req.setAttribute("pendingPTCount", pendingPTCount);
        req.setAttribute("totalBooking", totalBooking);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("listBooking", listBooking);
        req.setAttribute("listPayment", listPayment);
        req.setAttribute("listCancellation", listCancellation);

        req.setAttribute("statusCounts", statusCounts);
        req.setAttribute("hourCounts", hourCounts);
        req.setAttribute("ptCompletedCounts", ptCompletedCounts);
        req.setAttribute("monthlyBookings", monthlyBookings);
        req.setAttribute("monthlyRevenue", monthlyRevenue);
        req.setAttribute("cancelByRole", cancelByRole);

        req.getRequestDispatcher("/WEB-INF/views/admin/admin-statistics.jsp").forward(req, resp);
    }
}
