package com.ptconnect.controller.pt;

import com.ptconnect.model.dao.DBContext;
import com.ptconnect.model.dao.PTLocationDAO;
import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.dao.UserDAO;
import com.ptconnect.model.entity.PTLocation;
import com.ptconnect.model.entity.PTProfile;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "ManagePTProfileServlet", urlPatterns = {"/manage-pt-profile"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ManagePTProfileServlet extends HttpServlet {
    private PTProfileDAO ptProfileDAO;
    private PTLocationDAO ptLocationDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        ptProfileDAO = new PTProfileDAO();
        ptLocationDAO = new PTLocationDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        PTProfile ptProfile = ptProfileDAO.getProfileByUserId(u.getId());
        if (ptProfile != null) {
            req.getSession().setAttribute("ptId", ptProfile.getId());
            ptProfile.setLocations(ptLocationDAO.getLocationsByPT(ptProfile.getId()));
        }
        ArrayList<PTLocation> availableLocations = ptLocationDAO.getAllLocations();

        req.setAttribute("u", userDAO.getProfile(u.getId()));
        req.setAttribute("ptProfile", ptProfile);
        req.setAttribute("availableLocations", availableLocations);
        req.getRequestDispatcher("/WEB-INF/views/pt/manage-pt-profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        String action = req.getParameter("action");

        if ("UPLOAD_AVATAR".equals(action)) {
            Part filePart = req.getPart("avatarFile");
            String url = FileUploadUtil.saveUploadedFile(filePart, getServletContext(), "avatars");
            if (url != null) userDAO.updateAvatarUrl(u.getId(), url);
            resp.sendRedirect(req.getContextPath() + "/manage-pt-profile?msg=avatar");
            return;
        }
        if ("SAVE_AVATAR_URL".equals(action)) {
            String url = req.getParameter("avatarUrl");
            if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                userDAO.updateAvatarUrl(u.getId(), url.trim());
            }
            resp.sendRedirect(req.getContextPath() + "/manage-pt-profile?msg=avatar");
            return;
        }

        PTProfile existing = ptProfileDAO.getProfileByUserId(u.getId());
        if (existing == null) {
            resp.sendRedirect(req.getContextPath() + "/pt-home");
            return;
        }

        String locationsRaw = req.getParameter("locationsText");
        ArrayList<PTLocation> locations = new ArrayList<>();
        if (locationsRaw != null) {
            for (String s : locationsRaw.split(",")) {
                if (!s.trim().isEmpty()) {
                    PTLocation loc = new PTLocation();
                    loc.setPtId(existing.getId());
                    loc.setLocationName(s.trim());
                    locations.add(loc);
                }
            }
        }
        if (locations.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/manage-pt-profile?err=empty_location");
            return;
        }

        PTProfile profile = new PTProfile();
        profile.setId(existing.getId());
        profile.setBio(req.getParameter("bio"));
        profile.setExperienceYears(Integer.parseInt(req.getParameter("experienceYears")));
        profile.setLocationPricingNote(req.getParameter("locationPricingNote"));
        profile.setCertificateUrls(req.getParameter("certificateUrls"));

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            PTProfileDAO txProfile = new PTProfileDAO(conn);
            PTLocationDAO txLoc = new PTLocationDAO(conn);

            if (txProfile.updateProfile(profile) && txLoc.updatePTLocations(existing.getId(), locations)) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            throw new ServletException("Lỗi cập nhật hồ sơ PT", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/manage-pt-profile?msg=saved");
    }
}
