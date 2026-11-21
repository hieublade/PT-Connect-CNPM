package com.ptconnect.controller.student;

import com.ptconnect.model.dao.*;
import com.ptconnect.model.entity.Booking;
import com.ptconnect.model.entity.Cancellation;
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
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet(name = "CancelBookingServlet", urlPatterns = {"/cancel-booking"})
public class CancelBookingServlet extends HttpServlet {
    private BookingDAO bookingDAO;
    private CancellationDAO cancellationDAO;
    private AvailabilitySlotDAO availabilitySlotDAO;
    private EnrollmentDAO enrollmentDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
        cancellationDAO = new CancellationDAO();
        availabilitySlotDAO = new AvailabilitySlotDAO();
        enrollmentDAO = new EnrollmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long bookingId = Long.parseLong(req.getParameter("bookingId"));
        Booking booking = bookingDAO.getBookingById(bookingId, u.getId());

        if (booking == null) {
            resp.sendRedirect(req.getContextPath() + "/my-schedule");
            return;
        }

        long hours = booking.getSlotStartTime() != null
                ? Math.max(0, Duration.between(LocalDateTime.now(), booking.getSlotStartTime()).toHours())
                : 24;
        boolean freeRefund = "PENDING".equals(booking.getStatus()) || hours >= 24;

        String cancelPolicy = "Hủy ca Chờ duyệt hoặc hủy trước >= 24 giờ: hoàn lại 1 buổi tập (nếu theo gói). Hủy ca Đã xác nhận < 24 giờ: không hoàn buổi và đền bù 50% giá trị buổi tập vào ví Huấn luyện viên.";
        String benefitWarning;
        if ("TRIAL".equals(booking.getBookingType())) {
            benefitWarning = "Ca học thử miễn phí: chỉ giải phóng khung giờ đã đặt.";
        } else if (freeRefund) {
            benefitWarning = "Đủ điều kiện hoàn 1 buổi tập vào hợp đồng (còn " + hours + " giờ trước giờ tập).";
        } else {
            BigDecimal comp = booking.getSessionFee() != null
                    ? booking.getSessionFee().multiply(BigDecimal.valueOf(0.5)).setScale(0, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            benefitWarning = "Hủy sát giờ (< 24 giờ): KHÔNG được hoàn buổi và đền bù " + comp + " đ (50%) vào ví Huấn luyện viên.";
        }

        req.setAttribute("booking", booking);
        req.setAttribute("cancelPolicy", cancelPolicy);
        req.setAttribute("benefitWarning", benefitWarning);
        req.getRequestDispatcher("/WEB-INF/views/student/cancel-booking.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long bookingId = Long.parseLong(req.getParameter("bookingId"));
        String reason = req.getParameter("cancelReason");

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            BookingDAO txBookingDAO = new BookingDAO(conn);
            CancellationDAO txCancelDAO = new CancellationDAO(conn);
            AvailabilitySlotDAO txSlotDAO = new AvailabilitySlotDAO(conn);
            EnrollmentDAO txEnrollDAO = new EnrollmentDAO(conn);
            WalletDAO txWalletDAO = new WalletDAO(conn);
            PaymentDAO txPaymentDAO = new PaymentDAO(conn);

            Booking booking = txBookingDAO.getBookingById(bookingId, u.getId());
            if (booking != null && ("PENDING".equals(booking.getStatus()) || "CONFIRMED".equals(booking.getStatus()))) {
                long hours = booking.getSlotStartTime() != null
                        ? Math.max(0, Duration.between(LocalDateTime.now(), booking.getSlotStartTime()).toHours())
                        : 24;
                boolean isPackage = "PACKAGE".equals(booking.getBookingType());
                boolean freeRefund = "PENDING".equals(booking.getStatus()) || hours >= 24;

                BigDecimal sessionFee = booking.getSessionFee() != null ? booking.getSessionFee() : BigDecimal.ZERO;
                BigDecimal refundAmount = (isPackage && freeRefund) ? sessionFee : BigDecimal.ZERO;
                BigDecimal compAmount = (isPackage && !freeRefund)
                        ? sessionFee.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                Cancellation c = new Cancellation();
                c.setBookingId(bookingId);
                c.setCancelledById(u.getId());
                c.setCancelledByRole("STUDENT");
                c.setReason(reason != null && !reason.isBlank() ? reason.trim() : "Học viên hủy lịch");
                c.setHoursBeforeSession(BigDecimal.valueOf(hours));
                c.setRefundAmount(refundAmount);
                c.setCompensationAmount(compAmount);
                c.setPenaltyAmount(BigDecimal.ZERO);
                c.setStatus("PROCESSED");

                txCancelDAO.createCancellation(c);
                txBookingDAO.cancelBooking(bookingId);
                txSlotDAO.updateSlotStatus(booking.getSlotId(), "AVAILABLE");

                if (isPackage && booking.getEnrollmentId() != null) {
                    if (freeRefund) {
                        txEnrollDAO.refundSession(booking.getEnrollmentId());
                    } else if (compAmount.compareTo(BigDecimal.ZERO) > 0 && booking.getPtUserId() != null) {
                        Wallet ptWallet = txWalletDAO.getWallet(booking.getPtUserId());
                        if (ptWallet != null && txWalletDAO.getCredit(ptWallet.getId(), compAmount)) {
                            Payment p = new Payment();
                            p.setWalletId(ptWallet.getId());
                            p.setEnrollmentId(booking.getEnrollmentId());
                            p.setAmount(compAmount);
                            p.setPaymentType("COMPENSATION");
                            p.setPaymentMethod("WALLET");
                            p.setStatus("SUCCESS");
                            p.setTransactionRef("TXN_COMP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                            txPaymentDAO.createPayment(p);
                        }
                    }
                }
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new ServletException("Lỗi hủy đặt lịch ca tập", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
        resp.sendRedirect(req.getContextPath() + "/my-schedule?msg=cancelled");
    }
}
