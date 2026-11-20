package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Course;
import java.sql.*;
import java.util.ArrayList;

public class CourseDAO extends DAO {

    public CourseDAO() { super(); }
    public CourseDAO(Connection conn) { super(conn); }

    public ArrayList<Course> getActiveCourse(Long ptId) {
        ArrayList<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE pt_id = ? AND status = 'ACTIVE' ORDER BY id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public ArrayList<Course> getCourseByPT(Long ptId) {
        return getActiveCourse(ptId);
    }

    public ArrayList<Course> getCourses(Long ptId) {
        ArrayList<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE pt_id = ? ORDER BY id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public Course getCourseById(Long courseId) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapCourse(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return null;
    }

    public String checkCourseStatus(Long courseId) {
        Course c = getCourseById(courseId);
        return c != null ? c.getStatus() : null;
    }

    public boolean createCourse(Course course) {
        String sql = "INSERT INTO courses (pt_id, title, description, total_sessions, validity_days, base_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, course.getPtId());
                ps.setString(2, course.getTitle());
                ps.setString(3, course.getDescription());
                ps.setInt(4, course.getTotalSessions());
                ps.setInt(5, course.getValidityDays());
                ps.setBigDecimal(6, course.getBasePrice());
                ps.setString(7, course.getStatus() != null ? course.getStatus() : "ACTIVE");
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET status = ? WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, course.getStatus());
                ps.setLong(2, course.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean deleteCourse(Long courseId) {
        String sql = "DELETE FROM courses WHERE id = ? AND NOT EXISTS (SELECT 1 FROM enrollments WHERE course_id = ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, courseId);
                ps.setLong(2, courseId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getLong("id"));
        c.setPtId(rs.getLong("pt_id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setTotalSessions(rs.getInt("total_sessions"));
        c.setValidityDays(rs.getInt("validity_days"));
        c.setBasePrice(rs.getBigDecimal("base_price"));
        c.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());
        return c;
    }
}
