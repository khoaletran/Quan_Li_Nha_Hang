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

    public static List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = """
        SELECT 
            hd.maHD AS hd_maHD, hd.maKH AS hd_maKH, hd.maNV AS hd_maNV, hd.maBan AS hd_maBan,
            hd.maKM AS hd_maKM, hd.maSK AS hd_maSK,
            hd.tgCheckin AS hd_tgCheckin, hd.tgCheckout AS hd_tgCheckout,
            hd.kieuThanhToan AS hd_kieuThanhToan, hd.kieuDatBan AS hd_kieuDatBan,
            hd.thue AS hd_thue, hd.coc AS hd_coc, hd.trangThai AS hd_trangThai,
            hd.tongTienTruoc AS hd_tongTienTruoc, hd.tongTienSau AS hd_tongTienSau, hd.tongTienKM AS hd_tongTienKM,

            kh.maKH AS kh_maKH, kh.tenKH AS kh_tenKH, kh.sdt AS kh_sdt, kh.gioiTinh AS kh_gioiTinh, kh.diemTichLuy AS kh_diemTichLuy,

            nv.maNV AS nv_maNV, nv.tenNV AS nv_tenNV,

            b.maBan AS b_maBan, b.trangThai AS b_trangThai,

            km.maKM AS km_maKM, km.tenKM AS km_tenKM, km.soLuong AS km_soLuong,
            km.ngayPhatHanh AS km_ngayPhatHanh, km.ngayKetThuc AS km_ngayKetThuc,
            km.maThayThe AS km_maThayThe, km.phanTramGiamGia AS km_phanTramGiamGia, km.uuDai AS km_uuDai,

            sk.maSK AS sk_maSK, sk.tenSK AS sk_tenSK, sk.mota AS sk_mota, sk.gia AS sk_gia
        FROM HoaDon hd
        LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH
        LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV
        LEFT JOIN Ban b ON hd.maBan = b.maBan
        LEFT JOIN KhuyenMai km ON hd.maKM = km.maKM
        LEFT JOIN SuKien sk ON hd.maSK = sk.maSK
    """;

        Connection conn = null;
        try {
            // Lấy connection và kiểm tra trạng thái
            conn = connectDB.getConnection(); // hoặc connectDB.getInstance().getConnection() tùy implement của bạn
            System.out.println("DEBUG: initial conn = " + conn);
            if (conn == null || conn.isClosed()) {
                System.out.println("DEBUG: connection null/closed -> thử gọi connect() để tạo kết nối mới");
                // Thử gọi lại phương thức connect nếu bạn có (có thể khác tên)
                try {
                    connectDB.getInstance().connect(); // nếu class của bạn cung cấp
                } catch (Exception exConnect) {
                    System.err.println("DEBUG: connect() throw: " + exConnect.getMessage());
                }
                conn = connectDB.getConnection();
                System.out.println("DEBUG: conn after reconnect = " + conn);
            }

            if (conn == null) {
                throw new SQLException("Không thể tạo connection (conn == null). Kiểm tra connectDB.");
            }
            if (conn.isClosed()) {
                throw new SQLException("Connection vẫn đóng sau khi cố reconnect.");
            }

            // Dùng try-with-resources cho Statement/ResultSet, không đóng conn nếu bạn dùng kết nối chia sẻ
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    // --- (mapping giống như bạn đã có) ---
                    // ----- KHÁCH HÀNG -----
                    KhachHang kh = null;
                    String kh_ma = rs.getString("kh_maKH");
                    if (kh_ma != null) {
                        Integer diemTichLuy = rs.getObject("kh_diemTichLuy") != null ? rs.getInt("kh_diemTichLuy") : null;
                        Boolean gioiTinh = rs.getObject("kh_gioiTinh") != null ? rs.getBoolean("kh_gioiTinh") : null;
                        kh = new KhachHang(
                                kh_ma,
                                diemTichLuy != null ? diemTichLuy : 0,
                                gioiTinh != null ? gioiTinh : false,
                                rs.getString("kh_sdt"),
                                rs.getString("kh_tenKH"),
                                null
                        );
                    }

                    // ----- NHAN VIEN -----
                    NhanVien nv = null;
                    String nv_ma = rs.getString("nv_maNV");
                    if (nv_ma != null) {
                        nv = new NhanVien();
                        nv.setMaNV(nv_ma);
                        nv.setTenNV(rs.getString("nv_tenNV"));
                    }

                    // ----- BAN -----
                    Ban ban = null;
                    String b_ma = rs.getString("b_maBan");
                    if (b_ma != null) {
                        Boolean trangThaiBan = rs.getObject("b_trangThai") != null ? rs.getBoolean("b_trangThai") : false;
                        ban = new Ban(b_ma, null, null, trangThaiBan);
                    }

                    // ----- KHUYEN MAI -----
                    KhuyenMai km = null;
                    String km_ma = rs.getString("km_maKM");
                    if (km_ma != null) {
                        Integer soLuongKM = rs.getObject("km_soLuong") != null ? rs.getInt("km_soLuong") : 0;
                        java.sql.Date ngPh = rs.getDate("km_ngayPhatHanh");
                        java.sql.Date ngKt = rs.getDate("km_ngayKetThuc");
                        Integer phanTram = rs.getObject("km_phanTramGiamGia") != null ? rs.getInt("km_phanTramGiamGia") : 0;
                        Boolean uuDai = rs.getObject("km_uuDai") != null ? rs.getBoolean("km_uuDai") : false;

                        km = new KhuyenMai(
                                km_ma,
                                rs.getString("km_tenKM"),
                                soLuongKM,
                                ngPh != null ? ngPh.toLocalDate() : null,
                                ngKt != null ? ngKt.toLocalDate() : null,
                                rs.getString("km_maThayThe"),
                                phanTram,
                                uuDai
                        );
                    }

                    // ----- SU KIEN -----
                    SuKien sk = null;
                    String sk_ma = rs.getString("sk_maSK");
                    if (sk_ma != null) {
                        Double gia = rs.getObject("sk_gia") != null ? rs.getDouble("sk_gia") : 0.0;
                        sk = new SuKien(sk_ma, rs.getString("sk_tenSK"), rs.getString("sk_mota"), gia);
                    }

                    // ----- HOA DON -----
                    String hd_maHD = rs.getString("hd_maHD");
                    Double tongTienTruoc = rs.getObject("hd_tongTienTruoc") != null ? rs.getDouble("hd_tongTienTruoc") : 0.0;
                    Double tongTienKM = rs.getObject("hd_tongTienKM") != null ? rs.getDouble("hd_tongTienKM") : 0.0;
                    Double tongTienSau = rs.getObject("hd_tongTienSau") != null ? rs.getDouble("hd_tongTienSau") : 0.0;
                    Double cocVal = rs.getObject("hd_coc") != null ? rs.getDouble("hd_coc") : 0.0;
                    Double thueVal = rs.getObject("hd_thue") != null ? rs.getDouble("hd_thue") : 0.0;
                    Boolean kieuDatBan = rs.getObject("hd_kieuDatBan") != null ? rs.getBoolean("hd_kieuDatBan") : false;
                    Boolean kieuThanhToan = rs.getObject("hd_kieuThanhToan") != null ? rs.getBoolean("hd_kieuThanhToan") : false;
                    int trangThai = rs.getObject("hd_trangThai") != null ? rs.getInt("hd_trangThai") : 0;

                    java.sql.Timestamp tsCheckin = rs.getTimestamp("hd_tgCheckin");
                    java.sql.Timestamp tsCheckout = rs.getTimestamp("hd_tgCheckout");

                    HoaDon hd = new HoaDon(
                            hd_maHD,
                            tongTienKM,
                            tongTienSau,
                            tongTienTruoc,
                            cocVal,
                            thueVal,
                            kieuDatBan,
                            kieuThanhToan,
                            km,
                            sk,
                            trangThai,
                            tsCheckout != null ? tsCheckout.toLocalDateTime() : null,
                            tsCheckin  != null ? tsCheckin.toLocalDateTime()  : null,
                            ban,
                            nv,
                            kh
                    );

                    ds.add(hd);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
            e.printStackTrace(); // in stack trace để debug nguyên nhân close
        }
        return ds;
    }




    // ================== THÊM HÓA ĐƠN ==================
    public static boolean insert(HoaDon hd) {
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
    public static boolean update(HoaDon hd) {
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
    public static boolean delete(String maHD) {
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
