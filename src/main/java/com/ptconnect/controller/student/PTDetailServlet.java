package com.ptconnect.controller.student;

import com.ptconnect.model.dao.*;
import com.ptconnect.model.entity.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "PTDetailServlet", urlPatterns = {"/pt-detail"})
public class PTDetailServlet extends HttpServlet {
    private PTProfileDAO ptProfileDAO;
    private PTLocationDAO ptLocationDAO;
    private CourseDAO courseDAO;
    private ReviewDAO reviewDAO;
    private AvailabilitySlotDAO slotDAO;

    @Override
    public void init() {
        ptProfileDAO = new PTProfileDAO();
        ptLocationDAO = new PTLocationDAO();
        courseDAO = new CourseDAO();
        reviewDAO = new ReviewDAO();
        slotDAO = new AvailabilitySlotDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long ptId = Long.parseLong(req.getParameter("ptId"));
        PTProfile pt = ptProfileDAO.getPTDetail(ptId);
        if (pt != null) {
            ArrayList<PTLocation> locations = ptLocationDAO.getLocations(ptId);
            ArrayList<Course> courses = courseDAO.getActiveCourse(ptId);
            ArrayList<Review> listReview = reviewDAO.getReview(ptId);
            ArrayList<AvailabilitySlot> slots = slotDAO.getAvailableSlot(ptId);

            pt.setLocations(locations);
            pt.setCourses(courses);
            pt.setAvailabilitySlots(slots);

            req.setAttribute("pt", pt);
            req.setAttribute("listReview", listReview);
        }
        req.getRequestDispatcher("/WEB-INF/views/student/pt-detail.jsp").forward(req, resp);
    }
}
