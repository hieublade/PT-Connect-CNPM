package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Cancellation;
import java.sql.*;
import java.util.ArrayList;

public class CancellationDAO extends DAO {

    public CancellationDAO() { super(); }
    public CancellationDAO(Connection conn) { super(conn); }

    public boolean createCancellation(Cancellation cancel) {
        String sql = "INSERT INTO cancellations (booking_id, cancelled_by_id, cancelled_by_role, reason, hours_before_session, refund_amount, compensation_amount, penalty_amount, evidence_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, cancel.getBookingId());
                if (cancel.getCancelledById() != null) ps.setLong(2, cancel.getCancelledById());
                else ps.setNull(2, Types.BIGINT);
                ps.setString(3, cancel.getCancelledByRole());
                ps.setString(4, cancel.getReason());
                ps.setBigDecimal(5, cancel.getHoursBeforeSession());
                ps.setBigDecimal(6, cancel.getRefundAmount());
                ps.setBigDecimal(7, cancel.getCompensationAmount());
                ps.setBigDecimal(8, cancel.getPenaltyAmount());
                ps.setString(9, cancel.getEvidenceUrl());
                ps.setString(10, cancel.getStatus() != null ? cancel.getStatus() : "PROCESSED");
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public ArrayList<Cancellation> getCancellation() {
        ArrayList<Cancellation> list = new ArrayList<>();
        String sql = "SELECT * FROM cancellations ORDER BY cancelled_at DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cancellation item = new Cancellation();
                    item.setId(rs.getLong("id"));
                    item.setBookingId(rs.getLong("booking_id"));
                    long byId = rs.getLong("cancelled_by_id");
                    if (!rs.wasNull()) item.setCancelledById(byId);
                    item.setCancelledByRole(rs.getString("cancelled_by_role"));
                    item.setReason(rs.getString("reason"));
                    item.setHoursBeforeSession(rs.getBigDecimal("hours_before_session"));
                    item.setRefundAmount(rs.getBigDecimal("refund_amount"));
                    item.setCompensationAmount(rs.getBigDecimal("compensation_amount"));
                    item.setPenaltyAmount(rs.getBigDecimal("penalty_amount"));
                    item.setEvidenceUrl(rs.getString("evidence_url"));
                    item.setStatus(rs.getString("status"));
                    Timestamp ts = rs.getTimestamp("cancelled_at");
                    if (ts != null) item.setCancelledAt(ts.toLocalDateTime());
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }
}
