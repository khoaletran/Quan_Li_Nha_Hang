package dao;

import connectDB.connectDB;
import entity.*;

import java.sql.*;
import java.util.*;

public class ChiTietHDDAO {

    // ============================================================================
    //  MAPPER KHÔNG getByID – TẠO ĐỦ Món + Loại + Hóa Đơn (maHD)
    // ============================================================================
    private static ChiTietHoaDon mapCTHDFull(ResultSet rs) throws SQLException {

        // ===== MÓN =====
        Mon mon = new Mon();
        mon.setMaMon(rs.getString("maMon"));
        mon.setTenMon(rs.getString("tenMon"));
        mon.setGiaGoc(rs.getDouble("giaGoc"));
        mon.setHinhAnh(rs.getString("hinhAnh"));

        // ===== LOẠI MÓN =====
        LoaiMon loai = new LoaiMon();
        loai.setMaLoaiMon(rs.getString("maLoaiMon"));
        loai.setTenLoaiMon(rs.getString("tenLoaiMon"));
        mon.setLoaiMon(loai);

        // ===== HÓA ĐƠN =====
        HoaDon hd = new HoaDon();
        hd.setMaHD(rs.getString("maHD"));

        // ===== CHI TIẾT HÓA ĐƠN =====
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setHoaDon(hd);
        ct.setMon(mon);
        ct.setSoLuong(rs.getInt("soLuong"));

        // ===== GIÁ BÁN =====
        int ptgb = rs.getInt("phanTramLoi");
        double thanhTien = mon.getGiaGoc() * (1 + ptgb / 100.0) * ct.getSoLuong();
        ct.setThanhTien(thanhTien);

        return ct;
    }


    // ============================================================================
    // 1. GET ALL CHI TIẾT HÓA ĐƠN THEO MÃ HÓA ĐƠN (FULL JOIN)
    // ============================================================================
    public static List<ChiTietHoaDon> getAllByMaHD(String maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();

        String sql = """
            SELECT
                cthd.maHD,
                cthd.maMon,
                cthd.soLuong,

                m.tenMon, m.giaGoc, m.hinhAnh,
                lm.maLoaiMon, lm.tenLoaiMon,

                COALESCE(ptMon.phanTramLoi, ptLoai.phanTramLoi, 0) AS phanTramLoi

            FROM ChiTietHoaDon cthd
            JOIN Mon m ON cthd.maMon = m.maMon
            JOIN LoaiMon lm ON m.loaiMon = lm.maLoaiMon

            LEFT JOIN (
                SELECT p1.maMon, p1.phanTramLoi
                FROM PhanTramGiaBan p1
                WHERE p1.ngayApDung = (
                    SELECT MAX(p2.ngayApDung)
                    FROM PhanTramGiaBan p2
                    WHERE p2.maMon = p1.maMon
                )
            ) ptMon ON ptMon.maMon = m.maMon

            LEFT JOIN (
                SELECT p3.maLoaiMon, p3.phanTramLoi
                FROM PhanTramGiaBan p3
                WHERE p3.maMon IS NULL
                  AND p3.ngayApDung = (
                        SELECT MAX(p4.ngayApDung)
                        FROM PhanTramGiaBan p4
                        WHERE p4.maLoaiMon = p3.maLoaiMon AND p4.maMon IS NULL
                )
            ) ptLoai ON ptLoai.maLoaiMon = lm.maLoaiMon

            WHERE cthd.maHD = ?
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapCTHDFull(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // ============================================================================
    // 2. GET BY MAHD – GIỮ NGHIỆP VỤ, NHƯNG JOIN ĐẦY ĐỦ (KHÔNG getByID)
    // ============================================================================
    public static List<ChiTietHoaDon> getByMaHD(String maHD) {
        return getAllByMaHD(maHD); // dùng JOIN version
    }


    // ============================================================================
    // 3. THÊM
    // ============================================================================
    public boolean insert(ChiTietHoaDon ct) {
        String sql = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong) VALUES (?, ?, ?)";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ct.getHoaDon().getMaHD());
            ps.setString(2, ct.getMon().getMaMon());
            ps.setInt(3, ct.getSoLuong());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi thêm chi tiết hóa đơn: " + e.getMessage());
            return false;
        }
    }


    // ============================================================================
    // 4. XÓA
    // ============================================================================
    public boolean delete(String maHD, String maMon) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maMon = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ps.setString(2, maMon);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi xóa chi tiết hóa đơn: " + e.getMessage());
            return false;
        }
    }


    // ============================================================================
    // 5. CẬP NHẬT
    // ============================================================================
    public boolean update(ChiTietHoaDon ct) {
        String sql = "UPDATE ChiTietHoaDon SET soLuong = ? WHERE maHD = ? AND maMon = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ct.getSoLuong());
            ps.setString(2, ct.getHoaDon().getMaHD());
            ps.setString(3, ct.getMon().getMaMon());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật chi tiết hóa đơn: " + e.getMessage());
            return false;
        }
    }


    // ============================================================================
    // 6. LẤY TOÀN BỘ CHI TIẾT HÓA ĐƠN (JOIN FULL)
    // ============================================================================
    public static List<ChiTietHoaDon> getAll() {
        List<ChiTietHoaDon> ds = new ArrayList<>();

        String sql = """
            SELECT 
                cthd.maHD, cthd.maMon, cthd.soLuong,

                m.tenMon, m.giaGoc, m.hinhAnh,
                lm.maLoaiMon, lm.tenLoaiMon,

                COALESCE(ptMon.phanTramLoi, ptLoai.phanTramLoi, 0) AS phanTramLoi

            FROM ChiTietHoaDon cthd
            JOIN Mon m ON cthd.maMon = m.maMon
            JOIN LoaiMon lm ON m.loaiMon = lm.maLoaiMon

            LEFT JOIN (
                SELECT p1.maMon, p1.phanTramLoi
                FROM PhanTramGiaBan p1
                WHERE p1.ngayApDung = (
                    SELECT MAX(p2.ngayApDung)
                    FROM PhanTramGiaBan p2
                    WHERE p2.maMon = p1.maMon
                )
            ) ptMon ON ptMon.maMon = m.maMon

            LEFT JOIN (
                SELECT p3.maLoaiMon, p3.phanTramLoi
                FROM PhanTramGiaBan p3
                WHERE p3.maMon IS NULL
                  AND p3.ngayApDung = (
                      SELECT MAX(p4.ngayApDung)
                      FROM PhanTramGiaBan p4
                      WHERE p4.maLoaiMon = p3.maLoaiMon AND p4.maMon IS NULL
                  )
            ) ptLoai ON ptLoai.maLoaiMon = lm.maLoaiMon
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) ds.add(mapCTHDFull(rs));

        } catch (Exception e) {
            System.err.println("Lỗi getAll CTHD: " + e.getMessage());
        }

        return ds;
    }


    // ============================================================================
    // 7. THỐNG KÊ THEO THÁNG NĂM (NHẸ & CHUẨN)
    // ============================================================================
    public static List<ChiTietHoaDon> getAllCTHDTheoThangNam(int nam, int thang) {
        List<ChiTietHoaDon> ds = new ArrayList<>();

        String sql = """
            SELECT 
                m.maMon, m.tenMon, m.hinhAnh, m.soLuong AS soLuongTon,
                SUM(cthd.soLuong) AS tongSoLuong
            FROM ChiTietHoaDon cthd
            JOIN HoaDon hd ON cthd.maHD = hd.maHD
            JOIN Mon m ON cthd.maMon = m.maMon
            WHERE YEAR(hd.tgLapHD) = ?
        """;

        if (thang != 0) sql += " AND MONTH(hd.tgLapHD) = ? ";

        sql += " GROUP BY m.maMon, m.tenMon, m.hinhAnh, m.soLuong ORDER BY tongSoLuong DESC";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nam);
            if (thang != 0) ps.setInt(2, thang);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Mon m = new Mon();
                m.setMaMon(rs.getString("maMon"));
                m.setTenMon(rs.getString("tenMon"));
                m.setHinhAnh(rs.getString("hinhAnh"));
                m.setSoLuong(rs.getInt("soLuongTon"));

                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMon(m);
                ct.setSoLuong(rs.getInt("tongSoLuong"));

                ds.add(ct);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }


    // ============================================================================
    // 8. LẤY SỐ LƯỢNG BÁN THEO THÁNG NĂM (NHẸ)
    // ============================================================================
    public static Map<String, Integer> getSoLuongTheoThangNam(int nam, int thang) {
        Map<String, Integer> map = new HashMap<>();

        String sql = """
            SELECT m.maMon, SUM(cthd.soLuong) AS tongSoLuong
            FROM ChiTietHoaDon cthd
            JOIN HoaDon hd ON cthd.maHD = hd.maHD
            JOIN Mon m ON cthd.maMon = m.maMon
            WHERE YEAR(hd.tgLapHD) = ? AND MONTH(hd.tgLapHD) = ?
            GROUP BY m.maMon
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nam);
            ps.setInt(2, thang);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("maMon"), rs.getInt("tongSoLuong"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

}
