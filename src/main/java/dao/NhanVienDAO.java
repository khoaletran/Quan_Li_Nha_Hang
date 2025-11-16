package dao;

import connectDB.connectDB;
import entity.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    // ==============================
    // MAP OBJECT
    // ==============================
    private static NhanVien map(ResultSet rs) throws SQLException {
        return new NhanVien(
                rs.getString("maNV"),
                rs.getString("tenNV"),
                rs.getString("sdt"),
                rs.getBoolean("gioiTinh"),
                rs.getBoolean("quanLi"),
                rs.getDate("ngayVaoLam").toLocalDate(),
                rs.getBoolean("trangThai"),
                rs.getString("matKhau")
        );
    }

    // ==============================
    // GET ALL
    // ==============================
    public static List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNV";

        try (Connection con = connectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            System.err.println("NhanVienDAO.getAll(): " + e.getMessage());
        }

        return list;
    }

    // ==============================
    // INSERT
    // ==============================
    public static boolean insert(NhanVien nv) {
        String sql = """
            INSERT INTO NhanVien(maNV, tenNV, sdt, gioiTinh, quanLi, ngayVaoLam, trangThai, matKhau)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getTenNV());
            ps.setString(3, nv.getSdt());
            ps.setBoolean(4, nv.isGioiTinh());
            ps.setBoolean(5, nv.isQuanLi());
            ps.setDate(6, Date.valueOf(nv.getNgayVaoLam()));
            ps.setBoolean(7, nv.isTrangThai());
            ps.setString(8, nv.getMatKhau());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("NhanVienDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public static boolean update(NhanVien nv) {
        String sql = """
            UPDATE NhanVien
            SET tenNV=?, sdt=?, gioiTinh=?, quanLi=?, ngayVaoLam=?, trangThai=?, matKhau=?
            WHERE maNV=?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nv.getTenNV());
            ps.setString(2, nv.getSdt());
            ps.setBoolean(3, nv.isGioiTinh());
            ps.setBoolean(4, nv.isQuanLi());
            ps.setDate(5, Date.valueOf(nv.getNgayVaoLam()));
            ps.setBoolean(6, nv.isTrangThai());
            ps.setString(7, nv.getMatKhau());
            ps.setString(8, nv.getMaNV());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("NhanVienDAO.update(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public static boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("NhanVienDAO.delete(): " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // GET BY ID
    // ==============================
    public static NhanVien getByID(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE maNV = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("NhanVienDAO.getByID(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // LẤY MÃ NV CUỐI
    // ==============================
    public static String maNVCuoi() {
        String sql = "SELECT TOP 1 maNV FROM NhanVien ORDER BY maNV DESC";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getString("maNV");

        } catch (SQLException e) {
            System.err.println("NhanVienDAO.maNVCuoi(): " + e.getMessage());
        }

        return null;
    }

    // ==============================
    // CẬP NHẬT MẬT KHẨU
    // ==============================
    public static boolean updateMatKhau(String maNV, String matKhauMoi) {
        String sql = "UPDATE NhanVien SET matKhau = ? WHERE maNV = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, matKhauMoi);
            ps.setString(2, maNV);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("NhanVienDAO.updateMatKhau(): " + e.getMessage());
            return false;
        }
    }
}
