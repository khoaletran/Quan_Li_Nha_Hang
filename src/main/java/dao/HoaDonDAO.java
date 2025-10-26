package dao;

import connectDB.connectDB;
import entity.HoaDon;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.KhuyenMai;
import entity.SuKien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // ================== LẤY TOÀN BỘ ==================
    public List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                HoaDon hd = new HoaDon(
                        rs.getString("maHD"),
                        rs.getDouble("tongTienKM"),
                        rs.getDouble("tongTienSau"),
                        rs.getDouble("tongTienTruoc"),
                        rs.getDouble("coc"),
                        rs.getDouble("thue"),
                        rs.getBoolean("kieuDatBan"),
                        rs.getBoolean("kieuThanhToan"),
                        null, null, // KH & NV
                        rs.getInt("trangThai"),
                        rs.getTimestamp("tgCheckin") != null ? rs.getTimestamp("tgCheckin").toLocalDateTime() : null,
                        rs.getTimestamp("tgCheckout") != null ? rs.getTimestamp("tgCheckout").toLocalDateTime() : null,
                        null, null, null // khuyến mãi, sự kiện, bàn
                );
                ds.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ================== THÊM HÓA ĐƠN ==================
    public boolean insert(HoaDon hd) {
        String sql = """
            INSERT INTO HoaDon(
                maHD, maKH, maNV, maBan, maKM, maSK, 
                tgCheckin, tgCheckout, kieuThanhToan, kieuDatBan, 
                thue, coc, tongTienTruoc, tongTienSau, tongTienKM, trangThai
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            ps.setString(3, hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : null);
            ps.setString(4, hd.getBan() != null ? hd.getBan().getMaBan() : null);
            ps.setString(5, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
            ps.setString(6, hd.getSuKien() != null ? hd.getSuKien().getMaSK() : null);

            ps.setTimestamp(7, hd.getTgCheckIn() != null ? Timestamp.valueOf(hd.getTgCheckIn()) : null);
            ps.setTimestamp(8, hd.getTgCheckOut() != null ? Timestamp.valueOf(hd.getTgCheckOut()) : null);
            ps.setBoolean(9, hd.isKieuThanhToan());
            ps.setBoolean(10, hd.isKieuDatBan());

            ps.setDouble(11, hd.getThue());
            ps.setDouble(12, hd.getCoc());
            ps.setDouble(13, hd.getTongTienTruoc());
            ps.setDouble(14, hd.getTongTienSau());
            ps.setDouble(15, hd.getTongTienKhuyenMai());
            ps.setInt(16, hd.getTrangthai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thêm hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ================== CẬP NHẬT ==================
    public boolean update(HoaDon hd) {
        String sql = """
            UPDATE HoaDon SET 
                maKH=?, maNV=?, maBan=?, maKM=?, maSK=?,
                tgCheckin=?, tgCheckout=?, 
                kieuThanhToan=?, kieuDatBan=?, 
                thue=?, coc=?, tongTienTruoc=?, tongTienSau=?, tongTienKM=?, trangThai=? 
            WHERE maHD=?
        """;
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            ps.setString(2, hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : null);
            ps.setString(3, hd.getBan() != null ? hd.getBan().getMaBan() : null);
            ps.setString(4, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
            ps.setString(5, hd.getSuKien() != null ? hd.getSuKien().getMaSK() : null);

            ps.setTimestamp(6, hd.getTgCheckIn() != null ? Timestamp.valueOf(hd.getTgCheckIn()) : null);
            ps.setTimestamp(7, hd.getTgCheckOut() != null ? Timestamp.valueOf(hd.getTgCheckOut()) : null);
            ps.setBoolean(8, hd.isKieuThanhToan());
            ps.setBoolean(9, hd.isKieuDatBan());
            ps.setDouble(10, hd.getThue());
            ps.setDouble(11, hd.getCoc());
            ps.setDouble(12, hd.getTongTienTruoc());
            ps.setDouble(13, hd.getTongTienSau());
            ps.setDouble(14, hd.getTongTienKhuyenMai());
            ps.setInt(15, hd.getTrangthai());
            ps.setString(16, hd.getMaHD());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ================== XÓA ==================
    public boolean delete(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE maHD=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi xóa hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ================== LẤY MÃ CUỐI ==================
    public String getMaHDCuoi() {
        String sql = "SELECT TOP 1 maHD FROM HoaDon ORDER BY maHD DESC";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getString("maHD");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ================== LẤY THEO MÃ ==================
    public static HoaDon getByID(String maHD) {
        String sql = "SELECT * FROM HoaDon WHERE maHD = ?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoaDon hd = new HoaDon(
                            rs.getString("maHD"),
                            rs.getDouble("tongTienKM"),
                            rs.getDouble("tongTienSau"),
                            rs.getDouble("tongTienTruoc"),
                            rs.getDouble("coc"),
                            rs.getDouble("thue"),
                            rs.getBoolean("kieuDatBan"),
                            rs.getBoolean("kieuThanhToan"),
                            null, null,
                            rs.getInt("trangThai"),
                            rs.getTimestamp("tgCheckin") != null ? rs.getTimestamp("tgCheckin").toLocalDateTime() : null,
                            rs.getTimestamp("tgCheckout") != null ? rs.getTimestamp("tgCheckout").toLocalDateTime() : null,
                            null, null, null
                    );
                    return hd;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm hóa đơn theo mã: " + e.getMessage());
        }
        return null;
    }

    public String getMaHDCuoiTheoNgay(String ca, String ngay) {
        String sql = "SELECT TOP 1 maHD FROM HoaDon WHERE maHD LIKE ? ORDER BY maHD DESC";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, "HD" + ca + ngay + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("maHD");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy mã hóa đơn cuối theo ca & ngày: " + e.getMessage());
        }
        return null;
    }


}
