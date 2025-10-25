package dao;

import connectDB.connectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHDDAO {

    public static class ChiTiet {
        public String maHD, maMon;
        public int soLuong;
        public double thanhTien;
        public ChiTiet(String maHD, String maMon, int soLuong, double thanhTien) {
            this.maHD = maHD;
            this.maMon = maMon;
            this.soLuong = soLuong;
            this.thanhTien = thanhTien;
        }
    }

    public List<ChiTiet> getAll() {
        List<ChiTiet> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new ChiTiet(
                        rs.getString("maHD"),
                        rs.getString("maMon"),
                        rs.getInt("soLuong"),
                        rs.getDouble("thanhTien")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    public boolean insert(ChiTiet ct) {
        String sql = "INSERT INTO ChiTietHoaDon VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, ct.maHD);
            ps.setString(2, ct.maMon);
            ps.setInt(3, ct.soLuong);
            ps.setDouble(4, ct.thanhTien);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(ChiTiet ct) {
        String sql = "UPDATE ChiTietHoaDon SET soLuong=?, thanhTien=? WHERE maHD=? AND maMon=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, ct.soLuong);
            ps.setDouble(2, ct.thanhTien);
            ps.setString(3, ct.maHD);
            ps.setString(4, ct.maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(String maHD, String maMon) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHD=? AND maMon=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
