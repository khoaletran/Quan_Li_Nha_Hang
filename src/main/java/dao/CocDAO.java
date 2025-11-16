package dao;

import connectDB.connectDB;
import entity.Coc;
import entity.KhuVuc;
import entity.LoaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CocDAO {

    // ==============================
    // HÀM MAP OBJECT
    // ==============================
    private Coc mapCoc(ResultSet rs) throws SQLException {

        Coc c = new Coc();
        c.setMaCoc(rs.getString("maCoc"));
        c.setLoaiCoc(rs.getBoolean("loaiCoc"));

        if (c.isLoaiCoc()) {
            c.setPhanTramCoc(rs.getInt("phanTramCoc"));
            c.setSoTienCoc(0);
        } else {
            c.setSoTienCoc(rs.getDouble("soTienCoc"));
            c.setPhanTramCoc(0);
        }

        // MAP KHU VỰC
        if (rs.getString("maKhuVuc") != null) {
            KhuVuc kv = new KhuVuc(
                    rs.getString("maKhuVuc"),
                    rs.getString("tenKhuVuc")
            );
            c.setKhuVuc(kv);
        }

        // MAP LOẠI BÀN
        if (rs.getString("maLoaiBan") != null) {
            LoaiBan lb = new LoaiBan(
                    rs.getString("maLoaiBan"),
                    rs.getInt("soLuong"),
                    rs.getString("tenLoaiBan")
            );
            c.setLoaiBan(lb);
        }

        return c;
    }

    // ==============================
    // GET ALL – JOIN FULL
    // ==============================
    public List<Coc> getAll() {
        List<Coc> list = new ArrayList<>();

        String sql = """
            SELECT c.*, 
                   kv.tenKhuVuc,
                   lb.tenLoaiBan, lb.soLuong
            FROM Coc c
            LEFT JOIN KhuVuc kv ON c.maKhuVuc = kv.maKhuVuc
            LEFT JOIN LoaiBan lb ON c.maLoaiBan = lb.maLoaiBan
            ORDER BY c.maCoc
        """;

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapCoc(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ==============================
    // GET LATEST
    // ==============================
    public Coc getLatest() {

        String sql = """
            SELECT TOP 1 c.*, 
                   kv.tenKhuVuc,
                   lb.tenLoaiBan, lb.soLuong
            FROM Coc c
            LEFT JOIN KhuVuc kv ON c.maKhuVuc = kv.maKhuVuc
            LEFT JOIN LoaiBan lb ON c.maLoaiBan = lb.maLoaiBan
            ORDER BY c.maCoc DESC
        """;

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) return mapCoc(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ==============================
    // INSERT
    // ==============================
    public boolean insert(Coc coc) {

        String sql = """
            INSERT INTO Coc(maCoc, loaiCoc, phanTramCoc, soTienCoc, maKhuVuc, maLoaiBan)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, coc.getMaCoc());
            ps.setBoolean(2, coc.isLoaiCoc());
            ps.setInt(3, coc.isLoaiCoc() ? coc.getPhanTramCoc() : 0);
            ps.setDouble(4, coc.isLoaiCoc() ? 0 : coc.getSoTienCoc());
            ps.setString(5, coc.getKhuVuc() != null ? coc.getKhuVuc().getMaKhuVuc() : null);
            ps.setString(6, coc.getLoaiBan() != null ? coc.getLoaiBan().getMaLoaiBan() : null);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public boolean update(Coc coc) {

        String sql = """
            UPDATE Coc
            SET loaiCoc = ?, 
                phanTramCoc = ?, 
                soTienCoc = ?, 
                maKhuVuc = ?, 
                maLoaiBan = ?
            WHERE maCoc = ?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, coc.isLoaiCoc());
            ps.setInt(2, coc.isLoaiCoc() ? coc.getPhanTramCoc() : 0);
            ps.setDouble(3, coc.isLoaiCoc() ? 0 : coc.getSoTienCoc());
            ps.setString(4, coc.getKhuVuc() != null ? coc.getKhuVuc().getMaKhuVuc() : null);
            ps.setString(5, coc.getLoaiBan() != null ? coc.getLoaiBan().getMaLoaiBan() : null);
            ps.setString(6, coc.getMaCoc());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // GET BY KV + LOẠI BÀN
    // ==============================
    public static Coc getByKhuVucVaLoaiBan(String maKV, String maLB) {

        String sql = """
            SELECT c.*, 
                   kv.tenKhuVuc,
                   lb.tenLoaiBan, lb.soLuong
            FROM Coc c
            LEFT JOIN KhuVuc kv ON c.maKhuVuc = kv.maKhuVuc
            LEFT JOIN LoaiBan lb ON c.maLoaiBan = lb.maLoaiBan
            WHERE c.maKhuVuc = ? AND c.maLoaiBan = ?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKV);
            ps.setString(2, maLB);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CocDAO dao = new CocDAO();
                return dao.mapCoc(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
