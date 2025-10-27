package dao;

import connectDB.connectDB;
import entity.HangKhachHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HangKhachDAO {

    // ======== LẤY DANH SÁCH TẤT CẢ ========
    public static List<HangKhachHang> getAll() {
        List<HangKhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM HangKhachHang";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                HangKhachHang hkh = new HangKhachHang(
                        rs.getString("maHang"),
                        rs.getString("moTa"),
                        rs.getInt("giaGiam"),
                        rs.getInt("diemHang")
                );
                ds.add(hkh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ======== THÊM MỚI ========
    public boolean insert(HangKhachHang hkh) {
        String sql = "INSERT INTO HangKhachHang(maHang, diemHang, giaGiam, moTa) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, hkh.getMaHang());
            ps.setInt(2, hkh.getDiemHang());
            ps.setInt(3, hkh.getGiamGia());
            ps.setString(4, hkh.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ======== CẬP NHẬT ========
    public boolean update(HangKhachHang hkh) {
        String sql = "UPDATE HangKhachHang SET diemHang=?, giaGiam=?, moTa=? WHERE maHang=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, hkh.getDiemHang());
            ps.setInt(2, hkh.getGiamGia());
            ps.setString(3, hkh.getMoTa());
            ps.setString(4, hkh.getMaHang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ======== XÓA ========
    public boolean delete(String maHang) {
        String sql = "DELETE FROM HangKhachHang WHERE maHang=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ======== TÌM THEO MÃ (phiên bản đơn giản) ========
    public HangKhachHang findById(String maHang) {
        return getByID(maHang);
    }

    // ======== LẤY THEO MÃ (phiên bản chuẩn OOP static) ========
    public static HangKhachHang getByID(String maHang) {
        String sql = "SELECT * FROM HangKhachHang WHERE maHang = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new HangKhachHang(
                            rs.getString("maHang"),
                            rs.getString("moTa"),
                            rs.getInt("giaGiam"),
                            rs.getInt("diemHang")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hạng khách hàng theo mã: " + e.getMessage());
        }
        return null;
    }

    // ======== LẤY HẠNG PHÙ HỢP THEO ĐIỂM ========
    public static HangKhachHang getHangTheoDiem(int diem) {
        String sql = """
            SELECT TOP 1 * FROM HangKhachHang
            WHERE diemHang <= ?
            ORDER BY diemHang DESC
        """;
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, diem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new HangKhachHang(
                            rs.getString("maHang"),
                            rs.getString("moTa"),
                            rs.getInt("giaGiam"),
                            rs.getInt("diemHang")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
