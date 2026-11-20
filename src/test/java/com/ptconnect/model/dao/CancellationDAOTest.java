package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Cancellation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CancellationDAOTest extends BaseDAOTest {
    private CancellationDAO cancellationDAO;

    @BeforeEach
    public void initDAO() {
        cancellationDAO = new CancellationDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy danh sách các ca hủy trong hệ thống")
    public void testGetCancellation() {
        ArrayList<Cancellation> list = cancellationDAO.getCancellation();
        assertNotNull(list);
    }

    @Test
    @DisplayName("TC02 - Ghi nhận biên bản hủy ca tập cho Booking 2")
    public void testCreateCancellation() {
        Cancellation c = new Cancellation();
        c.setBookingId(2L);
        c.setCancelledById(1L);
        c.setCancelledByRole("STUDENT");
        c.setReason("Học viên bận thi học kỳ");
        c.setHoursBeforeSession(new BigDecimal("24.00"));
        c.setRefundAmount(BigDecimal.ZERO);
        c.setPenaltyAmount(BigDecimal.ZERO);
        c.setCompensationAmount(BigDecimal.ZERO);

        boolean ok = cancellationDAO.createCancellation(c);
        assertTrue(ok);
    }
}
