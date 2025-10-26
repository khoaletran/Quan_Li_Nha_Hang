package dao;

import connectDB.connectDB;
import entity.HangKhachHang;
import entity.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    // ===== LẤY TOÀN BỘ DANH SÁCH KHÁCH HÀNG =====
    public static List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";

        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<HangKhachHang> dsHang = HangKhachDAO.getAll();

            while (rs.next()) {
                String maHang = rs.getString("maHang");
                HangKhachHang hang = dsHang.stream()
                        .filter(h -> h.getMaHang().equals(maHang))
                        .findFirst()
                        .orElse(null);

                KhachHang kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("gioiTinh"),
                        rs.getString("sdt"),
                        rs.getString("tenKH"),
                        hang
                );
                list.add(kh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== THÊM KHÁCH HÀNG MỚI =====
    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(maKH, maHang, tenKH, sdt, gioiTinh, diemTichLuy) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getHangKhachHang() != null ? kh.getHangKhachHang().getMaHang() : null);
            ps.setString(3, kh.getTenKhachHang());
            ps.setString(4, kh.getSdt());
            ps.setBoolean(5, kh.isGioiTinh());
            ps.setInt(6, kh.getDiemTichLuy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== CẬP NHẬT THÔNG TIN KHÁCH HÀNG =====
    public static boolean update(KhachHang kh) {
        String sql = "UPDATE KhachHang SET tenKH=?, sdt=?, gioiTinh=?, diemTichLuy=? WHERE maKH=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, kh.getTenKhachHang());
            ps.setString(2, kh.getSdt());
            ps.setBoolean(3, kh.isGioiTinh());
            ps.setInt(4, kh.getDiemTichLuy());
            ps.setString(5, kh.getMaKhachHang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== XÓA KHÁCH HÀNG =====
    public boolean delete(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKH=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== TÌM KHÁCH HÀNG THEO SỐ ĐIỆN THOẠI =====
    public KhachHang findBySDT(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE sdt = ?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, sdt);
            List<HangKhachHang> dsHang = HangKhachDAO.getAll();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maHang = rs.getString("maHang");
                    HangKhachHang hang = dsHang.stream()
                            .filter(h -> h.getMaHang().equals(maHang))
                            .findFirst()
                            .orElse(null);
                    return new KhachHang(
                            rs.getString("maKH"),
                            rs.getInt("diemTichLuy"),
                            rs.getBoolean("gioiTinh"),
                            rs.getString("sdt"),
                            rs.getString("tenKH"),
                            hang
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== LẤY MÃ KHÁCH HÀNG CUỐI CÙNG =====
    public String getMaKHCuoi() {
        String sql = "SELECT TOP 1 maKH FROM KhachHang ORDER BY maKH DESC";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("maKH");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
