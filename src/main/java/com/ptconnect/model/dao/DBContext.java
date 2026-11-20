package com.ptconnect.model.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBContext {
    private static final HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream is = DBContext.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (is != null) {
                    props.load(is);
                }
            }
            String url = firstNonBlank(System.getenv("PTCONNECT_DB_URL"), props.getProperty("db.url"));
            String user = firstNonBlank(System.getenv("PTCONNECT_DB_USER"), props.getProperty("db.username"));
            String pass = System.getenv("PTCONNECT_DB_PASS") != null
                    ? System.getenv("PTCONNECT_DB_PASS")
                    : props.getProperty("db.password", "");
            String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            int poolSize = Integer.parseInt(props.getProperty("db.poolSize", "10"));

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(pass);
            config.setMaximumPoolSize(poolSize);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(10000);
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Không thể khởi tạo HikariCP DataSource: " + e.getMessage());
        }
    }

    private static String firstNonBlank(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
