package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.Mon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHDDAO {

    // ===== 1. LẤY DANH SÁCH CHI TIẾT THEO MÃ HÓA ĐƠN =====
    public List<ChiTietHoaDon> getByMaHD(String maHD) {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHD = ?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maMon = rs.getString("maMon");
                    int soLuong = rs.getInt("soLuong");
                    double thanhTien = rs.getDouble("thanhTien");

                    Mon mon = new MonDAO().findByID(maMon); // lấy thông tin món
                    ds.add(new ChiTietHoaDon(HoaDonDAO.getByID(maHD), mon, soLuong));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ===== 2. THÊM MỘT CHI TIẾT HÓA ĐƠN =====
    public boolean insert(String maHD, ChiTietHoaDon ct) {
        String sql = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, thanhTien) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, ct.getMon().getMaMon());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
