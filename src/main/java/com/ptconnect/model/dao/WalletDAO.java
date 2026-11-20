package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Wallet;
import java.math.BigDecimal;
import java.sql.*;

public class WalletDAO extends DAO {

    public WalletDAO() { super(); }
    public WalletDAO(Connection conn) { super(conn); }

    public Wallet getWallet(Long userId) {
        String sql = "SELECT * FROM wallets WHERE user_id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Wallet w = new Wallet();
                        w.setId(rs.getLong("id"));
                        w.setUserId(rs.getLong("user_id"));
                        w.setBalance(rs.getBigDecimal("balance"));
                        w.setLockedBalance(rs.getBigDecimal("locked_balance"));
                        w.setCurrency(rs.getString("currency"));
                        w.setStatus(rs.getString("status"));
                        Timestamp up = rs.getTimestamp("updated_at");
                        if (up != null) w.setUpdatedAt(up.toLocalDateTime());
                        return w;
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

    public BigDecimal getWalletBalance(Long userId) {
        Wallet w = getWallet(userId);
        return w != null ? w.getBalance() : BigDecimal.ZERO;
    }

    public boolean createWallet(Long userId) {
        String sql = "INSERT INTO wallets (user_id, balance, locked_balance, currency, status) VALUES (?, 0.00, 0.00, 'VND', 'ACTIVE')";
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

    public boolean credit(Long walletId, BigDecimal amount) {
        return getCredit(walletId, amount);
    }

    public boolean getCredit(Long walletId, BigDecimal amount) {
        String sql = "UPDATE wallets SET balance = balance + ?, updated_at = NOW() WHERE id = ? AND status = 'ACTIVE'";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setBigDecimal(1, amount);
                ps.setLong(2, walletId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean debit(Long walletId, BigDecimal amount) {
        String sql = "UPDATE wallets SET balance = balance - ?, updated_at = NOW() WHERE id = ? AND status = 'ACTIVE' AND balance >= ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setBigDecimal(1, amount);
                ps.setLong(2, walletId);
                ps.setBigDecimal(3, amount);
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
