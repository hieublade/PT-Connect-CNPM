package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CourseDAOTest extends BaseDAOTest {
    private CourseDAO courseDAO;

    @BeforeEach
    public void initDAO() {
        courseDAO = new CourseDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy thông tin khóa học theo ID = 1")
    public void testGetCourseById() {
        Course course = courseDAO.getCourseById(1L);
        assertNotNull(course);
        assertEquals("Gói Giảm Mỡ Chuyên Sâu 12 Tuần", course.getTitle());
        assertEquals(36, course.getTotalSessions());
    }

    @Test
    @DisplayName("TC02 - Lấy khóa học với ID không tồn tại (-1)")
    public void testGetCourseByIdNotFound() {
        Course course = courseDAO.getCourseById(-1L);
        assertNull(course);
    }

    @Test
    @DisplayName("TC03 - Lấy danh sách khóa học của PT 1")
    public void testGetCoursesByPT() {
        ArrayList<Course> list = courseDAO.getCourses(1L);
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("TC04 - Tạo mới một khóa học thành công")
    public void testCreateCourse() {
        Course c = new Course();
        c.setPtId(1L);
        c.setTitle("Khóa học Test JUnit");
        c.setTotalSessions(12);
        c.setValidityDays(30);
        c.setBasePrice(new BigDecimal("3500000.00"));

        boolean ok = courseDAO.createCourse(c);
        assertTrue(ok, "Tạo khóa học thành công phải trả về true");
    }
}
