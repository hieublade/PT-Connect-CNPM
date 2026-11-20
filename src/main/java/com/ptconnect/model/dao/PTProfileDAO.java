package com.ptconnect.model.dao;

import com.ptconnect.model.entity.PTProfile;
import java.sql.*;
import java.util.ArrayList;

public class PTProfileDAO extends DAO {

    public PTProfileDAO() { super(); }
    public PTProfileDAO(Connection conn) { super(conn); }

    public ArrayList<PTProfile> getActivePT() {
        ArrayList<PTProfile> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name, u.email, u.phone, u.avatar_url, " +
                     "(SELECT MIN(c.base_price) FROM courses c WHERE c.pt_id = p.id AND c.status = 'ACTIVE') AS min_course_price " +
                     "FROM pt_profiles p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.verification_status = 'VERIFIED' AND u.status = 'ACTIVE' " +
                     "ORDER BY p.average_rating DESC, p.total_reviews DESC";
        Connection c = null;
        try {
            c = getConnection();
            PTLocationDAO locDAO = new PTLocationDAO(c);
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PTProfile pt = mapPTProfile(rs);
                    pt.setMinCoursePrice(rs.getBigDecimal("min_course_price"));
                    pt.setLocations(locDAO.getLocations(pt.getId()));
                    list.add(pt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public ArrayList<PTProfile> searchPT(String k) {
        ArrayList<PTProfile> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name, u.email, u.phone, u.avatar_url, " +
                     "(SELECT MIN(c.base_price) FROM courses c WHERE c.pt_id = p.id AND c.status = 'ACTIVE') AS min_course_price " +
                     "FROM pt_profiles p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.verification_status = 'VERIFIED' AND u.status = 'ACTIVE' " +
                     "AND LOWER(u.full_name) LIKE LOWER(?) " +
                     "ORDER BY p.average_rating DESC";
        Connection c = null;
        try {
            c = getConnection();
            PTLocationDAO locDAO = new PTLocationDAO(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, "%" + (k == null ? "" : k.trim()) + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        PTProfile pt = mapPTProfile(rs);
                        pt.setMinCoursePrice(rs.getBigDecimal("min_course_price"));
                        pt.setLocations(locDAO.getLocations(pt.getId()));
                        list.add(pt);
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

    public PTProfile getPTDetail(Long ptId) {
        return getProfile(ptId);
    }

    public PTProfile getProfile(Long ptId) {
        String sql = "SELECT p.*, u.full_name, u.email, u.phone, u.avatar_url " +
                     "FROM pt_profiles p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapPTProfile(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return null;
    }

    public PTProfile getProfileByUserId(Long userId) {
        String sql = "SELECT p.*, u.full_name, u.email, u.phone, u.avatar_url " +
                     "FROM pt_profiles p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.user_id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapPTProfile(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return null;
    }

    public boolean createDefaultProfile(Long userId) {
        String sql = "INSERT INTO pt_profiles (user_id, bio, experience_years, verification_status) VALUES (?, '', 0, 'PENDING')";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean updateProfile(PTProfile profile) {
        String sql = "UPDATE pt_profiles SET bio = ?, experience_years = ?, location_pricing_note = ?, certificate_url = ?, updated_at = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, profile.getBio());
                ps.setInt(2, profile.getExperienceYears());
                ps.setString(3, profile.getLocationPricingNote());
                ps.setString(4, profile.getCertificateUrls());
                ps.setLong(5, profile.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public ArrayList<PTProfile> getPendingPTProfiles() {
        ArrayList<PTProfile> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name, u.email, u.phone, u.avatar_url " +
                     "FROM pt_profiles p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.verification_status = 'PENDING' ORDER BY p.created_at DESC";
        Connection c = null;
        try {
            c = getConnection();
            PTLocationDAO locDAO = new PTLocationDAO(c);
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PTProfile pt = mapPTProfile(rs);
                    pt.setLocations(locDAO.getLocations(pt.getId()));
                    list.add(pt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public boolean approve(Long profileId) {
        String sql = "UPDATE pt_profiles SET verification_status = 'VERIFIED', updated_at = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, profileId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean reject(Long profileId) {
        String sql = "UPDATE pt_profiles SET verification_status = 'REJECTED', updated_at = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, profileId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    private PTProfile mapPTProfile(ResultSet rs) throws SQLException {
        PTProfile pt = new PTProfile();
        pt.setId(rs.getLong("id"));
        pt.setUserId(rs.getLong("user_id"));
        pt.setFullName(rs.getString("full_name"));
        pt.setEmail(rs.getString("email"));
        pt.setPhone(rs.getString("phone"));
        pt.setAvatarUrl(rs.getString("avatar_url"));
        pt.setBio(rs.getString("bio"));
        pt.setExperienceYears(rs.getInt("experience_years"));
        pt.setLocationPricingNote(rs.getString("location_pricing_note"));
        pt.setCertificateUrls(rs.getString("certificate_url"));
        pt.setAverageRating(rs.getBigDecimal("average_rating"));
        pt.setTotalReviews(rs.getInt("total_reviews"));
        pt.setVerificationStatus(rs.getString("verification_status"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) pt.setCreatedAt(c.toLocalDateTime());
        Timestamp up = rs.getTimestamp("updated_at");
        if (up != null) pt.setUpdatedAt(up.toLocalDateTime());
        return pt;
    }
}
