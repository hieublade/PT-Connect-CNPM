package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class WalletDAOTest extends BaseDAOTest {
    private WalletDAO walletDAO;

    @BeforeEach
    public void initDAO() {
        walletDAO = new WalletDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Lấy thông tin ví của User ID = 1")
    public void testGetWallet() {
        Wallet w = walletDAO.getWallet(1L);
        assertNotNull(w);
        assertEquals("ACTIVE", w.getStatus());
        assertTrue(w.getBalance().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("TC02 - Lấy số dư ví của User ID = 1")
    public void testGetWalletBalance() {
        BigDecimal balance = walletDAO.getWalletBalance(1L);
        assertNotNull(balance);
        assertEquals(0, balance.compareTo(new BigDecimal("5000000.00")));
    }

    @Test
    @DisplayName("TC03 - Nạp tiền vào ví (credit) thành công")
    public void testCredit() {
        Wallet w = walletDAO.getWallet(1L);
        BigDecimal addAmount = new BigDecimal("100000.00");

        boolean ok = walletDAO.credit(w.getId(), addAmount);
        assertTrue(ok);

        BigDecimal newBalance = walletDAO.getWalletBalance(1L);
        assertEquals(0, newBalance.compareTo(new BigDecimal("5100000.00")));
    }
}
