package dao;

import connectDB.connectDB;
import entity.PhanTramGiaBan;
import entity.LoaiMon;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhanTramGiaBanDAO {

    public List<PhanTramGiaBan> getAll() {
        List<PhanTramGiaBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhanTramGiaBan";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new PhanTramGiaBan(
                        rs.getInt("phanTramLoi"),
                        new LoaiMon(rs.getString("maLoaiMon"), "", ""),
                        rs.getDate("ngayApDung").toLocalDate()
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    public boolean insert(PhanTramGiaBan pt) {
        String sql = "INSERT INTO PhanTramGiaBan VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, pt.getLoaiMon().getMaLoaiMon());
            ps.setString(2, pt.getLoaiMon().getMaLoaiMon());
            ps.setInt(3, pt.getPhanTramLoi());
            ps.setDate(4, Date.valueOf(pt.getNgayApDung()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(PhanTramGiaBan pt) {
        String sql = "UPDATE PhanTramGiaBan SET phanTramLoi=?, ngayApDung=? WHERE maLoaiMon=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, pt.getPhanTramLoi());
            ps.setDate(2, Date.valueOf(pt.getNgayApDung()));
            ps.setString(3, pt.getLoaiMon().getMaLoaiMon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(String maLoaiMon) {
        String sql = "DELETE FROM PhanTramGiaBan WHERE maLoaiMon=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maLoaiMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
