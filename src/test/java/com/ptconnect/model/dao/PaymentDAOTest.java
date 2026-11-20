package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentDAOTest extends BaseDAOTest {
    private PaymentDAO paymentDAO;

    @BeforeEach
    public void initDAO() {
        paymentDAO = new PaymentDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy lịch sử giao dịch của ví ID = 2")
    public void testGetPayments() {
        ArrayList<Payment> list = paymentDAO.getPayments(2L);
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("TC02 - Tính tổng doanh thu hệ thống")
    public void testSumRevenue() {
        BigDecimal revenue = paymentDAO.sumRevenue();
        assertNotNull(revenue);
        assertTrue(revenue.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("TC03 - Tạo mới giao dịch thanh toán thành công")
    public void testCreatePayment() {
        Payment p = new Payment();
        p.setWalletId(1L);
        p.setEnrollmentId(null);
        p.setAmount(new BigDecimal("200000.00"));
        p.setPaymentType("TOPUP");
        p.setPaymentMethod("BANK_TRANSFER");
        p.setStatus("SUCCESS");
        p.setTransactionRef("TXN_TEST_" + System.currentTimeMillis());

        boolean ok = paymentDAO.createPayment(p);
        assertTrue(ok);
    }
}
