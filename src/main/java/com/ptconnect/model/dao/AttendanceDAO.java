package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Attendance;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttendanceDAO extends DAO {

    private static volatile Boolean hasOtpColumns = null;
    private static final Map<Long, OtpFallback> OTP_FALLBACK_STORE = new ConcurrentHashMap<>();

    private static class OtpFallback {
        final String code;
        final LocalDateTime expiresAt;
        OtpFallback(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    public AttendanceDAO() { super(); }
    public AttendanceDAO(Connection conn) { super(conn); }

    private boolean supportsOtpColumns(Connection c) {
        if (hasOtpColumns != null) return hasOtpColumns;
        try (ResultSet rs = c.getMetaData().getColumns(null, null, "attendance", "otp_code")) {
            hasOtpColumns = rs.next();
        } catch (SQLException e) {
            hasOtpColumns = false;
        }
        return hasOtpColumns;
    }

    public Attendance getByBookingId(Long bookingId) {
        String sql = "SELECT * FROM attendance WHERE booking_id = ?";
        Connection c = null;
        try {
            c = getConnection();
            boolean otpCol = supportsOtpColumns(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Attendance a = new Attendance();
                        a.setId(rs.getLong("id"));
                        a.setBookingId(rs.getLong("booking_id"));
                        Timestamp ci = rs.getTimestamp("check_in_time");
                        if (ci != null) a.setCheckInTime(ci.toLocalDateTime());
                        Timestamp co = rs.getTimestamp("check_out_time");
                        if (co != null) a.setCheckOutTime(co.toLocalDateTime());
                        a.setVerificationMethod(rs.getString("verification_method"));
                        a.setStudentConfirmed(rs.getBoolean("student_confirmed"));
                        a.setPtConfirmed(rs.getBoolean("pt_confirmed"));
                        a.setStatus(rs.getString("status"));
                        if (otpCol) {
                            a.setOtpCode(rs.getString("otp_code"));
                            Timestamp exp = rs.getTimestamp("otp_expires_at");
                            if (exp != null) a.setOtpExpiresAt(exp.toLocalDateTime());
                        } else {
                            OtpFallback fb = OTP_FALLBACK_STORE.get(bookingId);
                            if (fb != null) {
                                a.setOtpCode(fb.code);
                                a.setOtpExpiresAt(fb.expiresAt);
                            }
                        }
                        return a;
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

    public boolean openOtp(Long bookingId, String otpCode, LocalDateTime expiresAt) {
        Connection c = null;
        try {
            c = getConnection();
            boolean otpCol = supportsOtpColumns(c);
            String sql = otpCol
                    ? "INSERT INTO attendance (booking_id, check_in_time, verification_method, otp_code, otp_expires_at, student_confirmed, pt_confirmed, status) " +
                      "VALUES (?, NOW(), 'OTP', ?, ?, false, false, 'PRESENT') " +
                      "ON DUPLICATE KEY UPDATE check_in_time = COALESCE(check_in_time, NOW()), otp_code = VALUES(otp_code), otp_expires_at = VALUES(otp_expires_at)"
                    : "INSERT INTO attendance (booking_id, check_in_time, verification_method, student_confirmed, pt_confirmed, status) " +
                      "VALUES (?, NOW(), 'OTP', false, false, 'PRESENT') " +
                      "ON DUPLICATE KEY UPDATE check_in_time = COALESCE(check_in_time, NOW())";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                if (otpCol) {
                    ps.setString(2, otpCode);
                    ps.setTimestamp(3, Timestamp.valueOf(expiresAt));
                }
                boolean ok = ps.executeUpdate() > 0;
                if (ok && !otpCol) {
                    OTP_FALLBACK_STORE.put(bookingId, new OtpFallback(otpCode, expiresAt));
                }
                return ok;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean studentConfirmOtp(Long bookingId, String inputOtp) {
        Attendance a = getByBookingId(bookingId);
        if (a == null || a.getOtpCode() == null || !a.getOtpCode().equals(inputOtp)) {
            return false;
        }
        if (a.getOtpExpiresAt() != null && LocalDateTime.now().isAfter(a.getOtpExpiresAt())) {
            return false;
        }
        String sql = "UPDATE attendance SET student_confirmed = true WHERE booking_id = ?";
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

    public boolean ptConfirm(Long attendanceId) {
        String sql = "UPDATE attendance SET pt_confirmed = true, check_out_time = NOW() WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, attendanceId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean ptConfirmByBooking(Long bookingId) {
        Connection c = null;
        try {
            c = getConnection();
            boolean otpCol = supportsOtpColumns(c);
            String sql = otpCol
                    ? "UPDATE attendance SET pt_confirmed = true, check_out_time = NOW(), otp_code = NULL WHERE booking_id = ?"
                    : "UPDATE attendance SET pt_confirmed = true, check_out_time = NOW() WHERE booking_id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, bookingId);
                if (ps.executeUpdate() > 0) {
                    OTP_FALLBACK_STORE.remove(bookingId);
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

    public boolean markAbsent(Long bookingId) {
        String sql = "INSERT INTO attendance (booking_id, check_in_time, verification_method, student_confirmed, pt_confirmed, status) " +
                     "VALUES (?, NOW(), 'OTP', false, true, 'ABSENT') " +
                     "ON DUPLICATE KEY UPDATE pt_confirmed = true, status = 'ABSENT'";
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
}
