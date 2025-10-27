package dao;

import connectDB.connectDB;
import entity.NhanVien;
import java.sql.*;
import java.util.ArrayList;

public class NhanVienDAO {

    public static ArrayList<NhanVien> getAll() {
        ArrayList<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    list.add(new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("tenNV"),
                            rs.getString("sdt"),
                            rs.getBoolean("gioiTinh"),
                            rs.getBoolean("quanLi"),
                            rs.getDate("ngayVaoLam").toLocalDate(),
                            rs.getBoolean("trangThai"),
                            rs.getString("matKhau")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi truy xuất NhanVien: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean insert(NhanVien nv) {
        String sql = """
            INSERT INTO NhanVien(maNV, tenNV, sdt, gioiTinh, quanLi, ngayVaoLam, trangThai, matKhau)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nv.getMaNV());
                ps.setString(2, nv.getTenNV());
                ps.setString(3, nv.getSdt());
                ps.setBoolean(4, nv.isGioiTinh());
                ps.setBoolean(5, nv.isQuanLi());
                ps.setDate(6, Date.valueOf(nv.getNgayVaoLam()));
                ps.setBoolean(7, nv.isTrangThai());
                ps.setString(8, nv.getMatKhau());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm NhanVien: " + e.getMessage());
            return false;
        }
    }

    public static boolean update(NhanVien nv) {
        String sql = """
            UPDATE NhanVien
            SET tenNV=?, sdt=?, gioiTinh=?, quanLi=?, ngayVaoLam=?, trangThai=?, matKhau=?
            WHERE maNV=?
        """;
        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nv.getTenNV());
                ps.setString(2, nv.getSdt());
                ps.setBoolean(3, nv.isGioiTinh());
                ps.setBoolean(4, nv.isQuanLi());
                ps.setDate(5, Date.valueOf(nv.getNgayVaoLam()));
                ps.setBoolean(6, nv.isTrangThai());
                ps.setString(7, nv.getMatKhau());
                ps.setString(8, nv.getMaNV());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật NhanVien: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV=?";
        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maNV);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa NhanVien: " + e.getMessage());
            return false;
        }
    }

    public static NhanVien getByID(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE maNV = ?";
        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maNV);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
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
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy nhân viên theo mã: " + e.getMessage());
        }
        return null;
    }

    public static String maNVCuoi() {
        String sql = "SELECT TOP 1 maNV FROM NhanVien ORDER BY maNV DESC";
        String maNVCuoi = null;

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    maNVCuoi = rs.getString("maNV");
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy mã nhân viên cuối: " + e.getMessage());
        }
        return maNVCuoi;
    }

    public static boolean updateMatKhau(String maNV, String matKhauMoi) {
        String sql = "UPDATE NhanVien SET matKhau = ? WHERE maNV = ?";
        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, matKhauMoi);
                ps.setString(2, maNV);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật mật khẩu: " + e.getMessage());
            return false;
        }
    }
}
