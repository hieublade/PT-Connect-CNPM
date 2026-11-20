package com.ptconnect.controller.pt;

import com.ptconnect.model.dao.EnrollmentDAO;
import com.ptconnect.model.dao.PTProfileDAO;
import com.ptconnect.model.dao.WorkoutDAO;
import com.ptconnect.model.dao.WorkoutPlanDAO;
import com.ptconnect.model.entity.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@WebServlet(name = "ManageWorkoutPlanServlet", urlPatterns = {"/manage-workout-plan"})
public class ManageWorkoutPlanServlet extends HttpServlet {
    private WorkoutDAO workoutDAO;
    private WorkoutPlanDAO workoutPlanDAO;
    private EnrollmentDAO enrollmentDAO;
    private PTProfileDAO ptProfileDAO;

    @Override
    public void init() {
        workoutDAO = new WorkoutDAO();
        workoutPlanDAO = new WorkoutPlanDAO();
        enrollmentDAO = new EnrollmentDAO();
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
        String enrollmentIdParam = req.getParameter("enrollmentId");

        ArrayList<WorkoutPlan> listWorkoutPlan = (enrollmentIdParam != null && !enrollmentIdParam.isBlank())
                ? workoutPlanDAO.getPlansByEnrollment(Long.parseLong(enrollmentIdParam))
                : (ptId != null ? workoutPlanDAO.getPlanByPT(ptId) : new ArrayList<>());

        for (WorkoutPlan wp : listWorkoutPlan) {
            wp.setWorkouts(workoutDAO.getWorkoutByPlan(wp.getId()));
        }

        ArrayList<Enrollment> listEnrollment = ptId != null ? enrollmentDAO.getEnrollment(ptId) : new ArrayList<>();
        Enrollment selectedEnrollment = null;
        if (enrollmentIdParam != null && !enrollmentIdParam.isBlank()) {
            for (Enrollment en : listEnrollment) {
                if (enrollmentIdParam.equals(String.valueOf(en.getId()))) {
                    selectedEnrollment = en;
                    break;
                }
            }
        }
        req.setAttribute("enrollment", selectedEnrollment);
        req.setAttribute("enrollmentIdFilter", enrollmentIdParam);
        req.setAttribute("listWorkoutPlan", listWorkoutPlan);
        req.setAttribute("listEnrollment", listEnrollment);
        req.getRequestDispatcher("/WEB-INF/views/pt/manage-workout-plan.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        if ("DELETE_PLAN".equals(action)) {
            workoutPlanDAO.deletePlan(Long.parseLong(req.getParameter("planId")));
        } else if ("ADD_WORKOUT".equals(action)) {
            Workout w = new Workout();
            w.setPlanId(Long.parseLong(req.getParameter("planId")));
            w.setExerciseName(req.getParameter("exerciseName"));
            w.setTargetMuscle(req.getParameter("targetMuscle"));
            w.setSets(Integer.parseInt(req.getParameter("sets")));
            w.setReps(Integer.parseInt(req.getParameter("reps")));
            String weight = req.getParameter("weightKg");
            w.setWeightKg(weight != null && !weight.isBlank() ? new BigDecimal(weight) : BigDecimal.ZERO);
            w.setRestSecond(Integer.parseInt(req.getParameter("restSeconds")));
            w.setVideoDemoUrl(req.getParameter("videoDemoUrl"));
            workoutDAO.addWorkout(w);
        } else if ("DELETE_WORKOUT".equals(action)) {
            workoutDAO.deleteWorkout(Long.parseLong(req.getParameter("workoutId")));
        } else {
            Long enrollmentId = Long.parseLong(req.getParameter("enrollmentId"));
            LocalDate startDate = LocalDate.parse(req.getParameter("startDate"));
            LocalDate endDate = LocalDate.parse(req.getParameter("endDate"));
            if (startDate.isAfter(endDate)) {
                resp.sendRedirect(req.getContextPath() + "/manage-workout-plan?err=invalid_dates");
                return;
            }
            WorkoutPlan wp = new WorkoutPlan();
            wp.setTitle(req.getParameter("title"));
            wp.setGoal(req.getParameter("goal"));
            wp.setStartDate(startDate);
            wp.setEndDate(endDate);
            wp.setCreatedAt(LocalDateTime.now());
            workoutPlanDAO.createdPlan(wp, enrollmentId);
        }

        resp.sendRedirect(req.getContextPath() + "/manage-workout-plan?msg=ok");
    }
}
