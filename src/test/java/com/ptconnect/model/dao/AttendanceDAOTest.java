package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Attendance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AttendanceDAOTest extends BaseDAOTest {
    private AttendanceDAO attendanceDAO;

    @BeforeEach
    public void initDAO() {
        attendanceDAO = new AttendanceDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy thông tin điểm danh của Booking ID = 1")
    public void testGetByBookingId() {
        Attendance a = attendanceDAO.getByBookingId(1L);
        assertNotNull(a);
        assertEquals("PRESENT", a.getStatus());
        assertTrue(Boolean.TRUE.equals(a.getStudentConfirmed()));
        assertTrue(Boolean.TRUE.equals(a.getPtConfirmed()));
    }

    @Test
    @DisplayName("TC02 - Lấy thông tin điểm danh của Booking không tồn tại")
    public void testGetByInvalidBookingId() {
        Attendance a = attendanceDAO.getByBookingId(-1L);
        assertNull(a);
    }
}
