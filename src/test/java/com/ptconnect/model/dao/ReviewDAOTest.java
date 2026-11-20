package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewDAOTest extends BaseDAOTest {
    private ReviewDAO reviewDAO;

    @BeforeEach
    public void initDAO() {
        reviewDAO = new ReviewDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy danh sách đánh giá của PT ID = 1")
    public void testGetReviewByPT() {
        ArrayList<Review> reviews = reviewDAO.getReview(1L);
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
        assertEquals(5, reviews.get(0).getRating());
    }

    @Test
    @DisplayName("TC02 - Kiểm tra Booking ID = 1 đã được đánh giá chưa")
    public void testExistsForBooking() {
        boolean exists = reviewDAO.existsForBooking(1L);
        assertTrue(exists, "Booking 1 đã có review trong init.sql");
    }

    @Test
    @DisplayName("TC03 - Kiểm tra Booking chưa tồn tại đánh giá")
    public void testNotExistsForBooking() {
        boolean exists = reviewDAO.existsForBooking(2L);
        assertFalse(exists);
    }
}
