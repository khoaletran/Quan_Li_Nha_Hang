package dao;

import connectDB.connectDB;
import entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // ===================== GET ALL =====================
    public static List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maKH = rs.getString("maKH");
                String maNV = rs.getString("maNV");
                String maBan = rs.getString("maBan");
                String maKM = rs.getString("maKM");
                String maSK = rs.getString("maSK");

                KhachHang kh = (maKH != null) ? KhachHangDAO.getByID(maKH) : null;
                NhanVien nv = (maNV != null) ? NhanVienDAO.getByID(maNV) : null;
                Ban ban = (maBan != null) ? BanDAO.getByID(maBan) : null;
                KhuyenMai km = (maKM != null) ? KhuyenMaiDAO.getByID(maKM) : null;
                SuKien sk = (maSK != null) ? SuKienDAO.getByID(maSK) : null;

                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setKhachHang(kh);
                hd.setNhanVien(nv);
                hd.setBan(ban);
                hd.setKhuyenMai(km);
                hd.setSuKien(sk);
                hd.setTgCheckIn(rs.getTimestamp("tgCheckin") != null ? rs.getTimestamp("tgCheckin").toLocalDateTime() : null);
                hd.setTgCheckOut(rs.getTimestamp("tgCheckout") != null ? rs.getTimestamp("tgCheckout").toLocalDateTime() : null);
                hd.setKieuThanhToan(rs.getBoolean("kieuThanhToan"));
                hd.setKieuDatBan(rs.getBoolean("kieuDatBan"));
                hd.setTrangthai(rs.getInt("trangThai"));
                hd.setSoLuong(rs.getInt("soLuong"));
                hd.setMoTa(rs.getString("moTa"));

                ds.add(hd);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
        }

        return ds;
    }

    // ===================== GET BY ID =====================
    public static HoaDon getByID(String maHD) {
        String sql = "SELECT * FROM HoaDon WHERE maHD = ?";
        HoaDon hd = null;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maKH = rs.getString("maKH");
                    String maNV = rs.getString("maNV");
                    String maBan = rs.getString("maBan");
                    String maKM = rs.getString("maKM");
                    String maSK = rs.getString("maSK");

                    KhachHang kh = (maKH != null) ? KhachHangDAO.getByID(maKH) : null;
                    NhanVien nv = (maNV != null) ? NhanVienDAO.getByID(maNV) : null;
                    Ban ban = (maBan != null) ? BanDAO.getByID(maBan) : null;
                    KhuyenMai km = (maKM != null) ? KhuyenMaiDAO.getByID(maKM) : null;
                    SuKien sk = (maSK != null) ? SuKienDAO.getByID(maSK) : null;

                    hd = new HoaDon();
                    hd.setMaHD(rs.getString("maHD"));
                    hd.setKhachHang(kh);
                    hd.setNhanVien(nv);
                    hd.setBan(ban);
                    hd.setKhuyenMai(km);
                    hd.setSuKien(sk);
                    hd.setTgCheckIn(rs.getTimestamp("tgCheckin") != null ? rs.getTimestamp("tgCheckin").toLocalDateTime() : null);
                    hd.setTgCheckOut(rs.getTimestamp("tgCheckout") != null ? rs.getTimestamp("tgCheckout").toLocalDateTime() : null);
                    hd.setKieuThanhToan(rs.getBoolean("kieuThanhToan"));
                    hd.setKieuDatBan(rs.getBoolean("kieuDatBan"));
                    hd.setTrangthai(rs.getInt("trangThai"));
                    hd.setSoLuong(rs.getInt("soLuong"));
                    hd.setMoTa(rs.getString("moTa"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn theo mã: " + e.getMessage());
        }

        return hd;
    }

    // ===================== INSERT =====================
    public static boolean insert(HoaDon hd) {
        String sql = """
            INSERT INTO HoaDon(
                maHD, maKH, maNV, maBan, maKM, maSK,
                tgCheckin, tgCheckout, kieuThanhToan, kieuDatBan,
                trangThai, soLuong, moTa
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
            ps.setInt(11, hd.getTrangthai());
            ps.setInt(12, hd.getSoLuong());
            ps.setString(13, hd.getMoTa());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ===================== UPDATE =====================
    public static boolean update(HoaDon hd) {
        String sql = """
            UPDATE HoaDon SET 
                maKH=?, maNV=?, maBan=?, maKM=?, maSK=?,
                tgCheckin=?, tgCheckout=?, kieuThanhToan=?, kieuDatBan=?,
                trangThai=?, soLuong=?, moTa=?
            WHERE maHD=?
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            ps.setString(2, hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : null);
            ps.setString(3, hd.getBan() != null ? hd.getBan().getMaBan() : null);
            ps.setString(4, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
            ps.setString(5, hd.getSuKien() != null ? hd.getSuKien().getMaSK() : null);
            ps.setTimestamp(6, hd.getTgCheckIn() != null ? Timestamp.valueOf(hd.getTgCheckIn()) : null);
            ps.setTimestamp(7, hd.getTgCheckOut() != null ? Timestamp.valueOf(hd.getTgCheckOut()) : null);
            ps.setBoolean(8, hd.isKieuThanhToan());
            ps.setBoolean(9, hd.isKieuDatBan());
            ps.setInt(10, hd.getTrangthai());
            ps.setInt(11, hd.getSoLuong());
            ps.setString(12, hd.getMoTa());
            ps.setString(13, hd.getMaHD());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ===================== DELETE =====================
    public static boolean delete(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE maHD=?";
        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ===================== GET MAHD CUỐI THEO NGÀY =====================
    public static String getMaHDCuoiTheoNgay(String ca, String ngay) {
        String sql = "SELECT TOP 1 maHD FROM HoaDon WHERE maHD LIKE ? ORDER BY maHD DESC";
        String result = null;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "HD" + ca + ngay + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString("maHD");
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy mã hóa đơn cuối theo ngày: " + e.getMessage());
        }

        return result;
    }
}
