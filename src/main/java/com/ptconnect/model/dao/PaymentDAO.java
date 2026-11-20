package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Payment;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class PaymentDAO extends DAO {

    public PaymentDAO() { super(); }
    public PaymentDAO(Connection conn) { super(conn); }

    public ArrayList<Payment> getPayments(Long walletId) {
        ArrayList<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE wallet_id = ? ORDER BY created_at DESC, id DESC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, walletId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapPayment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public boolean createPayment(Payment payment) {
        String sql = "INSERT INTO payments (wallet_id, enrollment_id, amount, payment_type, payment_method, status, transaction_ref) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, payment.getWalletId());
                if (payment.getEnrollmentId() != null) ps.setLong(2, payment.getEnrollmentId());
                else ps.setNull(2, Types.BIGINT);
                ps.setBigDecimal(3, payment.getAmount());
                ps.setString(4, payment.getPaymentType());
                ps.setString(5, payment.getPaymentMethod());
                ps.setString(6, payment.getStatus());
                ps.setString(7, payment.getTransactionRef());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public BigDecimal sumRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_type IN ('PURCHASE_PACKAGE', 'BUY_COURSE') AND status = 'SUCCESS'";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return BigDecimal.ZERO;
    }

    public ArrayList<Payment> getPaymentAnalytic() {
        ArrayList<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE payment_type IN ('PURCHASE_PACKAGE', 'BUY_COURSE') AND status = 'SUCCESS' ORDER BY created_at ASC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getLong("id"));
        p.setWalletId(rs.getLong("wallet_id"));
        long eid = rs.getLong("enrollment_id");
        if (!rs.wasNull()) p.setEnrollmentId(eid);
        p.setAmount(rs.getBigDecimal("amount"));
        p.setPaymentType(rs.getString("payment_type"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setStatus(rs.getString("status"));
        p.setTransactionRef(rs.getString("transaction_ref"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) p.setCreatedAt(ts.toLocalDateTime());
        return p;
    }
}
