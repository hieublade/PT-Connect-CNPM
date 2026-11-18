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

    /**
     * Trả về kết nối dùng chung nếu DAO đang tham gia JDBC Transaction (conn != null),
     * ngược lại lấy kết nối mới từ HikariCP Connection Pool (DBContext).
     */
    public Connection getConnection() throws SQLException {
        if (this.conn != null && !this.conn.isClosed()) {
            return this.conn;
        }
        return DBContext.getConnection();
    }

    /**
     * Chỉ đóng Connection nếu DAO tự mở kết nối riêng (this.conn == null).
     */
    protected void closeIfOwned(Connection c) {
        if (this.conn == null && c != null) {
            try {
                c.close();
            } catch (SQLException ignored) {}
        }
    }
}
