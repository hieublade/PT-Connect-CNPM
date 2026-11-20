package com.ptconnect.model.dao;

import com.ptconnect.model.entity.WorkoutPlan;
import java.sql.*;
import java.util.ArrayList;

public class WorkoutPlanDAO extends DAO {

    public WorkoutPlanDAO() { super(); }
    public WorkoutPlanDAO(Connection conn) { super(conn); }

    public ArrayList<WorkoutPlan> getPlansByEnrollment(Long enrollmentId) {
        ArrayList<WorkoutPlan> list = new ArrayList<>();
        String sql = "SELECT wp.*, stu.full_name AS student_name, c.title AS course_title " +
                     "FROM workout_plans wp " +
                     "JOIN enrollments e ON wp.enrollment_id = e.id " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "JOIN users stu ON e.student_id = stu.id " +
                     "WHERE wp.enrollment_id = ? ORDER BY wp.id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, enrollmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapPlan(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public ArrayList<WorkoutPlan> getPlanByPT(Long ptId) {
        ArrayList<WorkoutPlan> list = new ArrayList<>();
        String sql = "SELECT wp.*, stu.full_name AS student_name, c.title AS course_title " +
                     "FROM workout_plans wp " +
                     "JOIN enrollments e ON wp.enrollment_id = e.id " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "JOIN users stu ON e.student_id = stu.id " +
                     "WHERE c.pt_id = ? ORDER BY wp.id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapPlan(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public boolean createdPlan(WorkoutPlan plan) {
        return createdPlan(plan, plan.getEnrollmentId());
    }

    public boolean createdPlan(WorkoutPlan plan, Long enrollmentId) {
        String sql = "INSERT INTO workout_plans (enrollment_id, title, goal, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, enrollmentId);
                ps.setString(2, plan.getTitle());
                ps.setString(3, plan.getGoal());
                ps.setDate(4, Date.valueOf(plan.getStartDate()));
                ps.setDate(5, Date.valueOf(plan.getEndDate()));
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean deletePlan(Long planId) {
        String delWorkouts = "DELETE FROM workouts WHERE plan_id = ?";
        String delPlan = "DELETE FROM workout_plans WHERE id = ?";
        Connection c = null;
        boolean ownConn = (this.conn == null);
        try {
            c = getConnection();
            if (ownConn) c.setAutoCommit(false);
            try (PreparedStatement ps1 = c.prepareStatement(delWorkouts)) {
                ps1.setLong(1, planId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement(delPlan)) {
                ps2.setLong(1, planId);
                int rows = ps2.executeUpdate();
                if (ownConn) c.commit();
                return rows > 0;
            }
        } catch (SQLException e) {
            if (ownConn && c != null) {
                try { c.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (ownConn && c != null) {
                try { c.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            closeIfOwned(c);
        }
        return false;
    }

    private WorkoutPlan mapPlan(ResultSet rs) throws SQLException {
        WorkoutPlan wp = new WorkoutPlan();
        wp.setId(rs.getLong("id"));
        wp.setEnrollmentId(rs.getLong("enrollment_id"));
        wp.setTitle(rs.getString("title"));
        wp.setGoal(rs.getString("goal"));
        wp.setStudentName(rs.getString("student_name"));
        wp.setCourseTitle(rs.getString("course_title"));
        Date sd = rs.getDate("start_date");
        if (sd != null) wp.setStartDate(sd.toLocalDate());
        Date ed = rs.getDate("end_date");
        if (ed != null) wp.setEndDate(ed.toLocalDate());
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) wp.setCreatedAt(ts.toLocalDateTime());
        return wp;
    }
}
