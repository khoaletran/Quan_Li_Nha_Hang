package dao;

import connectDB.connectDB;
import entity.PhanTramGiaBan;
import entity.LoaiMon;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhanTramGiaBanDAO {

    public static List<PhanTramGiaBan> getAll() {
        List<PhanTramGiaBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhanTramGiaBan";

        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // Lấy danh sách loại món để ghép
            List<LoaiMon> dsLoai = LoaiMonDAO.getAll();

            while (rs.next()) {
                String maLoaiMon = rs.getString("maLoaiMon");

                // Tìm loại món tương ứng
                LoaiMon loai = dsLoai.stream()
                        .filter(l -> l.getMaLoaiMon().equals(maLoaiMon))
                        .findFirst()
                        .orElse(null);

                // Tạo đối tượng PhanTramGiaBan
                PhanTramGiaBan pt = new PhanTramGiaBan(
                        rs.getString("maPTGB"),
                        loai,
                        rs.getInt("phanTramLoi"),
                        rs.getDate("ngayApDung").toLocalDate()
                );

                ds.add(pt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

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
