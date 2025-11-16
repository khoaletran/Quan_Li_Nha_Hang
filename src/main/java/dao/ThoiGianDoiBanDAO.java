package dao;

import connectDB.connectDB;
import entity.ThoiGianDoiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThoiGianDoiBanDAO {

    // ==============================
    // MAP OBJECT
    // ==============================
    private static ThoiGianDoiBan map(ResultSet rs) throws SQLException {
        return new ThoiGianDoiBan(
                rs.getString("maTGDB"),
                rs.getBoolean("loaiDatBan"),
                rs.getInt("thoiGian")
        );
    }

    // ==============================
    // GET ALL
    // ==============================
    public static List<ThoiGianDoiBan> getAll() {
        List<ThoiGianDoiBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM ThoiGianDoiBan ORDER BY maTGDB";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) ds.add(map(rs));

        } catch (SQLException e) {
            System.err.println("ThoiGianDoiBanDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }

    // ==============================
    // GET LATEST (any loaiDatBan)
    // ==============================
    public ThoiGianDoiBan getLatest() {
        String sql = "SELECT TOP 1 * FROM ThoiGianDoiBan ORDER BY maTGDB DESC";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("ThoiGianDoiBanDAO.getLatest(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // GET LATEST BY loaiDatBan
    // ==============================
    public static ThoiGianDoiBan getLatestByLoai(boolean loaiDatBan) {
        String sql = """
            SELECT TOP 1 * 
            FROM ThoiGianDoiBan 
            WHERE loaiDatBan = ? 
            ORDER BY maTGDB DESC
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, loaiDatBan);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("ThoiGianDoiBanDAO.getLatestByLoai(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // INSERT
    // ==============================
    public boolean insert(ThoiGianDoiBan tgdb) {
        String sql = "INSERT INTO ThoiGianDoiBan(maTGDB, loaiDatBan, thoiGian) VALUES (?, ?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tgdb.getMaTGDB());
            ps.setBoolean(2, tgdb.isLoaiDatBan());
            ps.setInt(3, tgdb.getThoiGian());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ThoiGianDoiBanDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public boolean update(ThoiGianDoiBan tgdb) {
        String sql = "UPDATE ThoiGianDoiBan SET loaiDatBan=?, thoiGian=? WHERE maTGDB=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, tgdb.isLoaiDatBan());
            ps.setInt(2, tgdb.getThoiGian());
            ps.setString(3, tgdb.getMaTGDB());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ThoiGianDoiBanDAO.update(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public boolean delete(String maTGDB) {
        String sql = "DELETE FROM ThoiGianDoiBan WHERE maTGDB=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maTGDB);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ThoiGianDoiBanDAO.delete(): " + e.getMessage());
            return false;
        }
    }
}
