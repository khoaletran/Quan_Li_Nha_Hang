package dao;

import connectDB.connectDB;
import entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // ===================== GET BY ID =====================
    public static HoaDon getByID(String maHD) {
        String sql = "SELECT * FROM HoaDon WHERE maHD = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // ======== LẤY KHÓA NGOẠI ========
                String maKH = rs.getString("maKH");
                String maNV = rs.getString("maNV");
                String maBan = rs.getString("maBan");
                String maKM = rs.getString("maKM");
                String maSK = rs.getString("maSK");

                // ======== LẤY ĐỐI TƯỢNG LIÊN QUAN ========
                KhachHang kh = (maKH != null) ? KhachHangDAO.getByID(maKH) : null;
                NhanVien nv = (maNV != null) ? NhanVienDAO.getByID(maNV) : null;
                Ban ban = (maBan != null) ? BanDAO.getByID(maBan) : null;
                KhuyenMai km = (maKM != null) ? KhuyenMaiDAO.getByID(maKM) : null;
                SuKien sk = (maSK != null) ? SuKienDAO.getByID(maSK) : null;

                // ======== TẠO ĐỐI TƯỢNG HÓA ĐƠN ========
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setKhachHang(kh);
                hd.setNhanVien(nv);
                hd.setBan(ban);
                hd.setKhuyenMai(km);
                hd.setSuKien(sk);

                hd.setTgCheckIn(rs.getTimestamp("tgCheckin") != null
                        ? rs.getTimestamp("tgCheckin").toLocalDateTime() : null);
                hd.setTgCheckOut(rs.getTimestamp("tgCheckout") != null
                        ? rs.getTimestamp("tgCheckout").toLocalDateTime() : null);

                hd.setKieuThanhToan(rs.getBoolean("kieuThanhToan"));
                hd.setKieuDatBan(rs.getBoolean("kieuDatBan"));
                hd.setThue(rs.getDouble("thue"));
                hd.setCoc(rs.getDouble("coc"));
                hd.setTrangthai(rs.getInt("trangThai"));
                hd.setTongTienTruoc(rs.getDouble("tongTienTruoc"));
                hd.setTongTienSau(rs.getDouble("tongTienSau"));
                hd.setTongTienKhuyenMai(rs.getDouble("tongTienKM"));
                hd.setSoLuong(rs.getInt("soLuong"));

                return hd;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ===================== GET ALL =====================
    public static List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT maHD FROM HoaDon";
        try (Connection conn = connectDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                HoaDon hd = getByID(rs.getString("maHD"));
                if (hd != null) ds.add(hd);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
        }
        return ds;
    }

    // ===================== INSERT =====================
    public static boolean insert(HoaDon hd) {
        String sql = """
            INSERT INTO HoaDon(
                maHD, maKH, maNV, maBan, maKM, maSK,
                tgCheckin, tgCheckout, kieuThanhToan, kieuDatBan,
                thue, coc, tongTienTruoc, tongTienSau, tongTienKM, trangThai, soLuong
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
            ps.setInt(17, hd.getSoLuong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thêm hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ===================== UPDATE =====================
    public static boolean update(HoaDon hd) {
        String sql = """
            UPDATE HoaDon SET 
                maKH=?, maNV=?, maBan=?, maKM=?, maSK=?,
                tgCheckin=?, tgCheckout=?,
                kieuThanhToan=?, kieuDatBan=?,
                thue=?, coc=?, tongTienTruoc=?, tongTienSau=?, tongTienKM=?, trangThai=?, soLuong=?
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
            ps.setInt(16, hd.getSoLuong());
            ps.setString(17, hd.getMaHD());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ===================== DELETE =====================
    public static boolean delete(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE maHD=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
            return false;
        }
    }

    public static String getMaHDCuoiTheoNgay(String ca, String ngay) {
        String sql = "SELECT TOP 1 maHD FROM HoaDon WHERE maHD LIKE ? ORDER BY maHD DESC";

        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "HD" + ca + ngay + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("maHD");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy mã hóa đơn cuối theo ngày: " + e.getMessage());
        }
        return null;
    }

}
