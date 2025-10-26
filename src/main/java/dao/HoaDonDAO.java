package dao;

import connectDB.connectDB;
import entity.HoaDon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                HoaDon hd = new HoaDon(
                        rs.getString("maHD"),
                        rs.getDouble("tongTienKM"),
                        rs.getDouble("tongTienSau"),
                        rs.getDouble("tongTienTruoc"),
                        rs.getDouble("coc"),
                        rs.getDouble("thue"),
                        rs.getBoolean("kieuDatBan"),
                        rs.getBoolean("kieuThanhToan"),
                        null, null,
                        rs.getTimestamp("tgCheckin") != null ? rs.getTimestamp("tgCheckin").toLocalDateTime() : null,
                        rs.getTimestamp("tgCheckout") != null ? rs.getTimestamp("tgCheckout").toLocalDateTime() : null,
                        null, null, null
                );
                ds.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(HoaDon hd) {
        String sql = "INSERT INTO HoaDon(maHD, tgCheckin, tgCheckout, kieuThanhToan, kieuDatBan, thue, coc, tongTienTruoc, tongTienSau, tongTienKM) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setTimestamp(2, hd.getTgCheckIn() == null ? null : Timestamp.valueOf(hd.getTgCheckIn()));
            ps.setTimestamp(3, hd.getTgCheckOut() == null ? null : Timestamp.valueOf(hd.getTgCheckOut()));
            ps.setBoolean(4, hd.isKieuThanhToan());
            ps.setBoolean(5, hd.isKieuDatBan());
            ps.setDouble(6, hd.getThue());
            ps.setDouble(7, hd.getCoc());
            ps.setDouble(8, hd.getTongTienTruoc());
            ps.setDouble(9, hd.getTongTienSau());
            ps.setDouble(10, hd.getTongTienKhuyenMai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(HoaDon hd) {
        String sql = "UPDATE HoaDon SET tgCheckin=?, tgCheckout=?, kieuThanhToan=?, kieuDatBan=?, thue=?, coc=?, tongTienTruoc=?, tongTienSau=?, tongTienKM=? WHERE maHD=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, hd.getTgCheckIn() == null ? null : Timestamp.valueOf(hd.getTgCheckIn()));
            ps.setTimestamp(2, hd.getTgCheckOut() == null ? null : Timestamp.valueOf(hd.getTgCheckOut()));
            ps.setBoolean(3, hd.isKieuThanhToan());
            ps.setBoolean(4, hd.isKieuDatBan());
            ps.setDouble(5, hd.getThue());
            ps.setDouble(6, hd.getCoc());
            ps.setDouble(7, hd.getTongTienTruoc());
            ps.setDouble(8, hd.getTongTienSau());
            ps.setDouble(9, hd.getTongTienKhuyenMai());
            ps.setString(10, hd.getMaHD());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }


    public boolean delete(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE maHD=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
