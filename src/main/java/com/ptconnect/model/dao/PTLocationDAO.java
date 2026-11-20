package com.ptconnect.model.dao;

import com.ptconnect.model.entity.PTLocation;
import java.sql.*;
import java.util.ArrayList;

public class PTLocationDAO extends DAO {

    public PTLocationDAO() { super(); }
    public PTLocationDAO(Connection conn) { super(conn); }

    public ArrayList<PTLocation> getLocations(Long ptId) {
        return getLocationsByPT(ptId);
    }

    public ArrayList<PTLocation> getLocationsByPT(Long ptId) {
        ArrayList<PTLocation> list = new ArrayList<>();
        String sql = "SELECT * FROM pt_locations WHERE pt_id = ? ORDER BY id ASC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, ptId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapLocation(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public ArrayList<PTLocation> getAllLocations() {
        ArrayList<PTLocation> list = new ArrayList<>();
        String sql = "SELECT MIN(id) AS id, MIN(pt_id) AS pt_id, location_name FROM pt_locations GROUP BY location_name ORDER BY location_name ASC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapLocation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public boolean updatePTLocations(Long ptId, ArrayList<PTLocation> locations) {
        String delSql = "DELETE FROM pt_locations WHERE pt_id = ?";
        String insSql = "INSERT IGNORE INTO pt_locations (pt_id, location_name) VALUES (?, ?)";
        Connection c = null;
        boolean ownConn = (this.conn == null);
        try {
            c = getConnection();
            if (ownConn) c.setAutoCommit(false);
            try (PreparedStatement del = c.prepareStatement(delSql)) {
                del.setLong(1, ptId);
                del.executeUpdate();
            }
            try (PreparedStatement ins = c.prepareStatement(insSql)) {
                for (PTLocation loc : locations) {
                    if (loc.getLocationName() != null && !loc.getLocationName().isBlank()) {
                        ins.setLong(1, ptId);
                        ins.setString(2, loc.getLocationName().trim());
                        ins.addBatch();
                    }
                }
                ins.executeBatch();
            }
            if (ownConn) c.commit();
            return true;
        } catch (SQLException e) {
            if (ownConn && c != null) {
                try { c.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (ownConn && c != null) {
                try { c.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            closeIfOwned(c);
        }
        return false;
    }

    private PTLocation mapLocation(ResultSet rs) throws SQLException {
        PTLocation l = new PTLocation();
        l.setId(rs.getLong("id"));
        l.setPtId(rs.getLong("pt_id"));
        l.setLocationName(rs.getString("location_name"));
        return l;
    }
}
