package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BookingDAOTest extends BaseDAOTest {
    private BookingDAO bookingDAO;

    @BeforeEach
    public void initDAO() {
        bookingDAO = new BookingDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy danh sách tất cả Booking trong hệ thống")
    public void testGetAllBookings() {
        ArrayList<Booking> list = bookingDAO.getBooking();
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("TC02 - Lấy Booking theo ID và Student ID hợp lệ")
    public void testGetBookingById() {
        Booking b = bookingDAO.getBookingById(1L, 2L);
        assertNotNull(b);
        assertEquals("COMPLETED", b.getStatus());
    }

    @Test
    @DisplayName("TC03 - Lấy Booking với ID không tồn tại (-1)")
    public void testGetBookingNotFound() {
        Booking b = bookingDAO.getBookingById(-1L, 2L);
        assertNull(b);
    }

    @Test
    @DisplayName("TC04 - Hủy ca tập thành công (Booking ID = 2)")
    public void testCancelBooking() {
        boolean ok = bookingDAO.cancelBooking(2L);
        assertTrue(ok);
        Booking b = bookingDAO.getBookingById(2L, 1L);
        assertEquals("CANCELLED", b.getStatus());
    }
}
