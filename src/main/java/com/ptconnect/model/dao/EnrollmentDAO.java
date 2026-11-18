package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Enrollment;
import com.ptconnect.util.PdfUtil;
import java.sql.*;
import java.util.ArrayList;

public class EnrollmentDAO extends DAO {

    public EnrollmentDAO() { super(); }
    public EnrollmentDAO(Connection conn) { super(conn); }

    public ArrayList<Enrollment> getEnrollments(Long studentId) {
        ArrayList<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.title AS course_title, c.pt_id, ptu.full_name AS pt_name " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "JOIN pt_profiles p ON c.pt_id = p.id " +
                     "JOIN users ptu ON p.user_id = ptu.id " +
                     "WHERE e.student_id = ? ORDER BY e.id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public ArrayList<Enrollment> getEnrollment(Long ptId) {
        ArrayList<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.title AS course_title, c.pt_id, stu.full_name AS student_name, ptu.full_name AS pt_name " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "JOIN users stu ON e.student_id = stu.id " +
                     "JOIN pt_profiles p ON c.pt_id = p.id " +
                     "JOIN users ptu ON p.user_id = ptu.id " +
                     "WHERE c.pt_id = ? AND e.status = 'ACTIVE' ORDER BY e.id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Enrollment en = mapEnrollment(rs);
                        en.setStudentName(rs.getString("student_name"));
                        list.add(en);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public ArrayList<Enrollment> getActiveEnrollment(Long studentId) {
        ArrayList<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.title AS course_title, c.pt_id, ptu.full_name AS pt_name " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "JOIN pt_profiles p ON c.pt_id = p.id " +
                     "JOIN users ptu ON p.user_id = ptu.id " +
                     "WHERE e.student_id = ? AND e.status = 'ACTIVE' AND e.remaining_sessions > 0 AND e.end_date >= CURDATE() " +
                     "ORDER BY e.id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public boolean hasActiveEnrollment(Long studentId, Long courseId) {
        String sql = "SELECT 1 FROM enrollments WHERE student_id = ? AND course_id = ? AND status = 'ACTIVE' AND remaining_sessions > 0 AND end_date >= CURDATE() LIMIT 1";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, studentId);
                ps.setLong(2, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public Long createEnrollmentAndReturnId(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, course_id, training_location, final_price, total_sessions, remaining_sessions, start_date, end_date, contract_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, enrollment.getStudentId());
                ps.setLong(2, enrollment.getCourseId());
                ps.setString(3, enrollment.getTrainingLocation());
                ps.setBigDecimal(4, enrollment.getFinalPrice());
                ps.setInt(5, enrollment.getTotalSessions());
                ps.setInt(6, enrollment.getRemainingSessions());
                ps.setDate(7, Date.valueOf(enrollment.getStartDate()));
                ps.setDate(8, Date.valueOf(enrollment.getEndDate()));
                ps.setString(9, enrollment.getContractUrl());
                ps.setString(10, enrollment.getStatus() != null ? enrollment.getStatus() : "ACTIVE");
                if (ps.executeUpdate() > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            long id = keys.getLong(1);
                            enrollment.setId(id);
                            return id;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return null;
    }

    public boolean createEnrollment(Enrollment enrollment) {
        return createEnrollmentAndReturnId(enrollment) != null;
    }

    public boolean consumeSession(Long enrollmentId) {
        String sql = "UPDATE enrollments SET remaining_sessions = remaining_sessions - 1 WHERE id = ? AND remaining_sessions > 0";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, enrollmentId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean refundSession(Long enrollmentId) {
        String sql = "UPDATE enrollments SET remaining_sessions = LEAST(total_sessions, remaining_sessions + 1) WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, enrollmentId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public byte[] generateContractPdf(Long enrollmentId) {
        String sql = "SELECT student_id FROM enrollments WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, enrollmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return generateContractPdf(enrollmentId, rs.getLong("student_id"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return new byte[0];
    }

    public byte[] generateContractPdf(Long enrollmentId, Long studentId) {
        String sql = "SELECT e.*, c.title AS course_title, stu.full_name AS student_name, ptu.full_name AS pt_name " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "JOIN users stu ON e.student_id = stu.id " +
                     "JOIN pt_profiles p ON c.pt_id = p.id " +
                     "JOIN users ptu ON p.user_id = ptu.id " +
                     "WHERE e.id = ? AND e.student_id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, enrollmentId);
                ps.setLong(2, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return PdfUtil.generateContractPdf(
                                enrollmentId,
                                rs.getString("student_name"),
                                rs.getString("pt_name"),
                                rs.getString("course_title"),
                                rs.getString("training_location"),
                                rs.getInt("total_sessions"),
                                rs.getBigDecimal("final_price"),
                                rs.getDate("start_date").toLocalDate(),
                                rs.getDate("end_date").toLocalDate()
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return new byte[0];
    }

    private Enrollment mapEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setId(rs.getLong("id"));
        e.setStudentId(rs.getLong("student_id"));
        e.setCourseId(rs.getLong("course_id"));
        e.setPtId(rs.getLong("pt_id"));
        e.setCourseTitle(rs.getString("course_title"));
        e.setPtName(rs.getString("pt_name"));
        e.setTrainingLocation(rs.getString("training_location"));
        e.setFinalPrice(rs.getBigDecimal("final_price"));
        e.setTotalSessions(rs.getInt("total_sessions"));
        e.setRemainingSessions(rs.getInt("remaining_sessions"));
        Date sd = rs.getDate("start_date");
        if (sd != null) e.setStartDate(sd.toLocalDate());
        Date ed = rs.getDate("end_date");
        if (ed != null) e.setEndDate(ed.toLocalDate());
        e.setContractUrl(rs.getString("contract_url"));
        e.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) e.setCreatedAt(ts.toLocalDateTime());
        return e;
    }
}
