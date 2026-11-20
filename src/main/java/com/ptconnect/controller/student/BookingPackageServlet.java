package com.ptconnect.controller.student;

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
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@WebServlet(name = "BookingPackageServlet", urlPatterns = {"/booking-package"})
public class BookingPackageServlet extends HttpServlet {
    private EnrollmentDAO enrollmentDAO;
    private AvailabilitySlotDAO availabilitySlotDAO;
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        enrollmentDAO = new EnrollmentDAO();
        availabilitySlotDAO = new AvailabilitySlotDAO();
        ptProfileDAO = new PTProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        String bookingType = "TRIAL".equalsIgnoreCase(req.getParameter("bookingType")) ? "TRIAL" : "PACKAGE";

        ArrayList<Enrollment> listEnrollment = enrollmentDAO.getActiveEnrollment(u.getId());
        ArrayList<PTProfile> listPT = ptProfileDAO.getActivePT();
        ArrayList<AvailabilitySlot> listSlot = new ArrayList<>();

        if ("PACKAGE".equals(bookingType)) {
            Enrollment selected = null;
            String eidParam = req.getParameter("enrollmentId");
            for (Enrollment e : listEnrollment) {
                if (eidParam != null && eidParam.equals(String.valueOf(e.getId()))) {
                    selected = e;
                    break;
                }
            }
            if (selected == null && !listEnrollment.isEmpty()) {
                selected = listEnrollment.get(0);
            }
            if (selected != null) {
                ArrayList<AvailabilitySlot> allSlots = availabilitySlotDAO.getAvailableSlot(selected.getPtId());
                for (AvailabilitySlot s : allSlots) {
                    if (!s.getStartTime().toLocalDate().isAfter(selected.getEndDate())) {
                        listSlot.add(s);
                    }
                }
            }
            req.setAttribute("selectedEnrollment", selected);
        } else {
            PTProfile selectedPT = null;
            String ptIdParam = req.getParameter("ptId");
            for (PTProfile pt : listPT) {
                if (ptIdParam != null && ptIdParam.equals(String.valueOf(pt.getId()))) {
                    selectedPT = pt;
                    break;
                }
            }
            if (selectedPT == null && !listPT.isEmpty()) {
                selectedPT = listPT.get(0);
            }
            if (selectedPT != null) {
                listSlot = availabilitySlotDAO.getAvailableSlot(selectedPT.getId());
            }
            req.setAttribute("selectedPT", selectedPT);
        }

        req.setAttribute("bookingType", bookingType);
        req.setAttribute("listEnrollment", listEnrollment);
        req.setAttribute("listPT", listPT);
        req.setAttribute("listSlot", listSlot);
        req.getRequestDispatcher("/WEB-INF/views/student/booking-package.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        String bookingType = "TRIAL".equalsIgnoreCase(req.getParameter("bookingType")) ? "TRIAL" : "PACKAGE";
        Long slotId = Long.parseLong(req.getParameter("slotId"));
        String studentNote = req.getParameter("studentNote");

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            BookingDAO txBookingDAO = new BookingDAO(conn);
            AvailabilitySlotDAO txSlotDAO = new AvailabilitySlotDAO(conn);
            EnrollmentDAO txEnrollDAO = new EnrollmentDAO(conn);

            AvailabilitySlot slot = txSlotDAO.getSlotById(slotId);
            if (slot == null || !"AVAILABLE".equals(slot.getStatus()) || !slot.getStartTime().isAfter(LocalDateTime.now())) {
                conn.rollback();
                resp.sendRedirect(req.getContextPath() + "/booking-package?bookingType=" + bookingType + "&err=slot_unavailable");
                return;
            }
            if (txBookingDAO.hasConflictingBooking(u.getId(), slotId)) {
                conn.rollback();
                resp.sendRedirect(req.getContextPath() + "/booking-package?bookingType=" + bookingType + "&err=conflict");
                return;
            }

            Booking b = new Booking();
            b.setStudentId(u.getId());
            b.setSlotId(slotId);
            b.setBookingType(bookingType);
            b.setStatus("PENDING");
            b.setStudentNote(studentNote);

            if ("TRIAL".equals(bookingType)) {
                if (txBookingDAO.hasTrialWithPT(u.getId(), slot.getPtId())) {
                    conn.rollback();
                    resp.sendRedirect(req.getContextPath() + "/booking-package?bookingType=TRIAL&err=trial_exists");
                    return;
                }
                b.setEnrollmentId(null);
                b.setSessionFee(BigDecimal.ZERO);
                if (txSlotDAO.lockSlot(slotId) && txBookingDAO.createBooking(b)) {
                    conn.commit();
                    resp.sendRedirect(req.getContextPath() + "/my-schedule?msg=booked");
                } else {
                    conn.rollback();
                    resp.sendRedirect(req.getContextPath() + "/booking-package?bookingType=TRIAL&err=failed");
                }
                return;
            }

            Long enrollmentId = Long.parseLong(req.getParameter("enrollmentId"));
            Enrollment chosen = txEnrollDAO.getActiveEnrollment(u.getId())
                    .stream().filter(e -> e.getId().equals(enrollmentId)).findFirst().orElse(null);

            if (chosen == null || chosen.getRemainingSessions() <= 0 || slot.getStartTime().toLocalDate().isAfter(chosen.getEndDate())) {
                conn.rollback();
                resp.sendRedirect(req.getContextPath() + "/booking-package?enrollmentId=" + enrollmentId + "&err=invalid_enroll");
                return;
            }

            BigDecimal sessionFee = chosen.getFinalPrice()
                    .divide(BigDecimal.valueOf(chosen.getTotalSessions()), 2, RoundingMode.HALF_UP);
            b.setEnrollmentId(enrollmentId);
            b.setSessionFee(sessionFee);

            if (txSlotDAO.lockSlot(slotId) && txBookingDAO.createBooking(b) && txEnrollDAO.consumeSession(enrollmentId)) {
                conn.commit();
                resp.sendRedirect(req.getContextPath() + "/my-schedule?msg=booked");
            } else {
                conn.rollback();
                resp.sendRedirect(req.getContextPath() + "/booking-package?enrollmentId=" + enrollmentId + "&err=failed");
            }
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new ServletException("Lỗi đặt lịch ca tập", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
