package dao;

import connectDB.connectDB;
import entity.LoaiMon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiMonDAO {

    // ==============================
    // MAP OBJECT
    // ==============================
    private static LoaiMon map(ResultSet rs) throws SQLException {
        return new LoaiMon(
                rs.getString("maLoaiMon"),
                rs.getString("tenLoaiMon"),
                rs.getString("moTa")
        );
    }

    // ==============================
    // GET ALL
    // ==============================
    public static List<LoaiMon> getAll() {
        List<LoaiMon> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoaiMon ORDER BY maLoaiMon";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) ds.add(map(rs));

        } catch (SQLException e) {
            System.err.println("LoaiMonDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }

    // ==============================
    // GET BY ID
    // ==============================
    public static LoaiMon getByID(String maLoaiMon) {
        String sql = "SELECT * FROM LoaiMon WHERE maLoaiMon = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoaiMon);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("LoaiMonDAO.getByID(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // GET MA LOAI MON BY TEN
    // ==============================
    public static String getMaLoaiMonByTen(String tenLoaiMon) {
        String sql = "SELECT maLoaiMon FROM LoaiMon WHERE tenLoaiMon = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenLoaiMon);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("maLoaiMon");

        } catch (SQLException e) {
            System.err.println("LoaiMonDAO.getMaLoaiMonByTen(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // INSERT
    // ==============================
    public boolean insert(LoaiMon loaiMon) {
        String sql = "INSERT INTO LoaiMon(maLoaiMon, tenLoaiMon, moTa) VALUES (?, ?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loaiMon.getMaLoaiMon());
            ps.setString(2, loaiMon.getTenLoaiMon());
            ps.setString(3, loaiMon.getMoTa());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("LoaiMonDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public boolean update(LoaiMon loaiMon) {
        String sql = "UPDATE LoaiMon SET tenLoaiMon=?, moTa=? WHERE maLoaiMon=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loaiMon.getTenLoaiMon());
            ps.setString(2, loaiMon.getMoTa());
            ps.setString(3, loaiMon.getMaLoaiMon());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("LoaiMonDAO.update(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public boolean delete(String maLoaiMon) {
        String sql = "DELETE FROM LoaiMon WHERE maLoaiMon=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoaiMon);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("LoaiMonDAO.delete(): " + e.getMessage());
            return false;
        }
    }
}
