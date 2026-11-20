package com.ptconnect.model.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAOTest {
    protected Connection testConn;

    @BeforeEach
    public void setUpConnection() throws SQLException {
        testConn = DBContext.getConnection();
        testConn.setAutoCommit(false);
    }

    @AfterEach
    public void tearDownConnection() {
        if (testConn != null) {
            try {
                testConn.rollback();
                testConn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
