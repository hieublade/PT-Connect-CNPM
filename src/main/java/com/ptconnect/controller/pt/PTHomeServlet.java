package com.ptconnect.controller.pt;

import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.entity.PTProfile;
import com.ptconnect.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "PTHomeServlet", urlPatterns = {"/pt-home"})
public class PTHomeServlet extends HttpServlet {
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        ptProfileDAO = new PTProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        PTProfile ptProfile = ptProfileDAO.getProfileByUserId(u.getId());
        if (ptProfile != null) {
            req.getSession().setAttribute("ptId", ptProfile.getId());
        }
        req.setAttribute("u", u);
        req.setAttribute("ptProfile", ptProfile);
        req.getRequestDispatcher("/WEB-INF/views/pt/pt-home.jsp").forward(req, resp);
    }
}
