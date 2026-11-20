package com.ptconnect.controller.student;

import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.entity.PTProfile;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "DiscoveryPTServlet", urlPatterns = {"/discovery-pt"})
public class DiscoveryPTServlet extends HttpServlet {
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        ptProfileDAO = new PTProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyWord = req.getParameter("keyWord");
        ArrayList<PTProfile> listPT = (keyWord != null && !keyWord.isBlank())
                ? ptProfileDAO.searchPT(keyWord)
                : ptProfileDAO.getActivePT();

        req.setAttribute("keyWord", keyWord);
        req.setAttribute("listPT", listPT);
        req.getRequestDispatcher("/WEB-INF/views/student/discovery-pt.jsp").forward(req, resp);
    }
}
