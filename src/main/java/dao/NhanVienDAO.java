package dao;

import connectDB.connectDB;
import entity.NhanVien;

import java.sql.*;
import java.util.ArrayList;

public class NhanVienDAO {

    // ===================== LẤY DANH SÁCH NHÂN VIÊN =====================
    public static ArrayList<NhanVien> getAll() {
        ArrayList<NhanVien> list = new ArrayList<>();

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            String sql = "SELECT * FROM NhanVien";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String id = rs.getString("maNV");
                String name = rs.getString("tenNV");
                String sdt = rs.getString("sdt");
                boolean gioitinh = rs.getBoolean("gioiTinh");
                boolean quanLi = rs.getBoolean("quanLi");
                Date ngayVaoLam = rs.getDate("ngayVaoLam");
                boolean trangThai = rs.getBoolean("trangThai");
                String matKhau = rs.getString("matKhau");

                NhanVien nv = new NhanVien(
                        id, name, sdt, gioitinh, quanLi,
                        ngayVaoLam.toLocalDate(),
                        trangThai, matKhau
                );
                list.add(nv);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.err.println("Lỗi khi truy xuất NhanVien: " + e.getMessage());
        }
        return list;
    }

    // ===================== THÊM NHÂN VIÊN =====================
    public static boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien(maNV, tenNV, sdt, gioiTinh, quanLi, ngayVaoLam, trangThai, matKhau) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getTenNV());
            ps.setString(3, nv.getSdt());
            ps.setBoolean(4, nv.isGioiTinh());
            ps.setBoolean(5, nv.isQuanLi());
            ps.setDate(6, Date.valueOf(nv.getNgayVaoLam()));
            ps.setBoolean(7, nv.isTrangThai());
            ps.setString(8, nv.getMatKhau());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi thêm NhanVien: " + e.getMessage());
            return false;
        }
    }

    // ===================== CẬP NHẬT NHÂN VIÊN =====================
    public static boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET tenNV=?, sdt=?, gioiTinh=?, quanLi=?, ngayVaoLam=?, trangThai=?, matKhau=? WHERE maNV=?";

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, nv.getTenNV());
            ps.setString(2, nv.getSdt());
            ps.setBoolean(3, nv.isGioiTinh());
            ps.setBoolean(4, nv.isQuanLi());
            ps.setDate(5, Date.valueOf(nv.getNgayVaoLam()));
            ps.setBoolean(6, nv.isTrangThai());
            ps.setString(7, nv.getMatKhau());
            ps.setString(8, nv.getMaNV());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật NhanVien: " + e.getMessage());
            return false;
        }
    }

    // ===================== XÓA NHÂN VIÊN =====================
    public static boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV=?";

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maNV);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi xóa NhanVien: " + e.getMessage());
            return false;
        }
    }

    // ===================== TÌM NHÂN VIÊN THEO MÃ =====================
    public static NhanVien findById(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE maNV=?";
        NhanVien nv = null;

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nv = new NhanVien(
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

            rs.close();
            ps.close();

        } catch (Exception e) {
            System.err.println("Lỗi khi tìm NhanVien theo mã: " + e.getMessage());
        }

        return nv;
    }

    public static String maNVCuoi() {
        String sql = "SELECT TOP 1 maNV FROM NhanVien ORDER BY maNV DESC";
        String maNVCuoi = null;

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                maNVCuoi = rs.getString("maNV");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            System.err.println("Lỗi khi lấy mã nhân viên cuối: " + e.getMessage());
        }

        return maNVCuoi;
    }

    // ===================== CẬP NHẬT MẬT KHẨU =====================
    public static boolean updateMatKhau(String maNV, String matKhauMoi) {
        String sql = "UPDATE NhanVien SET matKhau = ? WHERE maNV = ?";
        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, matKhauMoi);
            ps.setString(2, maNV);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật mật khẩu: " + e.getMessage());
            return false;
        }
    }

}
