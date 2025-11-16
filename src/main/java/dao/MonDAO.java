package dao;

import connectDB.connectDB;
import entity.Mon;
import entity.LoaiMon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonDAO {

    // ==========================
    // MAP RESULT → MON
    // ==========================
    private static Mon map(ResultSet rs, List<LoaiMon> cacheLoai) throws SQLException {
        Mon m = new Mon();

        m.setMaMon(rs.getString("maMon"));
        m.setTenMon(rs.getString("tenMon"));
        m.setMoTa(rs.getString("moTa"));
        m.setHinhAnh(rs.getString("hinhAnh"));
        m.setGiaGoc(rs.getDouble("giaGoc"));
        m.setSoLuong(rs.getInt("soLuong"));

        String maLoai = rs.getString("loaiMon");
        if (maLoai != null) {
            LoaiMon loai = cacheLoai.stream()
                    .filter(x -> x.getMaLoaiMon().equals(maLoai))
                    .findFirst()
                    .orElse(null);
            m.setLoaiMon(loai);
        }

        return m;
    }

    // dùng riêng cho findByID
    private static Mon mapSingle(ResultSet rs) throws SQLException {
        Mon m = new Mon();

        m.setMaMon(rs.getString("maMon"));
        m.setTenMon(rs.getString("tenMon"));
        m.setMoTa(rs.getString("moTa"));
        m.setHinhAnh(rs.getString("hinhAnh"));
        m.setGiaGoc(rs.getDouble("giaGoc"));
        m.setSoLuong(rs.getInt("soLuong"));

        String maLoai = rs.getString("loaiMon");
        if (maLoai != null)
            m.setLoaiMon(LoaiMonDAO.getByID(maLoai));

        return m;
    }

    // ==========================
    // GET ALL
    // ==========================
    public static List<Mon> getAll() {
        List<Mon> ds = new ArrayList<>();
        String sql = "SELECT * FROM Mon ORDER BY maMon";

        try (Connection con = connectDB.getInstance().getNewConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<LoaiMon> cacheLoai = LoaiMonDAO.getAll();

            while (rs.next()) {
                ds.add(map(rs, cacheLoai));
            }

        } catch (SQLException e) {
            System.err.println("MonDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }

    // ==========================
    // FIND BY ID
    // ==========================
    public static Mon findByID(String maMon) {
        String sql = "SELECT * FROM Mon WHERE maMon=?";

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSingle(rs) : null;
            }

        } catch (SQLException e) {
            System.err.println("MonDAO.findByID(): " + e.getMessage());
        }
        return null;
    }

    // ==========================
    // INSERT
    // ==========================
    public static boolean insert(Mon mon) {
        String sql = """
            INSERT INTO Mon(maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mon.getMaMon());
            ps.setString(2, mon.getTenMon());
            ps.setString(3, mon.getMoTa());
            ps.setString(4, mon.getHinhAnh());
            ps.setDouble(5, mon.getGiaGoc());
            ps.setInt(6, mon.getSoLuong());
            ps.setString(7, mon.getLoaiMon() != null ? mon.getLoaiMon().getMaLoaiMon() : null);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("MonDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ==========================
    // UPDATE
    // ==========================
    public static boolean update(Mon mon) {
        String sql = """
            UPDATE Mon
            SET tenMon=?, moTa=?, hinhAnh=?, giaGoc=?, soLuong=?, loaiMon=?
            WHERE maMon=?
        """;

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mon.getTenMon());
            ps.setString(2, mon.getMoTa());
            ps.setString(3, mon.getHinhAnh());
            ps.setDouble(4, mon.getGiaGoc());
            ps.setInt(5, mon.getSoLuong());
            ps.setString(6, mon.getLoaiMon() != null ? mon.getLoaiMon().getMaLoaiMon() : null);
            ps.setString(7, mon.getMaMon());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("MonDAO.update(): " + e.getMessage());
            return false;
        }
    }

    // ==========================
    // DELETE
    // ==========================
    public static boolean delete(String maMon) {
        String sql = "DELETE FROM Mon WHERE maMon=?";

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maMon);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("MonDAO.delete(): " + e.getMessage());
            return false;
        }
    }

    // ==========================
    // GET LATEST MA
    // ==========================
    public static String getLatestMaMon() {
        String sql = "SELECT TOP 1 maMon FROM Mon ORDER BY maMon DESC";

        try (Connection con = connectDB.getInstance().getNewConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            return rs.next() ? rs.getString("maMon") : null;

        } catch (SQLException e) {
            System.err.println("MonDAO.getLatestMaMon(): " + e.getMessage());
            return null;
        }
    }
}
