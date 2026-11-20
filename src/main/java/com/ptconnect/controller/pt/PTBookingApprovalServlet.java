package com.ptconnect.controller.pt;

import com.ptconnect.model.dao.*;
import com.ptconnect.model.entity.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@WebServlet(name = "PTBookingApprovalServlet", urlPatterns = {"/pt-booking-approval"})
public class PTBookingApprovalServlet extends HttpServlet {
    private AvailabilitySlotDAO availabilitySlotDAO;
    private AttendanceDAO attendanceDAO;
    private BookingDAO bookingDAO;
    private CancellationDAO cancellationDAO;
    private EnrollmentDAO enrollmentDAO;
    private PTProfileDAO ptProfileDAO;
    private final SecureRandom random = new SecureRandom();

    @Override
    public void init() {
        availabilitySlotDAO = new AvailabilitySlotDAO();
        attendanceDAO = new AttendanceDAO();
        bookingDAO = new BookingDAO();
        cancellationDAO = new CancellationDAO();
        enrollmentDAO = new EnrollmentDAO();
        ptProfileDAO = new PTProfileDAO();
    }

    private Long resolvePtId(HttpServletRequest req, User u) {
        Long ptId = (Long) req.getSession().getAttribute("ptId");
        if (ptId == null) {
            PTProfile p = ptProfileDAO.getProfileByUserId(u.getId());
            if (p != null) {
                ptId = p.getId();
                req.getSession().setAttribute("ptId", ptId);
            }
        }
        return ptId;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long ptId = resolvePtId(req, u);
        String statusFilter = req.getParameter("statusFilter");
        if (statusFilter == null || statusFilter.isBlank()) {
            statusFilter = "ALL";
        }

        ArrayList<Booking> all = ptId != null ? bookingDAO.getBooking(ptId, statusFilter) : new ArrayList<>();
        ArrayList<Booking> pendingBookingList = new ArrayList<>();
        ArrayList<Booking> confirmedBookingList = new ArrayList<>();
        ArrayList<Booking> otherBookingList = new ArrayList<>();

        for (Booking b : all) {
            if ("CONFIRMED".equals(b.getStatus())) {
                b.setAttendance(attendanceDAO.getByBookingId(b.getId()));
                confirmedBookingList.add(b);
            } else if ("PENDING".equals(b.getStatus())) {
                pendingBookingList.add(b);
            } else {
                otherBookingList.add(b);
            }
        }

        req.setAttribute("statusFilter", statusFilter);
        req.setAttribute("pendingBookingList", pendingBookingList);
        req.setAttribute("confirmedBookingList", confirmedBookingList);
        req.setAttribute("otherBookingList", otherBookingList);
        req.getRequestDispatcher("/WEB-INF/views/pt/pt-booking-approval.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long ptId = resolvePtId(req, u);
        if (ptId == null) {
            resp.sendRedirect(req.getContextPath() + "/pt-home");
            return;
        }

        String action = req.getParameter("action");
        Long bookingId = Long.parseLong(req.getParameter("bookingId"));
        Booking booking = bookingDAO.getBookingByIdForPT(bookingId, ptId);
        if (booking == null) {
            resp.sendRedirect(req.getContextPath() + "/pt-booking-approval");
            return;
        }

        if ("APPROVE".equals(action)) {
            Connection conn = null;
            try {
                conn = DBContext.getConnection();
                conn.setAutoCommit(false);
                BookingDAO txBooking = new BookingDAO(conn);
                AvailabilitySlotDAO txSlot = new AvailabilitySlotDAO(conn);
                if (txBooking.confirm(bookingId, ptId) && txSlot.markAsBooked(booking.getSlotId())) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } catch (Exception e) {
                if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
                throw new ServletException(e);
            } finally {
                if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        } else if ("REJECT".equals(action) || "CANCEL_CONFIRMED".equals(action)) {
            String reason = req.getParameter("reason");
            boolean isCancelConfirmed = "CANCEL_CONFIRMED".equals(action);
            Connection conn = null;
            try {
                conn = DBContext.getConnection();
                conn.setAutoCommit(false);
                BookingDAO txBooking = new BookingDAO(conn);
                CancellationDAO txCancel = new CancellationDAO(conn);
                AvailabilitySlotDAO txSlot = new AvailabilitySlotDAO(conn);
                EnrollmentDAO txEnroll = new EnrollmentDAO(conn);
                WalletDAO txWallet = new WalletDAO(conn);
                PaymentDAO txPayment = new PaymentDAO(conn);

                long hours = booking.getSlotStartTime() != null
                        ? Math.max(0, Duration.between(LocalDateTime.now(), booking.getSlotStartTime()).toHours())
                        : 24;
                BigDecimal fee = booking.getSessionFee() != null ? booking.getSessionFee() : BigDecimal.ZERO;
                BigDecimal penalty = (isCancelConfirmed && hours < 24 && "PACKAGE".equals(booking.getBookingType()))
                        ? fee.multiply(BigDecimal.valueOf(0.2)).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                Cancellation c = new Cancellation();
                c.setBookingId(bookingId);
                c.setCancelledById(u.getId());
                c.setCancelledByRole("PT");
                c.setReason(reason != null && !reason.isBlank() ? reason.trim() : "PT từ chối/hủy ca");
                c.setHoursBeforeSession(BigDecimal.valueOf(hours));
                c.setRefundAmount(BigDecimal.ZERO);
                c.setCompensationAmount(penalty);
                c.setPenaltyAmount(penalty);
                c.setStatus("PROCESSED");

                if (isCancelConfirmed) txBooking.cancelBooking(bookingId);
                else txBooking.reject(bookingId, ptId);
                txCancel.createCancellation(c);
                txSlot.releaseSlot(booking.getSlotId());
                if ("PACKAGE".equals(booking.getBookingType()) && booking.getEnrollmentId() != null) {
                    txEnroll.refundSession(booking.getEnrollmentId());
                }
                if (penalty.compareTo(BigDecimal.ZERO) > 0) {
                    Wallet ptWallet = txWallet.getWallet(u.getId());
                    Wallet stuWallet = txWallet.getWallet(booking.getStudentId());
                    if (ptWallet != null && stuWallet != null) {
                        txWallet.debit(ptWallet.getId(), penalty);
                        txWallet.getCredit(stuWallet.getId(), penalty);
                        Payment pay = new Payment();
                        pay.setWalletId(stuWallet.getId());
                        pay.setEnrollmentId(booking.getEnrollmentId());
                        pay.setAmount(penalty);
                        pay.setPaymentType("COMPENSATION");
                        pay.setPaymentMethod("WALLET");
                        pay.setStatus("SUCCESS");
                        pay.setTransactionRef("TXN_PTCOMP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                        txPayment.createPayment(pay);
                    }
                }
                conn.commit();
            } catch (Exception e) {
                if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
                throw new ServletException(e);
            } finally {
                if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        } else if ("OPEN_OTP".equals(action)) {
            String otp = String.format("%06d", random.nextInt(1_000_000));
            attendanceDAO.openOtp(bookingId, otp, LocalDateTime.now().plusMinutes(30));
        } else if ("COMPLETE_SESSION".equals(action)) {
            attendanceDAO.ptConfirmByBooking(bookingId);
            Attendance att = attendanceDAO.getByBookingId(bookingId);
            if (att != null && Boolean.TRUE.equals(att.getStudentConfirmed()) && Boolean.TRUE.equals(att.getPtConfirmed())) {
                bookingDAO.completeBooking(bookingId);
            }
        } else if ("MARK_ABSENT".equals(action)) {
            attendanceDAO.markAbsent(bookingId);
            bookingDAO.completeBooking(bookingId);
        }

        resp.sendRedirect(req.getContextPath() + "/pt-booking-approval");
    }
}
