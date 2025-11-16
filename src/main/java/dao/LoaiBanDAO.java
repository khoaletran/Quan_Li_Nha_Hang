package dao;

import connectDB.connectDB;
import entity.LoaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiBanDAO {

    // ==============================
    // MAP OBJECT
    // ==============================
    private static LoaiBan map(ResultSet rs) throws SQLException {
        return new LoaiBan(
                rs.getString("maLoaiBan"),
                rs.getInt("soLuong"),
                rs.getString("tenLoaiBan")
        );
    }

    // ==============================
    // GET ALL
    // ==============================
    public static List<LoaiBan> getAll() {
        List<LoaiBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoaiBan ORDER BY maLoaiBan";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) ds.add(map(rs));

        } catch (SQLException e) {
            System.err.println("LoaiBanDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }

    // ==============================
    // GET BY ID
    // ==============================
    public static LoaiBan getById(String maLoaiBan) {
        String sql = "SELECT * FROM LoaiBan WHERE maLoaiBan=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoaiBan);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("LoaiBanDAO.getById(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // GET BY NAME
    // ==============================
    public static LoaiBan getByName(String tenLoaiBan) {
        String sql = "SELECT * FROM LoaiBan WHERE tenLoaiBan=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenLoaiBan);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("LoaiBanDAO.getByName(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // INSERT
    // ==============================
    public boolean insert(LoaiBan lb) {
        String sql = "INSERT INTO LoaiBan(maLoaiBan, tenLoaiBan, soLuong) VALUES (?, ?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lb.getMaLoaiBan());
            ps.setString(2, lb.getTenLoaiBan());
            ps.setInt(3, lb.getSoLuong());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("LoaiBanDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public boolean update(LoaiBan lb) {
        String sql = "UPDATE LoaiBan SET tenLoaiBan=?, soLuong=? WHERE maLoaiBan=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lb.getTenLoaiBan());
            ps.setInt(2, lb.getSoLuong());
            ps.setString(3, lb.getMaLoaiBan());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("LoaiBanDAO.update(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public boolean delete(String maLoaiBan) {
        String sql = "DELETE FROM LoaiBan WHERE maLoaiBan=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoaiBan);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("LoaiBanDAO.delete(): " + e.getMessage());
            return false;
        }
    }
}
