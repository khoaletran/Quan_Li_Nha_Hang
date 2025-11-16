package dao;

import connectDB.connectDB;
import entity.KhuVuc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuVucDAO {

    // ==============================
    // MAP OBJECT
    // ==============================
    private static KhuVuc map(ResultSet rs) throws SQLException {
        return new KhuVuc(
                rs.getString("maKhuVuc"),
                rs.getString("tenKhuVuc")
        );
    }

    // ==============================
    // GET ALL
    // ==============================
    public static List<KhuVuc> getAll() {
        List<KhuVuc> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhuVuc ORDER BY maKhuVuc";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) ds.add(map(rs));

        } catch (SQLException e) {
            System.err.println("Lỗi KhuVucDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }

    // ==============================
    // GET BY ID
    // ==============================
    public static KhuVuc getById(String maKhuVuc) {
        String sql = "SELECT * FROM KhuVuc WHERE maKhuVuc = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKhuVuc);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("Lỗi KhuVucDAO.getById(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // GET BY NAME
    // ==============================
    public static KhuVuc getByName(String tenKhuVuc) {
        String sql = "SELECT * FROM KhuVuc WHERE tenKhuVuc = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenKhuVuc);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("Lỗi KhuVucDAO.getByName(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // INSERT
    // ==============================
    public boolean insert(KhuVuc kv) {
        String sql = "INSERT INTO KhuVuc(maKhuVuc, tenKhuVuc) VALUES (?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kv.getMaKhuVuc());
            ps.setString(2, kv.getTenKhuVuc());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi KhuVucDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public boolean update(KhuVuc kv) {
        String sql = "UPDATE KhuVuc SET tenKhuVuc = ? WHERE maKhuVuc = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kv.getTenKhuVuc());
            ps.setString(2, kv.getMaKhuVuc());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi KhuVucDAO.update(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public boolean delete(String maKhuVuc) {
        String sql = "DELETE FROM KhuVuc WHERE maKhuVuc = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKhuVuc);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi KhuVucDAO.delete(): " + e.getMessage());
            return false;
        }
    }
}
