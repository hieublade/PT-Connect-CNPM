package com.ptconnect.model.dao;

import com.ptconnect.model.entity.PTLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PTLocationDAOTest extends BaseDAOTest {
    private PTLocationDAO ptLocationDAO;

    @BeforeEach
    public void initDAO() {
        ptLocationDAO = new PTLocationDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy danh sách địa điểm nhận dạy của PT ID = 1")
    public void testGetLocationsByPT() {
        ArrayList<PTLocation> list = ptLocationDAO.getLocationsByPT(1L);
        assertNotNull(list);
        assertTrue(list.size() >= 2, "PT 1 có ít nhất 2 địa điểm");
    }

    @Test
    @DisplayName("TC02 - Lấy danh sách địa điểm của PT không tồn tại")
    public void testGetLocationsByInvalidPT() {
        ArrayList<PTLocation> list = ptLocationDAO.getLocationsByPT(-99L);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("TC03 - Lấy toàn bộ địa điểm hệ thống")
    public void testGetAllLocations() {
        ArrayList<PTLocation> list = ptLocationDAO.getAllLocations();
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }
}
