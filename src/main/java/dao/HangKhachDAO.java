package dao;

import connectDB.connectDB;
import entity.HangKhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HangKhachDAO {

    public static List<HangKhachHang> getAll() {
        List<HangKhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM HangKhachHang";

        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    ds.add(new HangKhachHang(
                            rs.getString("maHang"),
                            rs.getString("moTa"),
                            rs.getInt("giaGiam"),
                            rs.getInt("diemHang")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hạng khách hàng: " + e.getMessage());
            e.printStackTrace();
        }

        return ds;
    }

    public boolean insert(HangKhachHang hkh) {
        String sql = "INSERT INTO HangKhachHang(maHang, diemHang, giaGiam, moTa) VALUES (?, ?, ?, ?)";
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, hkh.getMaHang());
                ps.setInt(2, hkh.getDiemHang());
                ps.setInt(3, hkh.getGiamGia());
                ps.setString(4, hkh.getMoTa());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hạng khách hàng: " + e.getMessage());
            return false;
        }
    }

    public boolean update(HangKhachHang hkh) {
        String sql = "UPDATE HangKhachHang SET diemHang=?, giaGiam=?, moTa=? WHERE maHang=?";
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, hkh.getDiemHang());
                ps.setInt(2, hkh.getGiamGia());
                ps.setString(3, hkh.getMoTa());
                ps.setString(4, hkh.getMaHang());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật hạng khách hàng: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String maHang) {
        String sql = "DELETE FROM HangKhachHang WHERE maHang=?";
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maHang);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa hạng khách hàng: " + e.getMessage());
            return false;
        }
    }

    public static HangKhachHang getByID(String maHang) {
        String sql = "SELECT * FROM HangKhachHang WHERE maHang = ?";
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maHang);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new HangKhachHang(
                                rs.getString("maHang"),
                                rs.getString("moTa"),
                                rs.getInt("giaGiam"),
                                rs.getInt("diemHang")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hạng khách hàng theo mã: " + e.getMessage());
        }
        return null;
    }

    public static HangKhachHang getHangTheoDiem(int diem) {
        String sql = """
            SELECT TOP 1 * FROM HangKhachHang
            WHERE diemHang <= ?
            ORDER BY diemHang DESC
        """;
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, diem);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new HangKhachHang(
                                rs.getString("maHang"),
                                rs.getString("moTa"),
                                rs.getInt("giaGiam"),
                                rs.getInt("diemHang")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hạng theo điểm: " + e.getMessage());
        }
        return null;
    }
}
