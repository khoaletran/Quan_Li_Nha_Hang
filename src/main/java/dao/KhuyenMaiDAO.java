package dao;

import connectDB.connectDB;
import entity.KhuyenMai;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    public List<KhuyenMai> getAll() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new KhuyenMai(
                        rs.getString("maKM"),
                        rs.getInt("soLuong"),
                        rs.getInt("phanTramGiamGia"),
                        null,
                        rs.getDate("ngayPhatHanh").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate()
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    public boolean insert(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, km.getMaKM());
            ps.setInt(2, km.getSoLuong());
            ps.setInt(3, km.getPhanTRamGiamGia());
            ps.setDate(4, Date.valueOf(km.getNgayPhatHanh()));
            ps.setDate(5, Date.valueOf(km.getNgayKetThuc()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET soLuong=?, phanTramGiamGia=?, ngayPhatHanh=?, ngayKetThuc=? WHERE maKM=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, km.getSoLuong());
            ps.setInt(2, km.getPhanTRamGiamGia());
            ps.setDate(3, Date.valueOf(km.getNgayPhatHanh()));
            ps.setDate(4, Date.valueOf(km.getNgayKetThuc()));
            ps.setString(5, km.getMaKM());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(String maKM) {
        String sql = "DELETE FROM KhuyenMai WHERE maKM=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
