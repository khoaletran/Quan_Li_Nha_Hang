package dao;

import connectDB.connectDB;
import entity.ThoiGianDoiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThoiGianDoiBanDAO {

    // ==============================
    // LẤY TOÀN BỘ DANH SÁCH THỜI GIAN ĐỢI BÀN
    // ==============================
    public static ArrayList<ThoiGianDoiBan> getAll() {
        ArrayList<ThoiGianDoiBan> ds = new ArrayList<>();
        String sql = "SELECT maTGDB, loaiDatBan, thoiGian FROM ThoiGianDoiBan";

        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ThoiGianDoiBan tgdb = new ThoiGianDoiBan(
                        rs.getString("maTGDB"),
                        rs.getBoolean("loaiDatBan"),
                        rs.getInt("thoiGian")
                );
                ds.add(tgdb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    public ThoiGianDoiBan getLatest() {
        String sql = "SELECT TOP 1 * FROM ThoiGianDoiBan ORDER BY maTGDB DESC";
        ThoiGianDoiBan tgdb = null;

        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                tgdb = new ThoiGianDoiBan(
                        rs.getString("maTGDB"),
                        rs.getBoolean("loaiDatBan"),
                        rs.getInt("thoiGian")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tgdb;
    }

    public ThoiGianDoiBan getLatestByLoai(boolean loaiDatBan) {
        String sql = "SELECT TOP 1 * FROM ThoiGianDoiBan WHERE loaiDatBan=? ORDER BY maTGDB DESC";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, loaiDatBan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ThoiGianDoiBan(
                        rs.getString("maTGDB"),
                        rs.getBoolean("loaiDatBan"),
                        rs.getInt("thoiGian")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    // ==============================
    // THÊM MỚI THỜI GIAN ĐỢI BÀN
    // ==============================
    public boolean insert(ThoiGianDoiBan tgdb) {
        String sql = "INSERT INTO ThoiGianDoiBan (maTGDB, loaiDatBan, thoiGian) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, tgdb.getMaTGDB());
            ps.setBoolean(2, tgdb.isLoaiDatBan());
            ps.setInt(3, tgdb.getThoiGian());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // CẬP NHẬT THỜI GIAN ĐỢI BÀN
    // ==============================
    public boolean update(ThoiGianDoiBan tgdb) {
        String sql = "UPDATE ThoiGianDoiBan SET loaiDatBan=?, thoiGian=? WHERE maTGDB=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, tgdb.isLoaiDatBan());
            ps.setInt(2, tgdb.getThoiGian());
            ps.setString(3, tgdb.getMaTGDB());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // XOÁ THỜI GIAN ĐỢI BÀN
    // ==============================
    public boolean delete(String maTGDB) {
        String sql = "DELETE FROM ThoiGianDoiBan WHERE maTGDB=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maTGDB);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
