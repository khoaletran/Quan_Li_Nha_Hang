package dao;

import connectDB.connectDB;
import entity.KhuyenMai;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    // ==============================
    // MAP OBJECT
    // ==============================
    private static KhuyenMai map(ResultSet rs) throws SQLException {

        LocalDate ngayPhatHanh = rs.getDate("ngayPhatHanh") != null
                ? rs.getDate("ngayPhatHanh").toLocalDate() : null;

        LocalDate ngayKetThuc = rs.getDate("ngayKetThuc") != null
                ? rs.getDate("ngayKetThuc").toLocalDate() : null;

        return new KhuyenMai(
                rs.getString("maKM"),
                rs.getString("tenKM"),
                rs.getInt("soLuong"),
                ngayPhatHanh,
                ngayKetThuc,
                rs.getString("maThayThe"),
                rs.getInt("phanTramGiamGia"),
                rs.getBoolean("uuDai")
        );
    }

    // ==============================
    // GET ALL
    // ==============================
    public static List<KhuyenMai> getAll() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai ORDER BY maKM";

        try (Connection con = connectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) ds.add(map(rs));

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }

    // ==============================
    // INSERT
    // ==============================
    public static boolean insert(KhuyenMai km) {
        String sql = """
            INSERT INTO KhuyenMai(
                maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc,
                maThayThe, phanTramGiamGia, uuDai
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setInt(3, km.getSoLuong());
            ps.setDate(4, km.getNgayPhatHanh() != null ? Date.valueOf(km.getNgayPhatHanh()) : null);
            ps.setDate(5, km.getNgayKetThuc() != null ? Date.valueOf(km.getNgayKetThuc()) : null);
            ps.setString(6, km.getMaThayThe());
            ps.setInt(7, km.getPhanTRamGiamGia());
            ps.setBoolean(8, km.isUuDai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public static boolean update(KhuyenMai km) {
        String sql = """
            UPDATE KhuyenMai
            SET tenKM=?, soLuong=?, ngayPhatHanh=?, ngayKetThuc=?,
                maThayThe=?, phanTramGiamGia=?, uuDai=?
            WHERE maKM=?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, km.getTenKM());
            ps.setInt(2, km.getSoLuong());
            ps.setDate(3, km.getNgayPhatHanh() != null ? Date.valueOf(km.getNgayPhatHanh()) : null);
            ps.setDate(4, km.getNgayKetThuc() != null ? Date.valueOf(km.getNgayKetThuc()) : null);
            ps.setString(5, km.getMaThayThe());
            ps.setInt(6, km.getPhanTRamGiamGia());
            ps.setBoolean(7, km.isUuDai());
            ps.setString(8, km.getMaKM());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.update(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public static boolean delete(String maKM) {
        String sql = "DELETE FROM KhuyenMai WHERE maKM=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.delete(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // GET BY ID
    // ==============================
    public static KhuyenMai getByID(String maKM) {
        String sql = "SELECT * FROM KhuyenMai WHERE maKM = ?";

        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.getByID(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // LẤY MÃ KM CUỐI
    // ==============================
    public static String maKMCuoi() {
        String sql = "SELECT TOP 1 maKM FROM KhuyenMai ORDER BY maKM DESC";

        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getString("maKM");

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.maKMCuoi(): " + e.getMessage());
        }

        return null;
    }
    public static String taoMaKMTiepTheo() {
        String maCuoi = maKMCuoi(); // ví dụ KM0012
        int soMoi = 1;

        if (maCuoi != null && maCuoi.startsWith("KM")) {
            try {
                soMoi = Integer.parseInt(maCuoi.substring(2)) + 1;
            } catch (NumberFormatException ignored) {}
        }

        return String.format("KM%04d", soMoi); // KM0001
    }

    // ==============================
    // GET BY CODE (maKM hoặc maThayThe)
    // ==============================
    public static KhuyenMai getByCode(String code) {
        String sql = "SELECT TOP 1 * FROM KhuyenMai WHERE maKM = ? OR maThayThe = ?";

        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.getByCode(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // GIẢM SỐ LƯỢNG ATOMIC (soLuong > 0 mới giảm)
    // ==============================
    public static boolean giamSoLuongAtomic(String maKM) {
        String sql = "UPDATE KhuyenMai SET soLuong = soLuong - 1 WHERE maKM = ? AND soLuong > 0";

        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            return ps.executeUpdate() > 0; // >0 nghĩa là giảm được

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.giamSoLuongAtomic(): " + e.getMessage());
            return false;
        }
    }
    // ==============================
    // TĂNG SỐ LƯỢNG ATOMIC
    // ==============================
    public static boolean tangSoLuongAtomic(String maKM) {
        String sql = "UPDATE KhuyenMai SET soLuong = soLuong + 1 WHERE maKM = ?";

        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("KhuyenMaiDAO.tangSoLuongAtomic(): " + e.getMessage());
            return false;
        }
    }

    public static boolean insertVoucherHuyDatBan(
            String maHDHuy,
            int phanTramGiamGia,
            LocalDate ngayHetHan
    ) {
        String maKM = taoMaKMTiepTheo();

        String sql = """
        INSERT INTO KhuyenMai(
            maKM, tenKM, soLuong,
            ngayPhatHanh, ngayKetThuc,
            maThayThe, phanTramGiamGia, uuDai
        )
        VALUES (?, ?, 1, ?, ?, NULL, ?, 1)
    """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKM);
            ps.setString(2, maHDHuy);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setDate(4, Date.valueOf(ngayHetHan));
            ps.setInt(5, phanTramGiamGia);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("insertVoucherHuyDatBan(): " + e.getMessage());
            return false;
        }
    }



}
