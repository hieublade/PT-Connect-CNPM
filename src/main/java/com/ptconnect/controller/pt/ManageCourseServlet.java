package com.ptconnect.controller.pt;

import com.ptconnect.model.dao.CourseDAO;
import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.entity.Course;
import com.ptconnect.model.entity.PTProfile;
import com.ptconnect.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@WebServlet(name = "ManageCourseServlet", urlPatterns = {"/manage-course"})
public class ManageCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        courseDAO = new CourseDAO();
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
        ArrayList<Course> listCourse = ptId != null ? courseDAO.getCourses(ptId) : new ArrayList<>();

        req.setAttribute("listCourse", listCourse);
        req.getRequestDispatcher("/WEB-INF/views/pt/manage-course.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long ptId = resolvePtId(req, u);
        String action = req.getParameter("action");

        if ("TOGGLE_STATUS".equals(action)) {
            Course c = new Course();
            c.setId(Long.parseLong(req.getParameter("courseId")));
            c.setStatus("ACTIVE".equals(req.getParameter("currentStatus")) ? "INACTIVE" : "ACTIVE");
            courseDAO.updateCourse(c);
        } else if ("DELETE".equals(action)) {
            boolean deleted = courseDAO.deleteCourse(Long.parseLong(req.getParameter("courseId")));
            if (!deleted) {
                resp.sendRedirect(req.getContextPath() + "/manage-course?err=has_enrollment");
                return;
            }
        } else if (ptId != null) {
            Course c = new Course();
            c.setPtId(ptId);
            c.setTitle(req.getParameter("title"));
            c.setDescription(req.getParameter("description"));
            c.setTotalSessions(Integer.parseInt(req.getParameter("totalSessions")));
            c.setValidityDays(Integer.parseInt(req.getParameter("validityDays")));
            c.setBasePrice(new BigDecimal(req.getParameter("basePrice")));
            c.setStatus("ACTIVE");
            c.setCreatedAt(LocalDateTime.now());
            courseDAO.createCourse(c);
        }

        resp.sendRedirect(req.getContextPath() + "/manage-course?msg=ok");
    }
}
