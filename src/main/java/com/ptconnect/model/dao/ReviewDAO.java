package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Review;
import java.sql.*;
import java.util.ArrayList;

public class ReviewDAO extends DAO {

    public ReviewDAO() { super(); }
    public ReviewDAO(Connection conn) { super(conn); }

    public ArrayList<Review> getReview(Long ptId) {
        ArrayList<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS student_name FROM reviews r " +
                     "JOIN bookings b ON r.booking_id = b.id " +
                     "JOIN users u ON b.student_id = u.id " +
                     "JOIN availability_slots s ON b.slot_id = s.id " +
                     "WHERE s.pt_id = ? ORDER BY r.created_at DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Review r = new Review();
                        r.setId(rs.getLong("id"));
                        r.setBookingId(rs.getLong("booking_id"));
                        r.setStudentName(rs.getString("student_name"));
                        r.setRating(rs.getInt("rating"));
                        r.setComment(rs.getString("comment"));
                        Timestamp ts = rs.getTimestamp("created_at");
                        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());
                        list.add(r);
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

    public boolean existsForBooking(Long bookingId) {
        String sql = "SELECT 1 FROM reviews WHERE booking_id = ? LIMIT 1";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
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

    public boolean createReview(Review review) {
        String sql = "INSERT INTO reviews (booking_id, rating, comment) VALUES (?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, review.getBookingId());
                ps.setInt(2, review.getRating());
                ps.setString(3, review.getComment());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }
}
