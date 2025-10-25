package dao;

import connectDB.connectDB;
import entity.Mon;
import entity.LoaiMon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonDAO {

    public List<Mon> getAll() {
        List<Mon> ds = new ArrayList<>();
        Connection con = connectDB.getConnection();
        if (con == null) return ds;

        String sql = "SELECT maMon, tenMon, moTa, hinhAnh, giaGoc, giaBan, loaiMon FROM Mon";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Mon mon = new Mon(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getString("moTa"),
                        rs.getString("hinhAnh"),
                        new LoaiMon(rs.getString("loaiMon"), "", ""),
                        null,
                        rs.getDouble("giaGoc")
                );
                ds.add(mon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(Mon mon) {
        String sql = "INSERT INTO Mon(maMon, tenMon, moTa, hinhAnh, giaGoc, giaBan, loaiMon) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, mon.getMaMon());
            ps.setString(2, mon.getTenMon());
            ps.setString(3, mon.getMoTa());
            ps.setString(4, mon.getHinhAnh());
            ps.setDouble(5, mon.getGiaGoc());
            ps.setDouble(6, mon.giaBan());
            ps.setString(7, mon.getLoaiMon().getMaLoaiMon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Mon mon) {
        String sql = "UPDATE Mon SET tenMon=?, moTa=?, hinhAnh=?, giaGoc=?, giaBan=?, loaiMon=? WHERE maMon=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, mon.getTenMon());
            ps.setString(2, mon.getMoTa());
            ps.setString(3, mon.getHinhAnh());
            ps.setDouble(4, mon.getGiaGoc());
            ps.setDouble(5, mon.giaBan());
            ps.setString(6, mon.getLoaiMon().getMaLoaiMon());
            ps.setString(7, mon.getMaMon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maMon) {
        String sql = "DELETE FROM Mon WHERE maMon=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
