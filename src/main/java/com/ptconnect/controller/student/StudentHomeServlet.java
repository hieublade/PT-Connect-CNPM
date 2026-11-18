package com.ptconnect.controller.student;

import com.ptconnect.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "StudentHomeServlet", urlPatterns = {"/student-home"})
public class StudentHomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        req.setAttribute("u", u);
        req.getRequestDispatcher("/WEB-INF/views/student/student-home.jsp").forward(req, resp);
    }
}
