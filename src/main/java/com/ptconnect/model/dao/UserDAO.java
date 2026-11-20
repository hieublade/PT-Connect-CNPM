package com.ptconnect.model.dao;

import com.ptconnect.model.entity.User;
import com.ptconnect.util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;

public class UserDAO extends DAO {

    public UserDAO() { super(); }
    public UserDAO(Connection conn) { super(conn); }

    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User u = mapUser(rs);
                        if (PasswordUtil.verify(password, u.getPasswordHash())) {
                            return u;
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

    public Long register(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, phone, role, status, avatar_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getFullName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPasswordHash());
                ps.setString(4, user.getPhone());
                ps.setString(5, user.getRole());
                ps.setString(6, user.getStatus());
                ps.setString(7, user.getAvatarUrl());
                if (ps.executeUpdate() > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            long id = keys.getLong(1);
                            user.setId(id);
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

    public User getProfile(Long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return null;
    }

    public boolean updateAvatarUrl(Long userId, String avatarUrl) {
        String sql = "UPDATE users SET avatar_url = ?, updated_at = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, avatarUrl);
                ps.setLong(2, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public ArrayList<User> getUserByRole(String role) {
        ArrayList<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, role);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public boolean updateStatus(Long userId, String status) {
        String sql = "UPDATE users SET status = ?, updated_at = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setLong(2, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean active(Long ptId) {
        String sql = "UPDATE users SET status = 'ACTIVE', updated_at = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, role);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return 0;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) u.setCreatedAt(c.toLocalDateTime());
        Timestamp up = rs.getTimestamp("updated_at");
        if (up != null) u.setUpdatedAt(up.toLocalDateTime());
        return u;
    }
}
