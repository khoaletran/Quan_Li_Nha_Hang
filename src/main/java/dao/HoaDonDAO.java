package dao;

import connectDB.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class HoaDonDAO {

    // =====================================================================
    //                          MAPPER FULL
    // =====================================================================
    private static HoaDon mapFullHoaDon(ResultSet rs) throws SQLException {

        // ===== HẠNG KHÁCH =====
        HangKhachHang hang = null;
        if (rs.getString("maHang") != null) {
            hang = new HangKhachHang();
            hang.setMaHang(rs.getString("maHang"));
            hang.setDiemHang(rs.getInt("diemHang"));
            hang.setGiamGia(rs.getInt("giamGiaHang"));
            hang.setMoTa(rs.getString("moTaHang"));
        }
        // ===== KHÁCH HÀNG =====
        KhachHang kh = null;
        if (rs.getString("maKH") != null) {
            kh = new KhachHang();
            kh.setMaKhachHang(rs.getString("maKH"));
            kh.setTenKhachHang(rs.getString("tenKH"));
            kh.setSdt(rs.getString("sdtKH"));
            kh.setGioiTinh(rs.getBoolean("gioiTinhKH"));
            kh.setDiemTichLuy(rs.getInt("diemTichLuy"));
            kh.setHangKhachHang(hang);
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
            km.setUuDai(rs.getBoolean("uuDai"));
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
               kh.maHang,
               hh.diemHang, hh.giamGia AS giamGiaHang, hh.moTa AS moTaHang,
        
               nv.tenNV, nv.sdt AS sdtNV,
               b.trangThai AS trangThaiBan, b.maKhuVuc, b.maLoaiBan,
               kv.tenKhuVuc,
               lb.tenLoaiBan, lb.soLuong,
               km.tenKM, km.uuDai ,km.phanTramGiamGia,
               sk.tenSK, sk.gia AS giaSK
        FROM HoaDon hd
        LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH
        LEFT JOIN HangKhachHang hh ON kh.maHang = hh.maHang
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

    public List<HoaDon> searchHoaDon(String keyword,
                                     Integer trangThai,
                                     LocalDate ngay,
                                     String tenKhuVuc) {

        List<HoaDon> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_FULL).append(" WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // ===== KEYWORD (tìm dưới DB) =====
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();

            boolean isPhone = kw.matches("^0[3-9]\\d{8}$");   // sđt 10 số
            boolean isMaHD  = kw.toUpperCase().startsWith("HD");

            if (isMaHD) {
                sql.append(" AND hd.maHD LIKE ? ");
                params.add(kw.toUpperCase() + "%");
            } else if (isPhone) {
                sql.append(" AND kh.sdt LIKE ? ");
                params.add(kw + "%");
            } else {
                sql.append(" AND (hd.maHD LIKE ? OR kh.sdt LIKE ? OR kh.tenKH LIKE ? OR b.maBan LIKE ?) ");
                params.add(kw.toUpperCase() + "%");
                params.add(kw + "%");
                params.add("%" + kw + "%");          // tên KH nên contains
                params.add(kw.toUpperCase() + "%");
            }
        }

        // ===== TRẠNG THÁI =====
        if (trangThai != null) {
            sql.append(" AND hd.trangThai = ? ");
            params.add(trangThai);
        }

        // ===== NGÀY (đặt trước theo tgLapHD, còn lại theo tgCheckin) =====
        if (ngay != null) {
            if (trangThai != null && trangThai == 0) {
                sql.append(" AND hd.tgLapHD IS NOT NULL AND CAST(hd.tgLapHD AS DATE) = ? ");
                params.add(java.sql.Date.valueOf(ngay));
            } else if (trangThai != null) {
                sql.append(" AND hd.tgCheckin IS NOT NULL AND CAST(hd.tgCheckin AS DATE) = ? ");
                params.add(java.sql.Date.valueOf(ngay));
            } else {
                sql.append(" AND ( (hd.trangThai = 0 AND hd.tgLapHD IS NOT NULL AND CAST(hd.tgLapHD AS DATE) = ?) ")
                        .append("    OR (hd.trangThai <> 0 AND hd.tgCheckin IS NOT NULL AND CAST(hd.tgCheckin AS DATE) = ?) ) ");
                params.add(java.sql.Date.valueOf(ngay));
                params.add(java.sql.Date.valueOf(ngay));
            }
        }

        // ===== KHU VỰC (combo đang load tenKhuVuc từ DB) =====
        // Nếu tenKhuVuc = null/"Tất cả" -> không lọc.
        if (tenKhuVuc != null && !tenKhuVuc.isBlank() && !"Tất cả".equalsIgnoreCase(tenKhuVuc)) {
            sql.append(" AND kv.tenKhuVuc = ? ");
            params.add(tenKhuVuc);
        }

        sql.append(" ORDER BY hd.tgLapHD DESC ")
                .append(" OFFSET 0 ROWS FETCH NEXT 100 ROWS ONLY ");


        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof java.sql.Date) {
                    ps.setDate(i + 1, (java.sql.Date) p);
                } else if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setString(i + 1, String.valueOf(p));
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFullHoaDon(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi searchHoaDon: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
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

    public static List<HoaDon> getAllToday() {
        List<HoaDon> ds = new ArrayList<>();

        String sql = """
                SELECT hd.maHD, hd.maKH, hd.maKM, hd.maNV, hd.maBan, hd.maSK,
                       hd.tgLapHD, hd.tgCheckin, hd.kieuDatBan, hd.moTa, hd.trangThai, hd.soLuong,
                       kh.tenKH, kh.sdt, sk.tenSK, b.maKhuVuc, kv.tenKhuVuc
                FROM HoaDon hd
                JOIN KhachHang kh ON hd.maKH = kh.maKH
                JOIN Ban b ON hd.maBan = b.maBan
                JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
                JOIN NhanVien nv ON hd.maNV = nv.maNV
                LEFT JOIN KhuyenMai km ON km.maKM = hd.maKM
                LEFT JOIN SuKien sk ON hd.maSK = sk.maSK
                WHERE
                    tgCheckin is not null and
                (
                   (hd.kieuDatBan = 1
                    AND (hd.tgCheckin IS NULL OR
                         (hd.tgCheckin >= CAST(GETDATE() AS DATE)
                          AND hd.tgCheckin < DATEADD(DAY, 1, CAST(GETDATE() AS DATE)))))
                   OR
                   (hd.kieuDatBan = 0
                    AND (hd.tgLapHD IS NULL OR
                         (hd.tgLapHD >= CAST(GETDATE() AS DATE)
                          AND hd.tgLapHD < DATEADD(DAY, 1, CAST(GETDATE() AS DATE)))))
                )""";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(rs.getString("maKH"));
                kh.setTenKhachHang(rs.getString("tenKH"));
                kh.setSdt(rs.getString("sdt"));

                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));

                Ban ban = new Ban();
                ban.setMaBan(rs.getString("maBan"));

                KhuVuc kv = new KhuVuc();
                kv.setMaKhuVuc(rs.getString("maKhuVuc"));
                kv.setTenKhuVuc(rs.getString("tenKhuVuc"));
                ban.setKhuVuc(kv);

                KhuyenMai km = null;
                if (rs.getString("maKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("maKM"));
                }

                SuKien sk = null;
                if (rs.getString("maSK") != null) {
                    sk = new SuKien();
                    sk.setMaSK(rs.getString("maSK"));
                    sk.setTenSK(rs.getString("tenSK"));
                }

                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setKhachHang(kh);
                hd.setNhanVien(nv);
                hd.setBan(ban);
                hd.setKhuyenMai(km);
                hd.setSuKien(sk);
                hd.setTgLapHD(rs.getTimestamp("tgLapHD") != null
                        ? rs.getTimestamp("tgLapHD").toLocalDateTime() : null);
                hd.setTgCheckIn(rs.getTimestamp("tgCheckin") != null
                        ? rs.getTimestamp("tgCheckin").toLocalDateTime() : null);
                hd.setKieuDatBan(rs.getBoolean("kieuDatBan"));
                hd.setTrangthai(rs.getInt("trangThai"));
                hd.setSoLuong(rs.getInt("soLuong"));
                hd.setMoTa(rs.getString("moTa"));

                ds.add(hd);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn hôm nay: " + e.getMessage());
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
                hd.setTgCheckIn(rs.getTimestamp("tgCheckIn") != null
                        ? rs.getTimestamp("tgCheckIn").toLocalDateTime() : null);
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

    public static Map<HoaDon, Double> getAllForThongKeInDay() {
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
        WHERE hd.tgCheckin >= CAST(GETDATE() AS DATE)
		AND hd.tgCheckin < DATEADD(DAY, 1, CAST(GETDATE() AS DATE))
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

    // Lấy tất cả hóa đơn có trangThai = 0 (ví dụ: ĐẶT TRƯỚC)
    public static List<HoaDon> getAllTrangThai(int trangThai) {
        List<HoaDon> ds = new ArrayList<>();

        String sql = SELECT_FULL + " WHERE hd.trangThai = ? AND b.maBan LIKE 'B%'";
        //AND hd.tgCheckin > GETDATE()

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapFullHoaDon(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi getAllTrangThai: " + e.getMessage());
        }

        return ds;
    }
    public static List<HoaDon> getHoaDonTuHomNayTroVeSau() {
        List<HoaDon> ds = new ArrayList<>();

        String sql = SELECT_FULL + """
        WHERE
            b.maBan like 'B%' and
        (
            (hd.trangThai = 0
             AND hd.tgLapHD IS NOT NULL
             AND CAST(hd.tgLapHD AS DATE) >= CAST(GETDATE() AS DATE)
            )
            OR
            (hd.trangThai <> 0
             AND hd.tgCheckin IS NOT NULL
             AND CAST(hd.tgCheckin AS DATE) >= CAST(GETDATE() AS DATE)
            )
        )
        ORDER BY COALESCE(hd.tgCheckin, hd.tgLapHD) DESC
    """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(mapFullHoaDon(rs));
            }

        } catch (Exception e) {
            System.err.println("Lỗi getHoaDonTuHomNayTroVeSau: " + e.getMessage());
            e.printStackTrace();
        }

        return ds;
    }



    public static List<HoaDon> getAllDatTruoc() {
        return getAllTrangThai(0);   // 0 = Đặt trước
    }

    public static List<HoaDon> getAllDaNhan() {
        return getAllTrangThai(1);   // 1 = Đã nhận
    }


}
