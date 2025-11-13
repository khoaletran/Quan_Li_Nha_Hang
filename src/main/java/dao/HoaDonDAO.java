package dao;

import connectDB.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoaDonDAO {

    // ===================== GET ALL =====================
    public static List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HoaDon hd = mapHoaDon(rs);
                ds.add(hd);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
        }
        return ds;
    }

    // ===================== GET ALL NGÀY HÔM NAY =====================
    public static List<HoaDon> getAllNgayHomNay() {
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


    // ===================== GET BY ID =====================
    public static HoaDon getByID(String maHD) {
        String sql = "SELECT * FROM HoaDon WHERE maHD = ?";
        HoaDon hd = null;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hd = mapHoaDon(rs);
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
                        maHD, maKH, maNV, maBan, maKM, maSK, tgLapHd,
                        tgCheckin, tgCheckout, kieuThanhToan, kieuDatBan,
                        trangThai, soLuong, moTa
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hóa đơn: " + e.getMessage());
            return false;
        }
    }

    // ===================== UPDATE =====================
    public static boolean update(HoaDon hd) {
        String sql = """
                    UPDATE HoaDon SET 
                        maKH=?, maNV=?, maBan=?, maKM=?, maSK=?, tgLapHD = ?,
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

    // ===================== GET THEO MÃ NHÂN VIÊN =====================
    public static List<HoaDon> getTheoMaNV(String maNV) {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE maNV = ?";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = mapHoaDon(rs);
                    ds.add(hd);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn theo mã nhân viên: " + e.getMessage());
        }
        return ds;
    }

    // ===================== MAP TỪ RESULTSET -> HÓA ĐƠN =====================
    private static HoaDon mapHoaDon(ResultSet rs) throws SQLException {
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
        LocalDateTime tgLapHD = rs.getTimestamp("tgLapHD") != null
                ? rs.getTimestamp("tgLapHD").toLocalDateTime()
                : null;
        LocalDateTime checkIn = rs.getTimestamp("tgCheckin") != null
                ? rs.getTimestamp("tgCheckin").toLocalDateTime()
                : null;
        LocalDateTime checkOut = rs.getTimestamp("tgCheckout") != null
                ? rs.getTimestamp("tgCheckout").toLocalDateTime()
                : null;

        HoaDon hd = new HoaDon();
        hd.setMaHD(rs.getString("maHD"));
        hd.setKhachHang(kh);
        hd.setNhanVien(nv);
        hd.setBan(ban);
        hd.setKhuyenMai(km);
        hd.setSuKien(sk);
        if (tgLapHD != null)
            hd.setTgLapHD(tgLapHD);
        if (checkIn != null)
            hd.setTgCheckIn(checkIn);
        if (checkOut != null && checkIn != null && checkOut.isAfter(checkIn))
            hd.setTgCheckOut(checkOut);
        else
            hd.setTgCheckOut(null);

        hd.setKieuThanhToan(rs.getBoolean("kieuThanhToan"));
        hd.setKieuDatBan(rs.getBoolean("kieuDatBan"));
        hd.setTrangthai(rs.getInt("trangThai"));
        hd.setSoLuong(rs.getInt("soLuong"));
        hd.setMoTa(rs.getString("moTa"));

        return hd;
    }

    // ===================== GET ALL WAITLIST CHỜ =====================
    public static List<HoaDon> getAllWaitlistCho() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = """
                    SELECT 
                        hd.maHD, hd.maKH, hd.maNV, hd.maBan, hd.maSK,
                        hd.tgLapHD, hd.tgCheckin,
                        hd.kieuThanhToan, hd.kieuDatBan,
                        hd.trangThai, hd.soLuong, hd.moTa
                    FROM HoaDon hd
                    WHERE hd.kieuDatBan = 0 AND hd.trangThai = 0 AND maBan LIKE 'W%'
                    ORDER BY hd.maHD DESC
                """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // ========== HÓA ĐƠN ==========
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setSoLuong(rs.getInt("soLuong"));
                hd.setTrangthai(rs.getInt("trangThai"));
                hd.setKieuDatBan(rs.getBoolean("kieuDatBan"));
                hd.setKieuThanhToan(rs.getBoolean("kieuThanhToan"));
                hd.setMoTa(rs.getString("moTa"));

                Timestamp tgLap = rs.getTimestamp("tgLapHD");
                if (tgLap != null)
                    hd.setTgLapHD(tgLap.toLocalDateTime());

                Timestamp tgCheckin = rs.getTimestamp("tgCheckin");
                if (tgCheckin != null)
                    hd.setTgCheckIn(tgCheckin.toLocalDateTime());

                // ========== KHÁCH HÀNG / NHÂN VIÊN / BÀN / SỰ KIỆN ==========
                String maKH = rs.getString("maKH");
                String maNV = rs.getString("maNV");
                String maBan = rs.getString("maBan");
                String maSK = rs.getString("maSK");

                if (maKH != null)
                    hd.setKhachHang(KhachHangDAO.getByID(maKH));
                if (maNV != null)
                    hd.setNhanVien(NhanVienDAO.getByID(maNV));
                if (maBan != null)
                    hd.setBan(BanDAO.getByID(maBan));
                if (maSK != null)
                    hd.setSuKien(SuKienDAO.getByID(maSK));

                ds.add(hd);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getAllWaitlistCho: " + e.getMessage());
        }

        return ds;
    }

    public static Map<HoaDon, Double> getAllForThongKe() {
        Map<HoaDon, Double> ds = new LinkedHashMap<>();
        String sql =
                """      
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
                            hd.maHD,
                            hd.tgLapHD,
                            hd.tgCheckOut,
                            hd.trangThai,
                        	hd.maBan,
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
                        join Ban b on b.maBan = hd.maBan
                        join KhuVuc kv on kv.maKhuVuc = b.maKhuVuc
                        order by hd.tgLapHD
                        """;
        try (
                Connection conn = connectDB.getInstance().getNewConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while(rs.next()){
                KhuVuc kv = new KhuVuc();
                kv.setMaKhuVuc(rs.getString("maKhuVuc"));
                kv.setTenKhuVuc(rs.getString("tenKhuVuc"));

                Ban b = new Ban();
                b.setMaBan(rs.getString("maBan"));
                b.setKhuVuc(kv);

                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setTgLapHD(rs.getTimestamp("tgLapHD") != null
                        ? rs.getTimestamp("tgLapHD").toLocalDateTime() : null);
                hd.setTgCheckOut(rs.getTimestamp("tgCheckOut") != null
                        ? rs.getTimestamp("tgCheckOut").toLocalDateTime() : null);
                hd.setTrangthai(rs.getInt("trangThai"));
                hd.setBan(b);

                Double tongTienSau = rs.getDouble("tongTienSau");
                ds.put(hd, tongTienSau);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }


}
