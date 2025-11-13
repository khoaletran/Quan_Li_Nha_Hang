package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoaiMon;
import entity.Mon;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChiTietHDDAO {
    public static List<ChiTietHoaDon> getAllByMaHD(String maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = """
                        SELECT
                            cthd.*,
                            m.tenMon,
                            m.giaGoc,
                            lm.maLoaiMon,
                            lm.tenLoaiMon,
                            ISNULL(pt.phanTramLoi, ptLoai.phanTramLoi) AS phanTramLoi
                        FROM ChiTietHoaDon cthd
                        JOIN Mon m ON cthd.maMon = m.maMon
                        JOIN LoaiMon lm ON m.loaiMon = lm.maLoaiMon
                        LEFT JOIN (
                            SELECT p1.maMon, p1.phanTramLoi
                            FROM PhanTramGiaBan p1
                            WHERE p1.maPTGB = (
                                SELECT TOP 1 p2.maPTGB
                                FROM PhanTramGiaBan p2
                                WHERE p2.maMon = p1.maMon
                                ORDER BY p2.maPTGB DESC
                            )
                        ) pt ON pt.maMon = m.maMon
                        LEFT JOIN (
                            SELECT p3.maLoaiMon, p3.phanTramLoi
                            FROM PhanTramGiaBan p3
                            WHERE p3.maPTGB = (
                                SELECT TOP 1 p4.maPTGB
                                FROM PhanTramGiaBan p4
                                WHERE p4.maLoaiMon = p3.maLoaiMon AND p4.maMon IS NULL
                                ORDER BY p4.maPTGB DESC
                            )
                        ) ptLoai ON ptLoai.maLoaiMon = m.loaiMon
                        WHERE cthd.maHD = ?;
                """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mon m = new Mon();
                    m.setMaMon(rs.getString("maMon"));
                    m.setTenMon(rs.getString("tenMon"));
                    m.setGiaGoc(rs.getDouble("giaGoc"));

                    LoaiMon loai = new LoaiMon();
                    loai.setMaLoaiMon(rs.getString("maLoaiMon"));
                    loai.setTenLoaiMon(rs.getString("tenLoaiMon"));
                    m.setLoaiMon(loai);

                    HoaDon hd = new HoaDon();
                    hd.setMaHD(rs.getString("maHD"));

                    ChiTietHoaDon cthd = new ChiTietHoaDon();
                    cthd.setHoaDon(hd);
                    cthd.setMon(m);
                    cthd.setSoLuong(rs.getInt("soLuong"));

                    int ptgb = rs.getInt("phanTramLoi");
                    double thanhTien = m.getGiaGoc() * (1 + ptgb / 100.0) * cthd.getSoLuong();
                    cthd.setThanhTien(thanhTien);

                    list.add(cthd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


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
            System.err.println("Lỗi khi lấy danh sách chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi khác khi lấy danh sách chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }


    public static List<ChiTietHoaDon> getAllCTHDTheoThangNam(int nam, int thang) {
        List<ChiTietHoaDon> dsct = new ArrayList<>();

        String sql = """
                    SELECT
                        m.maMon,
                        m.tenMon,
                        m.hinhAnh,
                        m.soLuong as soLuongTon,
                        SUM(cthd.soLuong) AS tongSoLuong
                    FROM ChiTietHoaDon cthd
                    JOIN HoaDon hd ON cthd.maHD = hd.maHD
                    JOIN Mon m ON cthd.maMon = m.maMon
                    WHERE YEAR(hd.tgLapHD) = ?
                """;

        if (thang != 0) { // 0 nghĩa tất cả tháng
            sql += " AND MONTH(hd.tgLapHD) = ? ";
        }

        sql += " GROUP BY m.maMon, m.soLuong, m.tenMon, m.hinhAnh ORDER BY tongSoLuong DESC";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nam);
            if (thang != 0) {
                ps.setInt(2, thang);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietHoaDon cthd = new ChiTietHoaDon();
                Mon m = new Mon();
                m.setMaMon(rs.getString("maMon"));
                m.setTenMon(rs.getString("tenMon"));
                m.setHinhAnh(rs.getString("hinhAnh"));
                m.setSoLuong(rs.getInt("soLuongTon"));
                cthd.setMon(m);
                cthd.setSoLuong(rs.getInt("tongSoLuong")); // cheat tạm
                dsct.add(cthd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsct;
    }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

}