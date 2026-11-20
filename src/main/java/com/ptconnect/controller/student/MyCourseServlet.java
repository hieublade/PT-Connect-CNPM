package com.ptconnect.controller.student;

import com.ptconnect.model.dao.EnrollmentDAO;
import com.ptconnect.model.dao.WorkoutDAO;
import com.ptconnect.model.dao.WorkoutPlanDAO;
import com.ptconnect.model.entity.Enrollment;
import com.ptconnect.model.entity.User;
import com.ptconnect.model.entity.Workout;
import com.ptconnect.model.entity.WorkoutPlan;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "MyCourseServlet", urlPatterns = {"/my-course"})
public class MyCourseServlet extends HttpServlet {
    private EnrollmentDAO enrollmentDAO;
    private WorkoutPlanDAO workoutPlanDAO;
    private WorkoutDAO workoutDAO;

    @Override
    public void init() {
        enrollmentDAO = new EnrollmentDAO();
        workoutPlanDAO = new WorkoutPlanDAO();
        workoutDAO = new WorkoutDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        ArrayList<Enrollment> listEnrollment = enrollmentDAO.getEnrollments(u.getId());
        ArrayList<Workout> listWorkout = new ArrayList<>();
        String planIdParam = req.getParameter("planId");

        for (Enrollment en : listEnrollment) {
            ArrayList<WorkoutPlan> plans = workoutPlanDAO.getPlansByEnrollment(en.getId());
            for (WorkoutPlan wp : plans) {
                ArrayList<Workout> workouts = workoutDAO.getWorkoutByPlan(wp.getId());
                wp.setWorkouts(workouts);
                if (planIdParam != null && planIdParam.equals(String.valueOf(wp.getId()))) {
                    listWorkout = workouts;
                } else if (listWorkout.isEmpty()) {
                    listWorkout = workouts;
                }
            }
            en.setWorkoutPlans(plans);
        }

        req.setAttribute("listEnrollment", listEnrollment);
        req.setAttribute("listWorkout", listWorkout);
        req.getRequestDispatcher("/WEB-INF/views/student/my-course.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = (User) req.getSession().getAttribute("currentUser");
        Long enrollmentId = Long.parseLong(req.getParameter("enrollmentId"));
        byte[] pdfBytes = enrollmentDAO.generateContractPdf(enrollmentId, u.getId());

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"HopDong_PTConnect_" + enrollmentId + ".pdf\"");
        resp.setContentLength(pdfBytes.length);
        resp.getOutputStream().write(pdfBytes);
    }
}
