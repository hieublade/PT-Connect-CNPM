package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Attendance;
import com.ptconnect.model.entity.Booking;
import com.ptconnect.model.entity.Review;
import java.sql.*;
import java.util.ArrayList;

public class BookingDAO extends DAO {

    public BookingDAO() { super(); }
    public BookingDAO(Connection conn) { super(conn); }

    public ArrayList<Booking> getBooking() {
        return getBooking(null, null, null);
    }

    public ArrayList<Booking> getBooking(Long ptId, String status) {
        return getBooking(null, ptId, status);
    }

    public ArrayList<Booking> getBooking(Long studentId, Long ptId, String status) {
        ArrayList<Booking> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT b.*, stu.full_name AS student_name, stu.phone AS student_phone, " +
            "p.id AS pt_profile_id, p.user_id AS pt_user_id, ptu.full_name AS pt_name, " +
            "c.title AS course_title, e.training_location, s.start_time AS slot_start, s.end_time AS slot_end, " +
            "r.id AS rev_id, r.rating AS rev_rating, r.comment AS rev_comment, " +
            "a.id AS att_id, a.student_confirmed AS att_stu, a.pt_confirmed AS att_pt, a.status AS att_status " +
            "FROM bookings b " +
            "JOIN users stu ON b.student_id = stu.id " +
            "JOIN availability_slots s ON b.slot_id = s.id " +
            "JOIN pt_profiles p ON s.pt_id = p.id " +
            "JOIN users ptu ON p.user_id = ptu.id " +
            "LEFT JOIN enrollments e ON b.enrollment_id = e.id " +
            "LEFT JOIN courses c ON e.course_id = c.id " +
            "LEFT JOIN reviews r ON r.booking_id = b.id " +
            "LEFT JOIN attendance a ON a.booking_id = b.id " +
            "WHERE 1=1 "
        );
        if (studentId != null) sql.append("AND b.student_id = ? ");
        if (ptId != null) sql.append("AND s.pt_id = ? ");
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) sql.append("AND b.status = ? ");
        sql.append("ORDER BY b.id DESC");

        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
                int idx = 1;
                if (studentId != null) ps.setLong(idx++, studentId);
                if (ptId != null) ps.setLong(idx++, ptId);
                if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) ps.setString(idx, status);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public Booking getBookingById(Long bookingId, Long studentId) {
        String sql = "SELECT b.*, stu.full_name AS student_name, stu.phone AS student_phone, " +
                     "p.id AS pt_profile_id, p.user_id AS pt_user_id, ptu.full_name AS pt_name, " +
                     "c.title AS course_title, e.training_location, s.start_time AS slot_start, s.end_time AS slot_end, " +
                     "r.id AS rev_id, r.rating AS rev_rating, r.comment AS rev_comment, " +
                     "a.id AS att_id, a.student_confirmed AS att_stu, a.pt_confirmed AS att_pt, a.status AS att_status " +
                     "FROM bookings b " +
                     "JOIN users stu ON b.student_id = stu.id " +
                     "JOIN availability_slots s ON b.slot_id = s.id " +
                     "JOIN pt_profiles p ON s.pt_id = p.id " +
                     "JOIN users ptu ON p.user_id = ptu.id " +
                     "LEFT JOIN enrollments e ON b.enrollment_id = e.id " +
                     "LEFT JOIN courses c ON e.course_id = c.id " +
                     "LEFT JOIN reviews r ON r.booking_id = b.id " +
                     "LEFT JOIN attendance a ON a.booking_id = b.id " +
                     "WHERE b.id = ? AND b.student_id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                ps.setLong(2, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapBooking(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return null;
    }

    public Booking getBookingByIdForPT(Long bookingId, Long ptId) {
        String sql = "SELECT b.*, stu.full_name AS student_name, stu.phone AS student_phone, " +
                     "p.id AS pt_profile_id, p.user_id AS pt_user_id, ptu.full_name AS pt_name, " +
                     "c.title AS course_title, e.training_location, s.start_time AS slot_start, s.end_time AS slot_end, " +
                     "r.id AS rev_id, r.rating AS rev_rating, r.comment AS rev_comment, " +
                     "a.id AS att_id, a.student_confirmed AS att_stu, a.pt_confirmed AS att_pt, a.status AS att_status " +
                     "FROM bookings b " +
                     "JOIN users stu ON b.student_id = stu.id " +
                     "JOIN availability_slots s ON b.slot_id = s.id " +
                     "JOIN pt_profiles p ON s.pt_id = p.id " +
                     "JOIN users ptu ON p.user_id = ptu.id " +
                     "LEFT JOIN enrollments e ON b.enrollment_id = e.id " +
                     "LEFT JOIN courses c ON e.course_id = c.id " +
                     "LEFT JOIN reviews r ON r.booking_id = b.id " +
                     "LEFT JOIN attendance a ON a.booking_id = b.id " +
                     "WHERE b.id = ? AND s.pt_id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                ps.setLong(2, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapBooking(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return null;
    }

    public boolean hasConflictingBooking(Long studentId, Long slotId) {
        String sql = "SELECT 1 FROM bookings b " +
                     "JOIN availability_slots s1 ON b.slot_id = s1.id " +
                     "JOIN availability_slots s2 ON s2.id = ? " +
                     "WHERE b.student_id = ? AND b.status IN ('PENDING', 'CONFIRMED') " +
                     "AND s1.start_time < s2.end_time AND s1.end_time > s2.start_time LIMIT 1";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, slotId);
                ps.setLong(2, studentId);
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

    public boolean hasTrialWithPT(Long studentId, Long ptId) {
        String sql = "SELECT 1 FROM bookings b JOIN availability_slots s ON b.slot_id = s.id " +
                     "WHERE b.student_id = ? AND s.pt_id = ? AND b.booking_type = 'TRIAL' AND b.status <> 'CANCELLED' LIMIT 1";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, studentId);
                ps.setLong(2, ptId);
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

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_type, enrollment_id, student_id, slot_id, session_fee, status, student_note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, booking.getBookingType());
                if (booking.getEnrollmentId() != null) ps.setLong(2, booking.getEnrollmentId());
                else ps.setNull(2, Types.BIGINT);
                ps.setLong(3, booking.getStudentId());
                ps.setLong(4, booking.getSlotId());
                ps.setBigDecimal(5, booking.getSessionFee());
                ps.setString(6, booking.getStatus() != null ? booking.getStatus() : "PENDING");
                ps.setString(7, booking.getStudentNote());
                if (ps.executeUpdate() > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) booking.setId(keys.getLong(1));
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean cancelBooking(Long bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED', updated_at = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean confirm(Long bookingId) {
        String sql = "UPDATE bookings SET status = 'CONFIRMED', updated_at = NOW() WHERE id = ? AND status = 'PENDING'";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean confirm(Long bookingId, Long ptId) {
        String sql = "UPDATE bookings b JOIN availability_slots s ON b.slot_id = s.id " +
                     "SET b.status = 'CONFIRMED', b.updated_at = NOW() " +
                     "WHERE b.id = ? AND s.pt_id = ? AND b.status = 'PENDING'";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                ps.setLong(2, ptId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean reject(Long bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED', updated_at = NOW() WHERE id = ? AND status = 'PENDING'";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean reject(Long bookingId, Long ptId) {
        String sql = "UPDATE bookings b JOIN availability_slots s ON b.slot_id = s.id " +
                     "SET b.status = 'CANCELLED', b.updated_at = NOW() " +
                     "WHERE b.id = ? AND s.pt_id = ? AND b.status = 'PENDING'";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                ps.setLong(2, ptId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean completeBooking(Long bookingId) {
        String sql = "UPDATE bookings SET status = 'COMPLETED', updated_at = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getLong("id"));
        b.setStudentId(rs.getLong("student_id"));
        long eid = rs.getLong("enrollment_id");
        if (!rs.wasNull()) b.setEnrollmentId(eid);
        b.setSlotId(rs.getLong("slot_id"));
        b.setPtId(rs.getLong("pt_profile_id"));
        b.setPtUserId(rs.getLong("pt_user_id"));
        b.setBookingType(rs.getString("booking_type"));
        b.setSessionFee(rs.getBigDecimal("session_fee"));
        b.setStatus(rs.getString("status"));
        b.setStudentNote(rs.getString("student_note"));
        b.setStudentName(rs.getString("student_name"));
        b.setStudentPhone(rs.getString("student_phone"));
        b.setPtName(rs.getString("pt_name"));
        b.setCourseTitle(rs.getString("course_title"));
        b.setTrainingLocation(rs.getString("training_location"));
        Timestamp ss = rs.getTimestamp("slot_start");
        if (ss != null) b.setSlotStartTime(ss.toLocalDateTime());
        Timestamp se = rs.getTimestamp("slot_end");
        if (se != null) b.setSlotEndTime(se.toLocalDateTime());
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) b.setCreatedAt(cr.toLocalDateTime());
        Timestamp up = rs.getTimestamp("updated_at");
        if (up != null) b.setUpdatedAt(up.toLocalDateTime());

        long revId = rs.getLong("rev_id");
        if (!rs.wasNull()) {
            Review r = new Review();
            r.setId(revId);
            r.setBookingId(b.getId());
            r.setRating(rs.getInt("rev_rating"));
            r.setComment(rs.getString("rev_comment"));
            b.setReview(r);
        }

        long attId = rs.getLong("att_id");
        if (!rs.wasNull()) {
            Attendance a = new Attendance();
            a.setId(attId);
            a.setBookingId(b.getId());
            a.setStudentConfirmed(rs.getBoolean("att_stu"));
            a.setPtConfirmed(rs.getBoolean("att_pt"));
            a.setStatus(rs.getString("att_status"));
            b.setAttendance(a);
        }
        return b;
    }
}
