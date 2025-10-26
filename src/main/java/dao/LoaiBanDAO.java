package dao;

import connectDB.connectDB;
import entity.LoaiBan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiBanDAO {

    public static List<LoaiBan> getAll() {
        List<LoaiBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoaiBan";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new LoaiBan(
                        rs.getString("maLoaiBan"),
                        rs.getInt("soLuong"),
                        rs.getString("tenLoaiBan")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    public boolean insert(LoaiBan lb) {
        String sql = "INSERT INTO LoaiBan VALUES (?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, lb.getMaLoaiBan());
            ps.setString(2, lb.getTenLoaiBan());
            ps.setInt(3, lb.getSoLuong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(LoaiBan lb) {
        String sql = "UPDATE LoaiBan SET tenLoaiBan=?, soLuong=? WHERE maLoaiBan=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, lb.getTenLoaiBan());
            ps.setInt(2, lb.getSoLuong());
            ps.setString(3, lb.getMaLoaiBan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(String maLoaiBan) {
        String sql = "DELETE FROM LoaiBan WHERE maLoaiBan=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maLoaiBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static LoaiBan getById(String maLoaiBan) {
        LoaiBan loaiBan = null;
        String sql = "SELECT maLoaiBan, tenLoaiBan, soLuong FROM LoaiBan WHERE maLoaiBan = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoaiBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    loaiBan = new LoaiBan(
                            rs.getString("maLoaiBan"),
                            rs.getInt("soLuong"),
                            rs.getString("tenLoaiBan")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaiBan;
    }

}
