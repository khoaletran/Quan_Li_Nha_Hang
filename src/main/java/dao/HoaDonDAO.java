package dao;

import connectDB.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class HoaDonDAO {

    // =====================================================================
    //                          MAPPER FULL
    // =====================================================================
    private static HoaDon mapFullHoaDon(ResultSet rs) throws SQLException {

        // ===== KHÁCH HÀNG =====
        KhachHang kh = null;
        if (rs.getString("maKH") != null) {
            kh = new KhachHang();
            kh.setMaKhachHang(rs.getString("maKH"));
            kh.setTenKhachHang(rs.getString("tenKH"));
            kh.setSdt(rs.getString("sdtKH"));
            kh.setGioiTinh(rs.getBoolean("gioiTinhKH"));
            kh.setDiemTichLuy(rs.getInt("diemTichLuy"));
        }

        // ===== NHÂN VIÊN =====
        NhanVien nv = null;
        if (rs.getString("maNV") != null) {
            nv = new NhanVien();
            nv.setMaNV(rs.getString("maNV"));
            nv.setTenNV(rs.getString("tenNV"));
            nv.setSdt(rs.getString("sdtNV"));
        }

        // ===== BÀN – KHU VỰC – LOẠI BÀN =====
        Ban ban = null;
        if (rs.getString("maBan") != null) {

            KhuVuc kv = new KhuVuc();
            kv.setMaKhuVuc(rs.getString("maKhuVuc"));
            kv.setTenKhuVuc(rs.getString("tenKhuVuc"));

            LoaiBan lb = new LoaiBan();
            lb.setMaLoaiBan(rs.getString("maLoaiBan"));
            lb.setTenLoaiBan(rs.getString("tenLoaiBan"));
            lb.setSoLuong(rs.getInt("soLuong"));   // ✔ FIXED (không còn getSoLuong)

            ban = new Ban();
            ban.setMaBan(rs.getString("maBan"));
            ban.setTrangThai(rs.getBoolean("trangThaiBan"));
            ban.setKhuVuc(kv);
            ban.setLoaiBan(lb);
        }

        // ===== KHUYẾN MÃI =====
        KhuyenMai km = null;
        if (rs.getString("maKM") != null) {
            km = new KhuyenMai();
            km.setMaKM(rs.getString("maKM"));
            km.setTenKM(rs.getString("tenKM"));
            km.setPhanTRamGiamGia(rs.getInt("phanTramGiamGia"));
        }

        // ===== SỰ KIỆN =====
        SuKien sk = null;
        if (rs.getString("maSK") != null) {
            sk = new SuKien();
            sk.setMaSK(rs.getString("maSK"));
            sk.setTenSK(rs.getString("tenSK"));
            sk.setGia(rs.getDouble("giaSK"));
        }

        // ===== HÓA ĐƠN =====
        HoaDon hd = new HoaDon();
        hd.setMaHD(rs.getString("maHD"));
        hd.setKhachHang(kh);
        hd.setNhanVien(nv);
        hd.setBan(ban);
        hd.setKhuyenMai(km);
        hd.setSuKien(sk);

        Timestamp lap = rs.getTimestamp("tgLapHD");
        Timestamp ci = rs.getTimestamp("tgCheckin");
        Timestamp co = rs.getTimestamp("tgCheckout");

        hd.setTgLapHD(lap != null ? lap.toLocalDateTime() : null);
        hd.setTgCheckIn(ci != null ? ci.toLocalDateTime() : null);
        hd.setTgCheckOut(co != null ? co.toLocalDateTime() : null);

        hd.setKieuThanhToan(rs.getBoolean("kieuThanhToan"));
        hd.setKieuDatBan(rs.getBoolean("kieuDatBan"));
        hd.setTrangthai(rs.getInt("trangThai"));
        hd.setSoLuong(rs.getInt("soLuong"));
        hd.setMoTa(rs.getString("moTa"));

        return hd;
    }

    // =====================================================================
    //                      SELECT FULL – DÙNG CHUNG
    // =====================================================================
    private static final String SELECT_FULL = """
        SELECT hd.*,
               kh.tenKH, kh.sdt AS sdtKH, kh.gioiTinh AS gioiTinhKH, kh.diemTichLuy,
               nv.tenNV, nv.sdt AS sdtNV,
               b.trangThai AS trangThaiBan, b.maKhuVuc, b.maLoaiBan,
               kv.tenKhuVuc,
               lb.tenLoaiBan, lb.soLuong,
               km.tenKM, km.phanTramGiamGia,
               sk.tenSK, sk.gia AS giaSK
        FROM HoaDon hd
        LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH
        LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV
        LEFT JOIN Ban b ON hd.maBan = b.maBan
        LEFT JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
        LEFT JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
        LEFT JOIN KhuyenMai km ON hd.maKM = km.maKM
        LEFT JOIN SuKien sk ON hd.maSK = sk.maSK
        """;


    // =====================================================================
    //                              GET ALL
    // =====================================================================
    public static List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();

        String sql = SELECT_FULL;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                ds.add(mapFullHoaDon(rs));

        } catch (Exception e) {
            System.err.println("Lỗi getAll: " + e.getMessage());
        }

        return ds;
    }


    // =====================================================================
    //                          GET NGÀY HÔM NAY
    // =====================================================================
    public static List<HoaDon> getAllNgayHomNay() {
        List<HoaDon> ds = new ArrayList<>();

        String sql = SELECT_FULL + """
            WHERE (
                (hd.kieuDatBan = 1 AND hd.tgCheckin >= CAST(GETDATE() AS DATE)
                                   AND hd.tgCheckin < DATEADD(DAY,1,CAST(GETDATE() AS DATE)))
                OR
                (hd.kieuDatBan = 0 AND hd.tgLapHD >= CAST(GETDATE() AS DATE)
                                   AND hd.tgLapHD < DATEADD(DAY,1,CAST(GETDATE() AS DATE)))
            )
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                ds.add(mapFullHoaDon(rs));

        } catch (Exception e) {
            System.err.println("Lỗi getAllNgayHomNay: " + e.getMessage());
        }

        return ds;
    }


    // =====================================================================
    //                              GET BY ID
    // =====================================================================
    public static HoaDon getByID(String maHD) {

        String sql = SELECT_FULL + " WHERE hd.maHD = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapFullHoaDon(rs);

        } catch (Exception e) {
            System.err.println("Lỗi getByID: " + e.getMessage());
        }

        return null;
    }


    // =====================================================================
    //                          GET BY NHÂN VIÊN
    // =====================================================================
    public static List<HoaDon> getTheoMaNV(String maNV) {
        List<HoaDon> ds = new ArrayList<>();

        String sql = SELECT_FULL + " WHERE hd.maNV = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
                ds.add(mapFullHoaDon(rs));

        } catch (Exception e) {
            System.err.println("Lỗi getTheoMaNV: " + e.getMessage());
        }

        return ds;
    }


    // =====================================================================
    //                   WAITLIST – CHỜ
    // =====================================================================
    public static List<HoaDon> getAllWaitlistCho() {

        List<HoaDon> ds = new ArrayList<>();

        String sql = SELECT_FULL + """
            WHERE hd.kieuDatBan = 0
              AND hd.trangThai = 0
              AND hd.maBan LIKE 'W%'
            ORDER BY hd.maHD DESC
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                ds.add(mapFullHoaDon(rs));

        } catch (Exception e) {
            System.err.println("Lỗi getAllWaitlistCho: " + e.getMessage());
        }

        return ds;
    }



    // =====================================================================
    //        CÁC HÀM INSERT – UPDATE – DELETE (GIỮ NGUYÊN LOGIC)
    // =====================================================================

    public static boolean insert(HoaDon hd) {
        String sql = """
            INSERT INTO HoaDon(
                maHD, maKH, maNV, maBan, maKM, maSK, tgLapHD,
                tgCheckin, tgCheckout, kieuThanhToan, kieuDatBan,
                trangThai, soLuong, moTa
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            ps.setString(3, hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : null);
            ps.setString(4, hd.getBan() != null ? hd.getBan().getMaBan() : null);
            ps.setString(5, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
            ps.setString(6, hd.getSuKien() != null ? hd.getSuKien().getMaSK() : null);

            ps.setTimestamp(7, hd.getTgLapHD() != null ? Timestamp.valueOf(hd.getTgLapHD()) : null);
            ps.setTimestamp(8, hd.getTgCheckIn() != null ? Timestamp.valueOf(hd.getTgCheckIn()) : null);
            ps.setTimestamp(9, hd.getTgCheckOut() != null ? Timestamp.valueOf(hd.getTgCheckOut()) : null);

            ps.setBoolean(10, hd.isKieuThanhToan());
            ps.setBoolean(11, hd.isKieuDatBan());
            ps.setInt(12, hd.getTrangthai());
            ps.setInt(13, hd.getSoLuong());
            ps.setString(14, hd.getMoTa());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Lỗi insert: " + e.getMessage());
            return false;
        }
    }


    public static boolean update(HoaDon hd) {
        String sql = """
            UPDATE HoaDon SET
                maKH=?, maNV=?, maBan=?, maKM=?, maSK=?, tgLapHD=?,
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

            ps.setTimestamp(6, hd.getTgLapHD() != null ? Timestamp.valueOf(hd.getTgLapHD()) : null);
            ps.setTimestamp(7, hd.getTgCheckIn() != null ? Timestamp.valueOf(hd.getTgCheckIn()) : null);
            ps.setTimestamp(8, hd.getTgCheckOut() != null ? Timestamp.valueOf(hd.getTgCheckOut()) : null);

            ps.setBoolean(9, hd.isKieuThanhToan());
            ps.setBoolean(10, hd.isKieuDatBan());
            ps.setInt(11, hd.getTrangthai());
            ps.setInt(12, hd.getSoLuong());
            ps.setString(13, hd.getMoTa());
            ps.setString(14, hd.getMaHD());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Lỗi update: " + e.getMessage());
            return false;
        }
    }


    public static boolean delete(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE maHD=?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Lỗi delete: " + e.getMessage());
            return false;
        }
    }


    // =====================================================================
    //                  HÀM KIỂM TRA TRẠNG THÁI BÀN (GIỮ NGUYÊN)
    // =====================================================================
    public static boolean biDatChuaCheckout(String maBan, LocalDateTime tg) {
        String sql = """
            SELECT COUNT(*)
            FROM HoaDon
            WHERE maBan = ?
              AND CAST(tgCheckIn AS DATE) = CAST(? AS DATE)
              AND tgCheckIn <= ?
              AND tgCheckOut IS NULL
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);
            ps.setTimestamp(2, Timestamp.valueOf(tg));
            ps.setTimestamp(3, Timestamp.valueOf(tg));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (Exception e) { e.printStackTrace(); }

        return false;
    }

    public static boolean dangSuDungBan(String maBan) {

        String sql = """
            SELECT COUNT(*)
            FROM HoaDon
            WHERE maBan = ?
              AND tgCheckOut IS NULL
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (Exception e) { e.printStackTrace(); }

        return false;
    }

    public static boolean banDuocSuDungLuc(String maBan, LocalDateTime tg) {

        String sql = """
            SELECT COUNT(*)
            FROM HoaDon
            WHERE maBan = ?
              AND CAST(tgCheckIn AS DATE) = CAST(? AS DATE)
              AND tgCheckIn <= ?
              AND (tgCheckOut IS NULL OR tgCheckOut > ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);
            ps.setTimestamp(2, Timestamp.valueOf(tg));
            ps.setTimestamp(3, Timestamp.valueOf(tg));
            ps.setTimestamp(4, Timestamp.valueOf(tg));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (Exception e) { e.printStackTrace(); }

        return false;
    }


    // =====================================================================
    //                    LẤY MÃ HÓA ĐƠN CUỐI (GIỮ NGUYÊN)
    // =====================================================================
    public static String getMaHDCuoiTheoNgay(String ca, String ngay) {
        String prefix = "HD" + ca + ngay;

        String sql = """
            SELECT TOP 1 maHD
            FROM HoaDon
            WHERE maHD LIKE ?
            ORDER BY maHD DESC
        """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("maHD");

        } catch (SQLException e) {
            System.err.println("Lỗi getMaHDCuoiTheoNgay: " + e.getMessage());
        }

        return null;
    }

    // =====================================================================
//                     THỐNG KÊ (ĐÃ THÊM LOẠI BÀN)
// =====================================================================
    public static Map<HoaDon, Double> getAllForThongKe() {
        Map<HoaDon, Double> ds = new LinkedHashMap<>();

        String sql = """
        WITH ChiTiet_TinhTien AS (
            SELECT
                hd.maHD,
                cthd.maMon,
                cthd.soLuong,
                m.loaiMon,
                m.giaGoc,
                COALESCE(
                    (
                        SELECT TOP 1 p1.phanTramLoi
                        FROM PhanTramGiaBan p1
                        WHERE p1.maMon = m.maMon
                          AND p1.ngayApDung <= hd.tgLapHD
                        ORDER BY p1.ngayApDung DESC
                    ),
                    (
                        SELECT TOP 1 p2.phanTramLoi
                        FROM PhanTramGiaBan p2
                        WHERE p2.maLoaiMon = m.loaiMon
                          AND p2.ngayApDung <= hd.tgLapHD
                        ORDER BY p2.ngayApDung DESC
                    ),
                    0
                ) AS phanTramLoi
            FROM HoaDon hd
            JOIN ChiTietHoaDon cthd ON hd.maHD = cthd.maHD
            JOIN Mon m ON cthd.maMon = m.maMon
        ),
        TongTien AS (
            SELECT
                hd.maHD,
                SUM(COALESCE(ct.soLuong * ct.giaGoc * (1 + ct.phanTramLoi / 100.0), 0)) AS tongTienMon,
                COALESCE(MAX(sk.gia), 0) AS giaSuKien
            FROM HoaDon hd
            LEFT JOIN ChiTiet_TinhTien ct ON hd.maHD = ct.maHD
            LEFT JOIN SuKien sk ON sk.maSK = hd.maSK
            GROUP BY hd.maHD
        )
        SELECT
            hd.*, 
            b.maBan,
            b.maLoaiBan,
            lb.tenLoaiBan,
            lb.soLuong,
            kv.maKhuVuc,
            kv.tenKhuVuc,
            (t.tongTienMon + t.giaSuKien) AS tongTienTruoc,
            ((COALESCE(kh.hangGiam, 0) + COALESCE(km.phanTramGiamGia, 0)) / 100.0)
                * (t.tongTienMon + t.giaSuKien) AS tongTienKhuyenMai,
            (t.tongTienMon + t.giaSuKien) * 0.1 AS thue,
            (t.tongTienMon + t.giaSuKien)
              - ((COALESCE(kh.hangGiam, 0) + COALESCE(km.phanTramGiamGia, 0)) / 100.0)
                * (t.tongTienMon + t.giaSuKien)
              + ((t.tongTienMon + t.giaSuKien) * 0.1) AS tongTienSau
        FROM HoaDon hd
        JOIN TongTien t ON hd.maHD = t.maHD
        LEFT JOIN KhuyenMai km ON km.maKM = hd.maKM
        LEFT JOIN (
            SELECT kh.maKH, hh.giamGia AS hangGiam
            FROM KhachHang kh
            JOIN HangKhachHang hh ON kh.maHang = hh.maHang
        ) kh ON kh.maKH = hd.maKH
        JOIN Ban b ON b.maBan = hd.maBan
        JOIN LoaiBan lb ON lb.maLoaiBan = b.maLoaiBan
        JOIN KhuVuc kv ON kv.maKhuVuc = b.maKhuVuc
        ORDER BY hd.tgLapHD
    """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // ===== KHU VỰC =====
                KhuVuc kv = new KhuVuc();
                kv.setMaKhuVuc(rs.getString("maKhuVuc"));
                kv.setTenKhuVuc(rs.getString("tenKhuVuc"));

                // ===== LOẠI BÀN =====
                LoaiBan lb = new LoaiBan();
                lb.setMaLoaiBan(rs.getString("maLoaiBan"));
                lb.setTenLoaiBan(rs.getString("tenLoaiBan"));
                lb.setSoLuong(rs.getInt("soLuong"));

                // ===== BÀN =====
                Ban b = new Ban();
                b.setMaBan(rs.getString("maBan"));
                b.setKhuVuc(kv);
                b.setLoaiBan(lb);

                // ===== HÓA ĐƠN =====
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setTgLapHD(rs.getTimestamp("tgLapHD") != null
                        ? rs.getTimestamp("tgLapHD").toLocalDateTime() : null);
                hd.setTgCheckOut(rs.getTimestamp("tgCheckOut") != null
                        ? rs.getTimestamp("tgCheckOut").toLocalDateTime() : null);
                hd.setTrangthai(rs.getInt("trangThai"));
                hd.setSoLuong(rs.getInt("soLuong"));
                hd.setBan(b);

                double tongTienSau = rs.getDouble("tongTienSau");

                ds.put(hd, tongTienSau);
            }

        } catch (Exception e) {
            System.err.println("Lỗi thống kê: " + e.getMessage());
        }

        return ds;
    }


}
