package dao;

import connectDB.connectDB;
import entity.SuKien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuKienDAO {

    // ==============================
    // MAP OBJECT
    // ==============================
    private static SuKien map(ResultSet rs) throws SQLException {
        return new SuKien(
                rs.getString("maSK"),
                rs.getString("tenSK"),
                rs.getString("moTa"),
                rs.getDouble("gia")
        );
    }

    // ==============================
    // GET ALL
    // ==============================
    public static List<SuKien> getAll() {
        List<SuKien> ds = new ArrayList<>();
        String sql = "SELECT * FROM SuKien ORDER BY maSK";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) ds.add(map(rs));

        } catch (SQLException e) {
            System.err.println("SuKienDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }

    // ==============================
    // INSERT
    // ==============================
    public boolean insert(SuKien sk) {
        String sql = "INSERT INTO SuKien(maSK, tenSK, moTa, gia) VALUES (?, ?, ?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sk.getMaSK());
            ps.setString(2, sk.getTenSK());
            ps.setString(3, sk.getMota());
            ps.setDouble(4, sk.getGia());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("SuKienDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public boolean update(SuKien sk) {
        String sql = "UPDATE SuKien SET tenSK=?, moTa=?, gia=? WHERE maSK=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sk.getTenSK());
            ps.setString(2, sk.getMota());
            ps.setDouble(3, sk.getGia());
            ps.setString(4, sk.getMaSK());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("SuKienDAO.update(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public boolean delete(String maSK) {
        String sql = "DELETE FROM SuKien WHERE maSK=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maSK);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("SuKienDAO.delete(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // GET BY ID
    // ==============================
    public static SuKien getByID(String maSK) {
        String sql = "SELECT * FROM SuKien WHERE maSK = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maSK);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("SuKienDAO.getByID(): " + e.getMessage());
        }

        return null;
    }
}
