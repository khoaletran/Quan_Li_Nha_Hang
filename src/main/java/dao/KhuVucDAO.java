package dao;

import connectDB.connectDB;
import entity.KhuVuc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuVucDAO {

    public static List<KhuVuc> getAll() {
        List<KhuVuc> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhuVuc";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new KhuVuc(
                        rs.getString("maKhuVuc"),
                        rs.getString("tenKhuVuc")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public static KhuVuc getById(String maKhuVuc) {
        String sql = "SELECT * FROM KhuVuc WHERE maKhuVuc=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maKhuVuc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new KhuVuc(
                            rs.getString("maKhuVuc"),
                            rs.getString("tenKhuVuc")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static KhuVuc getByName(String tenKhuVuc) {
        String sql = "SELECT * FROM KhuVuc WHERE tenKhuVuc=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, tenKhuVuc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new KhuVuc(
                            rs.getString("maKhuVuc"),
                            rs.getString("tenKhuVuc")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean insert(KhuVuc kv) {
        String sql = "INSERT INTO KhuVuc VALUES (?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, kv.getMaKhuVuc());
            ps.setString(2, kv.getTenKhuVuc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KhuVuc kv) {
        String sql = "UPDATE KhuVuc SET tenKhuVuc=? WHERE maKhuVuc=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, kv.getTenKhuVuc());
            ps.setString(2, kv.getMaKhuVuc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maKhuVuc) {
        String sql = "DELETE FROM KhuVuc WHERE maKhuVuc=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maKhuVuc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
