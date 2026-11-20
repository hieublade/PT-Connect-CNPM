package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EnrollmentDAOTest extends BaseDAOTest {
    private EnrollmentDAO enrollmentDAO;

    @BeforeEach
    public void initDAO() {
        enrollmentDAO = new EnrollmentDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy danh sách hợp đồng khóa học của học viên ID = 2")
    public void testGetEnrollments() {
        ArrayList<Enrollment> list = enrollmentDAO.getEnrollments(2L);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals("ACTIVE", list.get(0).getStatus());
    }

    @Test
    @DisplayName("TC02 - Trừ 1 buổi tập của hợp đồng (consumeSession)")
    public void testConsumeSession() {
        boolean ok = enrollmentDAO.consumeSession(1L);
        assertTrue(ok);

        ArrayList<Enrollment> list = enrollmentDAO.getEnrollments(2L);
        assertEquals(34, list.get(0).getRemainingSessions());
    }

    @Test
    @DisplayName("TC03 - Hoàn lại 1 buổi tập của hợp đồng (refundSession)")
    public void testRefundSession() {
        boolean ok = enrollmentDAO.refundSession(1L);
        assertTrue(ok);

        ArrayList<Enrollment> list = enrollmentDAO.getEnrollments(2L);
        assertEquals(36, list.get(0).getRemainingSessions());
    }
}
