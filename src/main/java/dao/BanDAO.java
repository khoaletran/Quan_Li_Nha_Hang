package dao;

import connectDB.connectDB;
import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BanDAO {

    // ==============================
    // LẤY TOÀN BỘ DANH SÁCH BÀN
    // ==============================
    public List<Ban> getAll() {
        List<Ban> ds = new ArrayList<>();
        String sql = """
                SELECT b.maBan, b.trangThai, 
                       lb.maLoaiBan, lb.tenLoaiBan, lb.soLuong,
                       kv.maKhuVuc, kv.tenKhuVuc
                FROM Ban b
                JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
                JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
                """;
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                LoaiBan loaiBan = new LoaiBan(
                        rs.getString("maLoaiBan"),
                        rs.getInt("soLuong"),
                        rs.getString("tenLoaiBan")
                );
                KhuVuc khuVuc = new KhuVuc(
                        rs.getString("maKhuVuc"),
                        rs.getString("tenKhuVuc")
                );
                Ban ban = new Ban(
                        rs.getString("maBan"),
                        khuVuc,
                        loaiBan
                );
                ds.add(ban);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ==============================
    // THÊM BÀN MỚI
    // ==============================
    public boolean insert(Ban ban, boolean trangThai) {
        String sql = "INSERT INTO Ban (maBan, trangThai, maLoaiBan, maKhuVuc) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, ban.getMaBan());
            ps.setBoolean(2, trangThai);
            ps.setString(3, ban.getLoaiBan().getMaLoaiBan());
            ps.setString(4, ban.getKhuVuc().getMaKhuVuc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // CẬP NHẬT BÀN
    // ==============================
    public boolean update(Ban ban, boolean trangThai) {
        String sql = "UPDATE Ban SET trangThai=?, maLoaiBan=?, maKhuVuc=? WHERE maBan=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setString(2, ban.getLoaiBan().getMaLoaiBan());
            ps.setString(3, ban.getKhuVuc().getMaKhuVuc());
            ps.setString(4, ban.getMaBan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // XOÁ BÀN
    // ==============================
    public boolean delete(String maBan) {
        String sql = "DELETE FROM Ban WHERE maBan=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
