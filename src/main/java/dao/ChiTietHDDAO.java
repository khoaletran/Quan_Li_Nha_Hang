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
    public static List<ChiTietHoaDon> getByMaHD(String maHD) {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHD = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maMon = rs.getString("maMon");
                    int soLuong = rs.getInt("soLuong");
                    // Lấy thông tin món từ MonDAO
                    Mon mon = new MonDAO().findByID(maMon);

                    // Lấy thông tin hóa đơn từ HoaDonDAO
                    HoaDon hoaDon = HoaDonDAO.getByID(maHD);

                    // Tạo đối tượng ChiTietHoaDon theo constructor của bạn
                    ChiTietHoaDon ct = new ChiTietHoaDon(hoaDon, mon, soLuong);

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
        String sql = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong ) VALUES (?, ?, ?)";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ct.getHoaDon().getMaHD());
            ps.setString(2, ct.getMon().getMaMon());
            ps.setInt(3, ct.getSoLuong());

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
        String sql = "UPDATE ChiTietHoaDon SET soLuong = ? WHERE maHD = ? AND maMon = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ct.getSoLuong());
            ps.setString(2, ct.getHoaDon().getMaHD());
            ps.setString(3, ct.getMon().getMaMon());

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

    // ===== 6. LẤY TOÀN BỘ CHI TIẾT HÓA ĐƠN =====
    public static List<ChiTietHoaDon> getAll() {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // 🔹 Lấy dữ liệu phụ trước để giảm truy vấn lặp
            List<Mon> dsMon = MonDAO.getAll();
            List<HoaDon> dsHD = HoaDonDAO.getAll();

            while (rs.next()) {
                String maHD = rs.getString("maHD");
                String maMon = rs.getString("maMon");
                int soLuong = rs.getInt("soLuong");

                // 🔹 Lấy thông tin từ cache (RAM), không query SQL
                Mon mon = dsMon.stream()
                        .filter(m -> m.getMaMon().equals(maMon))
                        .findFirst()
                        .orElse(null);

                HoaDon hd = dsHD.stream()
                        .filter(h -> h.getMaHD().equals(maHD))
                        .findFirst()
                        .orElse(null);

                if (mon != null && hd != null) {
                    ds.add(new ChiTietHoaDon(hd, mon, soLuong));
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khác khi lấy danh sách chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }

}