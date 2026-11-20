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
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@WebServlet(name = "ConfirmPurchaseCourseServlet", urlPatterns = {"/confirm-purchase", "/confirm-purchase-course"})
public class ConfirmPurchaseCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;
    private PTLocationDAO ptLocationDAO;
    private WalletDAO walletDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() {
        courseDAO = new CourseDAO();
        enrollmentDAO = new EnrollmentDAO();
        ptLocationDAO = new PTLocationDAO();
        walletDAO = new WalletDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long courseId = Long.parseLong(req.getParameter("courseId"));
        Course course = courseDAO.getCourseById(courseId);

        if (course == null || !"ACTIVE".equals(courseDAO.checkCourseStatus(courseId))) {
            resp.sendRedirect(req.getContextPath() + "/discovery-pt?err=course_inactive");
            return;
        }

        boolean alreadyEnrolled = enrollmentDAO.hasActiveEnrollment(u.getId(), courseId);
        ArrayList<PTLocation> listLocation = ptLocationDAO.getLocations(course.getPtId());
        BigDecimal balance = walletDAO.getWalletBalance(u.getId());
        Wallet wallet = new Wallet();
        wallet.setBalance(balance);

        req.setAttribute("course", course);
        req.setAttribute("wallet", wallet);
        req.setAttribute("listLocation", listLocation);
        req.setAttribute("finalPrice", course.getBasePrice());
        req.setAttribute("alreadyEnrolled", alreadyEnrolled);
        req.setAttribute("enoughBalance", balance.compareTo(course.getBasePrice()) >= 0);
        req.getRequestDispatcher("/WEB-INF/views/student/confirm-purchase-course.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long courseId = Long.parseLong(req.getParameter("courseId"));
        String selectedLoc = req.getParameter("trainingLocation");
        String customLoc = req.getParameter("customLocation");
        String trainingLocation = ("OTHER".equals(selectedLoc) && customLoc != null && !customLoc.isBlank())
                ? customLoc.trim()
                : selectedLoc;

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            CourseDAO txCourseDAO = new CourseDAO(conn);
            EnrollmentDAO txEnrollDAO = new EnrollmentDAO(conn);
            WalletDAO txWalletDAO = new WalletDAO(conn);
            PaymentDAO txPaymentDAO = new PaymentDAO(conn);

            Course course = txCourseDAO.getCourseById(courseId);
            Wallet wallet = txWalletDAO.getWallet(u.getId());

            if (course == null || !"ACTIVE".equals(course.getStatus()) || wallet == null
                    || txEnrollDAO.hasActiveEnrollment(u.getId(), courseId)) {
                conn.rollback();
                resp.sendRedirect(req.getContextPath() + "/discovery-pt?err=cannot_purchase");
                return;
            }

            BigDecimal finalPrice = course.getBasePrice();
            if (!txWalletDAO.debit(wallet.getId(), finalPrice)) {
                conn.rollback();
                resp.sendRedirect(req.getContextPath() + "/confirm-purchase?courseId=" + courseId + "&err=insufficient_balance");
                return;
            }

            Enrollment en = new Enrollment();
            en.setStudentId(u.getId());
            en.setCourseId(courseId);
            en.setTrainingLocation(trainingLocation);
            en.setFinalPrice(finalPrice);
            en.setTotalSessions(course.getTotalSessions());
            en.setRemainingSessions(course.getTotalSessions());
            en.setStartDate(LocalDate.now());
            en.setEndDate(LocalDate.now().plusDays(course.getValidityDays()));
            en.setContractUrl("HopDong_PTConnect_" + courseId + ".pdf");
            en.setStatus("ACTIVE");

            Long enrollmentId = txEnrollDAO.createEnrollmentAndReturnId(en);
            if (enrollmentId == null) {
                conn.rollback();
                resp.sendRedirect(req.getContextPath() + "/confirm-purchase?courseId=" + courseId + "&err=enroll_failed");
                return;
            }

            Payment p = new Payment();
            p.setWalletId(wallet.getId());
            p.setEnrollmentId(enrollmentId);
            p.setAmount(finalPrice);
            p.setPaymentType("PURCHASE_PACKAGE");
            p.setPaymentMethod("WALLET");
            p.setStatus("SUCCESS");
            p.setTransactionRef("TXN_BUY_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            if (txPaymentDAO.createPayment(p)) {
                conn.commit();
                resp.sendRedirect(req.getContextPath() + "/my-course?purchased=" + enrollmentId);
            } else {
                conn.rollback();
                resp.sendRedirect(req.getContextPath() + "/confirm-purchase?courseId=" + courseId + "&err=payment_failed");
            }
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new ServletException("Lỗi mua khóa học", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
