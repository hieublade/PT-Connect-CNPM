package com.ptconnect.model.dao;

import com.ptconnect.model.entity.AvailabilitySlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AvailabilitySlotDAOTest extends BaseDAOTest {
    private AvailabilitySlotDAO slotDAO;

    @BeforeEach
    public void initDAO() {
        slotDAO = new AvailabilitySlotDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy danh sách khung giờ của PT 1")
    public void testGetSlots() {
        ArrayList<AvailabilitySlot> slots = slotDAO.getSlots(1L);
        assertNotNull(slots);
        assertFalse(slots.isEmpty());
    }

    @Test
    @DisplayName("TC02 - Lấy khung giờ rảnh (AVAILABLE) của PT 1")
    public void testGetAvailableSlots() {
        ArrayList<AvailabilitySlot> slots = slotDAO.getAvailableSlot(1L);
        assertNotNull(slots);
        for (AvailabilitySlot s : slots) {
            assertEquals("AVAILABLE", s.getStatus());
        }
    }

    @Test
    @DisplayName("TC03 - Lấy Slot theo ID hợp lệ (Slot ID = 3)")
    public void testGetSlotById() {
        AvailabilitySlot slot = slotDAO.getSlotById(3L);
        assertNotNull(slot);
        assertEquals("AVAILABLE", slot.getStatus());
    }

    @Test
    @DisplayName("TC04 - Cập nhật trạng thái slot sang BOOKED")
    public void testUpdateSlotStatus() {
        boolean ok = slotDAO.updateSlotStatus(3L, "BOOKED");
        assertTrue(ok);
        AvailabilitySlot slot = slotDAO.getSlotById(3L);
        assertEquals("BOOKED", slot.getStatus());
    }
}
