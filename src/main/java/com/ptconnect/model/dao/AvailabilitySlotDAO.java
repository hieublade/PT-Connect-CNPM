package com.ptconnect.model.dao;

import com.ptconnect.model.entity.AvailabilitySlot;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AvailabilitySlotDAO extends DAO {

    public AvailabilitySlotDAO() { super(); }
    public AvailabilitySlotDAO(Connection conn) { super(conn); }

    public ArrayList<AvailabilitySlot> getSlots(Long ptId) {
        ArrayList<AvailabilitySlot> list = new ArrayList<>();
        String sql = "SELECT * FROM availability_slots WHERE pt_id = ? ORDER BY start_time ASC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapSlot(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public ArrayList<AvailabilitySlot> getAvailableSlot(Long ptId) {
        ArrayList<AvailabilitySlot> list = new ArrayList<>();
        String sql = "SELECT * FROM availability_slots WHERE pt_id = ? AND status = 'AVAILABLE' AND start_time > NOW() ORDER BY start_time ASC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapSlot(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public AvailabilitySlot getSlotById(Long slotId) {
        String sql = "SELECT * FROM availability_slots WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, slotId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapSlot(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return null;
    }

    public boolean hasOverlappingSlot(Long ptId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT 1 FROM availability_slots WHERE pt_id = ? AND start_time < ? AND end_time > ? LIMIT 1";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                ps.setTimestamp(2, Timestamp.valueOf(endTime));
                ps.setTimestamp(3, Timestamp.valueOf(startTime));
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

    public boolean createSlot(AvailabilitySlot slot) {
        String sql = "INSERT INTO availability_slots (pt_id, start_time, end_time, status) VALUES (?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, slot.getPtId());
                ps.setTimestamp(2, Timestamp.valueOf(slot.getStartTime()));
                ps.setTimestamp(3, Timestamp.valueOf(slot.getEndTime()));
                ps.setString(4, slot.getStatus() != null ? slot.getStatus() : "AVAILABLE");
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean deleteSlot(Long slotId) {
        String sql = "DELETE FROM availability_slots WHERE id = ? AND status = 'AVAILABLE'";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, slotId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean lockSlot(Long slotId) {
        String sql = "UPDATE availability_slots SET status = 'BOOKED' WHERE id = ? AND status = 'AVAILABLE'";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, slotId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean markAsBooked(Long slotId) {
        return updateSlotStatus(slotId, "BOOKED");
    }

    public boolean releaseSlot(Long slotId) {
        return updateSlotStatus(slotId, "AVAILABLE");
    }

    public boolean updateSlotStatus(Long slotId, String status) {
        String sql = "UPDATE availability_slots SET status = ? WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setLong(2, slotId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    private AvailabilitySlot mapSlot(ResultSet rs) throws SQLException {
        AvailabilitySlot s = new AvailabilitySlot();
        s.setId(rs.getLong("id"));
        s.setPtId(rs.getLong("pt_id"));
        s.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        s.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        s.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    }
}
