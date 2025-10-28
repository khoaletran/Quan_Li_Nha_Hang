package dao;

import connectDB.connectDB;
import entity.Mon;
import entity.LoaiMon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonDAO {

    // Lấy toàn bộ món
    public static List<Mon> getAll() {
        List<Mon> ds = new ArrayList<>();
        Connection con = connectDB.getConnection();
        if (con == null) return ds;

        String sql = "SELECT * FROM Mon";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // 🔹 Load toàn bộ loại món 1 lần
            List<LoaiMon> dsLoai = LoaiMonDAO.getAll();

            while (rs.next()) {
                Mon mon = new Mon();
                mon.setMaMon(rs.getString("maMon"));
                mon.setTenMon(rs.getString("tenMon"));
                mon.setMoTa(rs.getString("moTa"));
                mon.setHinhAnh(rs.getString("hinhAnh"));
                mon.setGiaGoc(rs.getDouble("giaGoc"));
                mon.setSoLuong(rs.getInt("soLuong"));

                String maLoaiMon = rs.getString("loaiMon");
                LoaiMon loai = dsLoai.stream()
                        .filter(l -> l.getMaLoaiMon().equals(maLoaiMon))
                        .findFirst()
                        .orElse(null);
                mon.setLoaiMon(loai);

                ds.add(mon);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }


    // Thêm món mới
    public static boolean insert(Mon mon) {
        String sql = "INSERT INTO Mon(maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, mon.getMaMon());
            pst.setString(2, mon.getTenMon());
            pst.setString(3, mon.getMoTa());
            pst.setString(4, mon.getHinhAnh());
            pst.setDouble(5, mon.getGiaGoc());
            pst.setInt(6, mon.getSoLuong());

            if (mon.getLoaiMon() != null)
                pst.setString(7, mon.getLoaiMon().getMaLoaiMon());
            else
                pst.setNull(7, Types.VARCHAR);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật thông tin món
    public static boolean update(Mon mon) {
        String sql = "UPDATE Mon SET tenMon = ?, moTa = ?, hinhAnh = ?, giaGoc = ?, soLuong = ?, loaiMon = ? " +
                "WHERE maMon = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, mon.getTenMon());
            pst.setString(2, mon.getMoTa());
            pst.setString(3, mon.getHinhAnh());
            pst.setDouble(4, mon.getGiaGoc());
            pst.setInt(5, mon.getSoLuong());

            if (mon.getLoaiMon() != null)
                pst.setString(6, mon.getLoaiMon().getMaLoaiMon());
            else
                pst.setNull(6, Types.VARCHAR);

            pst.setString(7, mon.getMaMon());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa món theo mã
    public static boolean delete(String maMon) {
        String sql = "DELETE FROM Mon WHERE maMon = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, maMon);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Tìm món theo mã
    public static Mon findByID(String maMon) {
        Mon mon = null;
        String sql = "SELECT * FROM Mon WHERE maMon = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    mon = new Mon();
                    mon.setMaMon(rs.getString("maMon"));
                    mon.setTenMon(rs.getString("tenMon"));
                    mon.setMoTa(rs.getString("moTa"));
                    mon.setHinhAnh(rs.getString("hinhAnh"));
                    mon.setGiaGoc(rs.getDouble("giaGoc"));
                    mon.setSoLuong(rs.getInt("soLuong"));

                    String maLoaiMon = rs.getString("loaiMon");
                    if (maLoaiMon != null)
                        mon.setLoaiMon(LoaiMonDAO.getByID(maLoaiMon));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mon;
    }
    public static String getLatestMaMon() {
        String sql = "SELECT maMon FROM Mon ORDER BY maMon DESC "; // lấy mã lớn nhất
        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getString("maMon");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // nếu chưa có món nào
    }

}
