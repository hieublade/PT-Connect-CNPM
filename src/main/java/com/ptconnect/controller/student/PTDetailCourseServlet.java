package com.ptconnect.controller.student;

import com.ptconnect.model.dao.CourseDAO;
import com.ptconnect.model.entity.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "PTDetailCourseServlet", urlPatterns = {"/pt-detail-course"})
public class PTDetailCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;

    @Override
    public void init() {
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long ptId = Long.parseLong(req.getParameter("ptId"));
        String ptName = req.getParameter("ptName");
        ArrayList<Course> listCourse = courseDAO.getCourseByPT(ptId);

        req.setAttribute("ptId", ptId);
        req.setAttribute("ptName", ptName);
        req.setAttribute("listCourse", listCourse);
        req.getRequestDispatcher("/WEB-INF/views/student/pt-detail-course.jsp").forward(req, resp);
    }
}
