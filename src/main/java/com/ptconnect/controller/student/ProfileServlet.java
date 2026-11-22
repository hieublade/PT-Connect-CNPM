package com.ptconnect.controller.student;

import com.ptconnect.model.dao.UserDAO;
import com.ptconnect.model.entity.User;
import com.ptconnect.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;

@WebServlet(name = "ProfileServlet", urlPatterns = { "/profile" })
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ProfileServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = (User) req.getSession().getAttribute("currentUser");
        User u = userDAO.getProfile(sessionUser.getId());
        req.getSession().setAttribute("currentUser", u);

        req.setAttribute("u", u);
        req.setAttribute("avatarURL", u != null ? u.getAvatarUrl() : "");
        req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = (User) req.getSession().getAttribute("currentUser");
        String action = req.getParameter("action");
        String avatarUrl = null;

        if ("UPLOAD_FILE".equals(action)) {
            Part filePart = req.getPart("avatarFile");
            avatarUrl = FileUploadUtil.saveUploadedFile(filePart, getServletContext(), "avatars");
        } else {
            String inputUrl = req.getParameter("avatarUrl");
            if (inputUrl != null && (inputUrl.startsWith("http://") || inputUrl.startsWith("https://"))) {
                avatarUrl = inputUrl.trim();
            }
        }

        if (avatarUrl != null && !avatarUrl.isBlank()) {
            userDAO.updateAvatarUrl(sessionUser.getId(), avatarUrl);
        }
        resp.sendRedirect(req.getContextPath() + "/profile?updated=1");
    }
}
