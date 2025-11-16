package dao;

import connectDB.connectDB;
import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BanDAO {

    // ==============================
    // HÀM TẠO ĐỐI TƯỢNG TỪ RESULTSET
    // ==============================
    private static Ban mapBan(ResultSet rs) throws SQLException {

        LoaiBan loaiBan = new LoaiBan(
                rs.getString("maLoaiBan"),
                rs.getInt("soLuong"),
                rs.getString("tenLoaiBan")
        );

        KhuVuc khuVuc = new KhuVuc(
                rs.getString("maKhuVuc"),
                rs.getString("tenKhuVuc")
        );

        return new Ban(
                rs.getString("maBan"),
                khuVuc,
                loaiBan,
                rs.getBoolean("trangThai")
        );
    }

    // ==============================
    // GET ALL
    // ==============================
    public static List<Ban> getAll() {
        List<Ban> ds = new ArrayList<>();

        String sql = """
            SELECT b.maBan, b.trangThai,
                   lb.maLoaiBan, lb.tenLoaiBan, lb.soLuong,
                   kv.maKhuVuc, kv.tenKhuVuc
            FROM Ban b
            JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
            JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
            ORDER BY b.maBan
        """;

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ds.add(mapBan(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // ==============================
    // INSERT
    // ==============================
    public boolean insert(Ban ban, boolean trangThai) {
        String sql = """
            INSERT INTO Ban (maBan, trangThai, maLoaiBan, maKhuVuc)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ban.getMaBan());
            ps.setBoolean(2, trangThai);
            ps.setString(3, ban.getLoaiBan().getMaLoaiBan());
            ps.setString(4, ban.getKhuVuc().getMaKhuVuc());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public static boolean update(Ban ban, boolean trangThai) {
        String sql = """
            UPDATE Ban 
            SET trangThai = ?, maLoaiBan = ?, maKhuVuc = ? 
            WHERE maBan = ?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, trangThai);
            ps.setString(2, ban.getLoaiBan().getMaLoaiBan());
            ps.setString(3, ban.getKhuVuc().getMaKhuVuc());
            ps.setString(4, ban.getMaBan());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public static boolean delete(String maBan) {
        String sql = "DELETE FROM Ban WHERE maBan = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // GET BY ID (JOIN – KHÔNG DAO CON)
    // ==============================
    public static Ban getByID(String maBan) {
        String sql = """
            SELECT b.maBan, b.trangThai,
                   lb.maLoaiBan, lb.tenLoaiBan, lb.soLuong,
                   kv.maKhuVuc, kv.tenKhuVuc
            FROM Ban b
            JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
            JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
            WHERE b.maBan = ?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapBan(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ==============================
    // GET BÀN TRỐNG THEO KV + LOẠI
    // ==============================
    public static Ban getBanTrong(String maKV, String maLB) {

        String sql = """
            SELECT TOP 1 b.maBan, b.trangThai,
                   lb.maLoaiBan, lb.tenLoaiBan, lb.soLuong,
                   kv.maKhuVuc, kv.tenKhuVuc
            FROM Ban b
            JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
            JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
            WHERE b.maKhuVuc = ?
              AND b.maLoaiBan = ?
              AND b.trangThai = 0
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKV);
            ps.setString(2, maLB);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapBan(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ==============================
    // LẤY BÀN THEO KV + LOẠI (KHÔNG CẦN TRẠNG THÁI)
    // ==============================
    public static Ban getBanTheoLoaiVaKV(String maKV, String maLB) {

        String sql = """
            SELECT TOP 1 b.maBan, b.trangThai,
                   lb.maLoaiBan, lb.tenLoaiBan, lb.soLuong,
                   kv.maKhuVuc, kv.tenKhuVuc
            FROM Ban b
            JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
            JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
            WHERE b.maKhuVuc = ?
              AND b.maLoaiBan = ?
            ORDER BY b.maBan
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKV);
            ps.setString(2, maLB);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapBan(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ==============================
    // KIỂM TRA CÒN BÀN TRỐNG TRONG KV
    // ==============================
    public static boolean conBanTrongTheoKhuVuc(String maKV, int soLuongKhach) {

        String sql = """
            SELECT COUNT(*) AS sl
            FROM Ban b
            JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
            WHERE b.maKhuVuc = ?
              AND b.trangThai = 0
              AND lb.soLuong >= ?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKV);
            ps.setInt(2, soLuongKhach);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("sl") > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ==============================
    // GET TẤT CẢ BÀN TRỐNG
    // ==============================
    public static List<Ban> getAllTrong() {
        List<Ban> list = new ArrayList<>();

        String sql = """
            SELECT b.maBan, b.trangThai,
                   lb.maLoaiBan, lb.tenLoaiBan, lb.soLuong,
                   kv.maKhuVuc, kv.tenKhuVuc
            FROM Ban b
            JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
            JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
            WHERE b.trangThai = 0
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapBan(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ==============================
    // TẠO MÃ BÀN THEO KHU VỰC
    // ==============================
    public String taoMaBanChoTheoKhuVuc(KhuVuc khuVuc) {
        String prefix;

        switch (khuVuc.getMaKhuVuc()) {
            case "KV0001": prefix = "WO"; break;
            case "KV0002": prefix = "WI"; break;
            case "KV0003": prefix = "WV"; break;
            default: prefix = "WX"; break;
        }

        String sql = """
            SELECT TOP 1 maBan
            FROM Ban
            WHERE maBan LIKE ?
            ORDER BY maBan DESC
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");

            ResultSet rs = ps.executeQuery();

            int nextId = 1;

            if (rs.next()) {
                String lastId = rs.getString("maBan");
                nextId = Integer.parseInt(lastId.substring(2)) + 1;
            }

            return prefix + String.format("%04d", nextId);

        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "0001";
        }
    }
}
