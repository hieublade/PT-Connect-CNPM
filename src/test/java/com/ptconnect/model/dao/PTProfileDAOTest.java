package com.ptconnect.model.dao;

import com.ptconnect.model.entity.PTProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PTProfileDAOTest extends BaseDAOTest {
    private PTProfileDAO ptProfileDAO;

    @BeforeEach
    public void initDAO() {
        ptProfileDAO = new PTProfileDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy hồ sơ PT theo PT ID hợp lệ (ID = 1)")
    public void testGetProfileSuccess() {
        PTProfile profile = ptProfileDAO.getProfile(1L);
        assertNotNull(profile);
        assertEquals(3L, profile.getUserId());
        assertEquals("VERIFIED", profile.getVerificationStatus());
    }

    @Test
    @DisplayName("TC02 - Lấy hồ sơ PT theo ID không tồn tại (-99)")
    public void testGetProfileNotFound() {
        PTProfile profile = ptProfileDAO.getProfile(-99L);
        assertNull(profile);
    }

    @Test
    @DisplayName("TC03 - Lấy hồ sơ PT theo UserID (UserId = 3)")
    public void testGetProfileByUserId() {
        PTProfile profile = ptProfileDAO.getProfileByUserId(3L);
        assertNotNull(profile);
        assertEquals(1L, profile.getId());
    }

    @Test
    @DisplayName("TC04 - Lấy danh sách các PT đang hoạt động")
    public void testGetActivePT() {
        ArrayList<PTProfile> list = ptProfileDAO.getActivePT();
        assertNotNull(list);
        assertFalse(list.isEmpty(), "Danh sách PT hoạt động phải có dữ liệu");
    }

    @Test
    @DisplayName("TC05 - Tìm kiếm PT theo từ khóa có kết quả")
    public void testSearchPTFound() {
        ArrayList<PTProfile> list = ptProfileDAO.searchPT("Hoang");
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("TC06 - Tìm kiếm PT theo từ khóa không tồn tại")
    public void testSearchPTNotFound() {
        ArrayList<PTProfile> list = ptProfileDAO.searchPT("TuKhoaKhongTonTai_9999");
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}
