package com.ptconnect.controller.pt;

import com.ptconnect.model.dao.AvailabilitySlotDAO;
import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.entity.AvailabilitySlot;
import com.ptconnect.model.entity.PTProfile;
import com.ptconnect.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@WebServlet(name = "ManageSlotServlet", urlPatterns = {"/manage-slot"})
public class ManageSlotServlet extends HttpServlet {
    private AvailabilitySlotDAO availabilitySlotDAO;
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        availabilitySlotDAO = new AvailabilitySlotDAO();
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
        ArrayList<AvailabilitySlot> listSlot = ptId != null ? availabilitySlotDAO.getSlots(ptId) : new ArrayList<>();

        req.setAttribute("listSlot", listSlot);
        req.getRequestDispatcher("/WEB-INF/views/pt/manage-slot.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long ptId = resolvePtId(req, u);
        String action = req.getParameter("action");

        if ("DELETE_SLOT".equals(action)) {
            Long slotId = Long.parseLong(req.getParameter("slotId"));
            availabilitySlotDAO.deleteSlot(slotId);
            resp.sendRedirect(req.getContextPath() + "/manage-slot?msg=deleted");
            return;
        }

        if (ptId != null) {
            LocalDate date = LocalDate.parse(req.getParameter("date"));
            LocalTime startTime = LocalTime.parse(req.getParameter("startTime"));
            LocalTime endTime = LocalTime.parse(req.getParameter("endTime"));

            LocalDateTime startDT = LocalDateTime.of(date, startTime);
            LocalDateTime endDT = LocalDateTime.of(date, endTime);

            if (!startDT.isAfter(LocalDateTime.now()) || !endDT.isAfter(startDT)) {
                resp.sendRedirect(req.getContextPath() + "/manage-slot?err=invalid_time");
                return;
            }
            if (availabilitySlotDAO.hasOverlappingSlot(ptId, startDT, endDT)) {
                resp.sendRedirect(req.getContextPath() + "/manage-slot?err=overlap");
                return;
            }

            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setPtId(ptId);
            slot.setStartTime(startDT);
            slot.setEndTime(endDT);
            slot.setStatus("AVAILABLE");
            slot.setCreatedAt(LocalDateTime.now());
            availabilitySlotDAO.createSlot(slot);
        }
        resp.sendRedirect(req.getContextPath() + "/manage-slot?msg=created");
    }
}
