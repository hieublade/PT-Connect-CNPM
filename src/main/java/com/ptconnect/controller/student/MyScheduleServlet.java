package com.ptconnect.controller.student;

import com.ptconnect.model.dao.AttendanceDAO;
import com.ptconnect.model.dao.BookingDAO;
import com.ptconnect.model.dao.DBContext;
import com.ptconnect.model.dao.ReviewDAO;
import com.ptconnect.model.entity.Attendance;
import com.ptconnect.model.entity.Booking;
import com.ptconnect.model.entity.Review;
import com.ptconnect.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "MyScheduleServlet", urlPatterns = {"/my-schedule"})
public class MyScheduleServlet extends HttpServlet {
    private BookingDAO bookingDAO;
    private AttendanceDAO attendanceDAO;
    private ReviewDAO reviewDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
        attendanceDAO = new AttendanceDAO();
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        String status = req.getParameter("status");
        ArrayList<Booking> listBooking = bookingDAO.getBooking(u.getId(), null, status);

        req.setAttribute("status", status);
        req.setAttribute("listBooking", listBooking);
        req.getRequestDispatcher("/WEB-INF/views/student/my-schedule.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        String action = req.getParameter("action");
        Long bookingId = Long.parseLong(req.getParameter("bookingId"));
        Booking booking = bookingDAO.getBookingById(bookingId, u.getId());

        if (booking == null) {
            resp.sendRedirect(req.getContextPath() + "/my-schedule");
            return;
        }

        if ("OTP_CHECKIN".equals(action)) {
            String otpCode = req.getParameter("otpCode");
            if (otpCode != null && attendanceDAO.studentConfirmOtp(bookingId, otpCode.trim())) {
                Attendance att = attendanceDAO.getByBookingId(bookingId);
                if (att != null && Boolean.TRUE.equals(att.getStudentConfirmed()) && Boolean.TRUE.equals(att.getPtConfirmed())) {
                    bookingDAO.completeBooking(bookingId);
                }
                resp.sendRedirect(req.getContextPath() + "/my-schedule?msg=otp_ok");
            } else {
                resp.sendRedirect(req.getContextPath() + "/my-schedule?err=otp_invalid");
            }
            return;
        }

        if ("SUBMIT_REVIEW".equals(action)) {
            if ("COMPLETED".equals(booking.getStatus()) && !reviewDAO.existsForBooking(bookingId)) {
                int rating = Integer.parseInt(req.getParameter("rating"));
                String comment = req.getParameter("comment");
                Review r = new Review();
                r.setBookingId(bookingId);
                r.setRating(Math.max(1, Math.min(5, rating)));
                r.setComment(comment);

                Connection conn = null;
                try {
                    conn = DBContext.getConnection();
                    ReviewDAO txReviewDAO = new ReviewDAO(conn);
                    txReviewDAO.createReview(r);
                } catch (SQLException e) {
                    throw new ServletException("Lỗi gửi đánh giá", e);
                } finally {
                    if (conn != null) {
                        try { conn.close(); } catch (SQLException ignored) {}
                    }
                }
            }
            resp.sendRedirect(req.getContextPath() + "/my-schedule?msg=review_ok");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/my-schedule");
    }
}
