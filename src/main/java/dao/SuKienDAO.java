package dao;

import connectDB.connectDB;
import entity.SuKien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuKienDAO {

    public static List<SuKien> getAll() {
        List<SuKien> ds = new ArrayList<>();
        String sql = "SELECT * FROM SuKien";

        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    ds.add(new SuKien(
                            rs.getString("maSK"),
                            rs.getString("tenSK"),
                            rs.getString("moTa"),
                            rs.getDouble("gia")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sự kiện: " + e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(SuKien sk) {
        String sql = "INSERT INTO SuKien(maSK, tenSK, moTa, gia) VALUES (?, ?, ?, ?)";
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, sk.getMaSK());
                ps.setString(2, sk.getTenSK());
                ps.setString(3, sk.getMota());
                ps.setDouble(4, sk.getGia());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm sự kiện: " + e.getMessage());
            return false;
        }
    }

    public boolean update(SuKien sk) {
        String sql = "UPDATE SuKien SET tenSK=?, moTa=?, gia=? WHERE maSK=?";
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, sk.getTenSK());
                ps.setString(2, sk.getMota());
                ps.setDouble(3, sk.getGia());
                ps.setString(4, sk.getMaSK());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật sự kiện: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String maSK) {
        String sql = "DELETE FROM SuKien WHERE maSK=?";
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maSK);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa sự kiện: " + e.getMessage());
            return false;
        }
    }

    public static SuKien getByID(String maSK) {
        String sql = "SELECT * FROM SuKien WHERE maSK = ?";
        try {
            connectDB.getInstance().connect();
            Connection conn = connectDB.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maSK);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new SuKien(
                                rs.getString("maSK"),
                                rs.getString("tenSK"),
                                rs.getString("moTa"),
                                rs.getDouble("gia")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy sự kiện theo mã: " + e.getMessage());
        }
        return null;
    }
}
