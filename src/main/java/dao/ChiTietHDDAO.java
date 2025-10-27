package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.Mon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHDDAO {

    // ===== 1. LẤY DANH SÁCH CHI TIẾT THEO MÃ HÓA ĐƠN =====
    public List<ChiTietHoaDon> getByMaHD(String maHD) {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHD = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maMon = rs.getString("maMon");
                    int soLuong = rs.getInt("soLuong");
                    double thanhTien = rs.getDouble("thanhTien");

                    // Lấy thông tin món từ MonDAO
                    Mon mon = new MonDAO().findByID(maMon);

                    // Lấy thông tin hóa đơn từ HoaDonDAO
                    HoaDon hoaDon = HoaDonDAO.getByID(maHD);

                    // Tạo đối tượng ChiTietHoaDon theo constructor của bạn
                    ChiTietHoaDon ct = new ChiTietHoaDon(hoaDon, mon, soLuong);

                    // Set lại thành tiền từ database (vì constructor tính tự động)
                    ct.setThanhTien(thanhTien);

                    ds.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi khác khi lấy chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }

    // ===== 2. THÊM MỘT CHI TIẾT HÓA ĐƠN =====
    public boolean insert(ChiTietHoaDon ct) {
        String sql = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, thanhTien) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ct.getHoaDon().getMaHD());
            ps.setString(2, ct.getMon().getMaMon());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getThanhTien());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== 3. XÓA CHI TIẾT HÓA ĐƠN =====
    public boolean delete(String maHD, String maMon) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maMon = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ps.setString(2, maMon);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== 4. CẬP NHẬT CHI TIẾT HÓA ĐƠN =====
    public boolean update(ChiTietHoaDon ct) {
        String sql = "UPDATE ChiTietHoaDon SET soLuong = ?, thanhTien = ? WHERE maHD = ? AND maMon = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ct.getSoLuong());
            ps.setDouble(2, ct.getThanhTien());
            ps.setString(3, ct.getHoaDon().getMaHD());
            ps.setString(4, ct.getMon().getMaMon());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== 5. LẤY TỔNG TIỀN CỦA HÓA ĐƠN =====
    public double getTongTienByMaHD(String maHD) {
        String sql = "SELECT SUM(thanhTien) as tongTien FROM ChiTietHoaDon WHERE maHD = ?";
        double tongTien = 0;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tongTien = rs.getDouble("tongTien");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng tiền hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return tongTien;
    }
}