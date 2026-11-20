package com.ptconnect.model.dao;

import com.ptconnect.model.entity.WorkoutPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutPlanDAOTest extends BaseDAOTest {
    private WorkoutPlanDAO planDAO;

    @BeforeEach
    public void initDAO() {
        planDAO = new WorkoutPlanDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy giáo án của hợp đồng không tồn tại")
    public void testGetPlansNotFound() {
        ArrayList<WorkoutPlan> plans = planDAO.getPlansByEnrollment(-999L);
        assertNotNull(plans);
        assertTrue(plans.isEmpty());
    }

    @Test
    @DisplayName("TC02 - Tạo mới giáo án huấn luyện cho Enrollment 1")
    public void testCreatedPlan() {
        WorkoutPlan p = new WorkoutPlan();
        p.setEnrollmentId(1L);
        p.setTitle("Giáo án chuẩn bị thi đấu");
        p.setGoal("Tăng cơ giảm mỡ chuẩn bị thi đấu");
        p.setStartDate(LocalDate.now());
        p.setEndDate(LocalDate.now().plusDays(30));

        boolean ok = planDAO.createdPlan(p);
        assertTrue(ok);
    }
}
