package dao;

import connectDB.connectDB;
import entity.LoaiMon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiMonDAO {

    public static List<LoaiMon> getAll() {
        List<LoaiMon> ds = new ArrayList<>();
        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            String sql = "SELECT * FROM LoaiMon";
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    ds.add(new LoaiMon(
                            rs.getString("maLoaiMon"),
                            rs.getString("tenLoaiMon"),
                            rs.getString("moTa")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi truy vấn LoaiMon: " + e.getMessage());
        }
        return ds;
    }
    public static LoaiMon getByID(String maLoaiMon) {
        LoaiMon loai = null;
        String sql = "SELECT * FROM LoaiMon WHERE maLoaiMon = ?";
        Connection con = connectDB.getConnection();
        if (con == null) {
            System.out.println("STOP1");
        }
        try (

                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLoaiMon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    loai = new LoaiMon(
                            rs.getString("maLoaiMon"),
                            rs.getString("tenLoaiMon"),
                            rs.getString("moTa")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loai;
    }
    public static String getMaLoaiMonByTen(String tenLoaiMon) {
        String sql = "SELECT maLoaiMon FROM LoaiMon WHERE tenLoaiMon = ?";

        try {
            // Mở Connection trước khi query
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            if (con == null || con.isClosed()) {
                System.out.println("Connection chưa mở!");
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tenLoaiMon);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("maLoaiMon");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // không tìm thấy
    }



    public boolean insert(LoaiMon loaiMon) {
        String sql = "INSERT INTO LoaiMon VALUES (?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, loaiMon.getMaLoaiMon());
            ps.setString(2, loaiMon.getTenLoaiMon());
            ps.setString(3, loaiMon.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(LoaiMon loaiMon) {
        String sql = "UPDATE LoaiMon SET tenLoaiMon=?, moTa=? WHERE maLoaiMon=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, loaiMon.getTenLoaiMon());
            ps.setString(2, loaiMon.getMoTa());
            ps.setString(3, loaiMon.getMaLoaiMon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maLoaiMon) {
        String sql = "DELETE FROM LoaiMon WHERE maLoaiMon=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maLoaiMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LoaiMon findByID(String maLoaiMon) {
        String sql = "SELECT * FROM LoaiMon WHERE maLoaiMon = ?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maLoaiMon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LoaiMon(
                            rs.getString("maLoaiMon"),
                            rs.getString("tenLoaiMon"),
                            rs.getString("moTa")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm loại món theo mã: " + e.getMessage());
        }
        return null; // Không tìm thấy
    }



}
