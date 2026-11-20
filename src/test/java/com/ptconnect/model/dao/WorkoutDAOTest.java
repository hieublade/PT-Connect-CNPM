package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Workout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutDAOTest extends BaseDAOTest {
    private WorkoutDAO workoutDAO;

    @BeforeEach
    public void initDAO() {
        workoutDAO = new WorkoutDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy danh sách bài tập của Plan không tồn tại")
    public void testGetWorkoutsNotFound() {
        ArrayList<Workout> list = workoutDAO.getWorkoutByPlan(-999L);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}
