package dao;

import connectDB.connectDB;
import entity.LoaiMon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiMonDAO {

    public static List<LoaiMon> getAll() {
        List<LoaiMon> ds = new ArrayList<>();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM LoaiMon";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new LoaiMon(
                        rs.getString("maLoaiMon"),
                        rs.getString("tenLoaiMon"),
                        rs.getString("moTa")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
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
}
