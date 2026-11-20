package com.ptconnect.model.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DAO {
    protected Connection conn;

    public DAO() {
    }

    public DAO(Connection conn) {
        this.conn = conn;
    }

    public Connection getConnection() throws SQLException {
        if (this.conn != null && !this.conn.isClosed()) {
            return this.conn;
        }
        return DBContext.getConnection();
    }

    protected void closeIfOwned(Connection c) {
        if (this.conn == null && c != null) {
            try {
                c.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
