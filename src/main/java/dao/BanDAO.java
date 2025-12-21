package dao;

import connectDB.connectDB;
import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;

import java.sql.*;
import java.time.LocalDateTime;
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

    public static String getMaBanCuoiTheoKhuVuc(String khuVuc) {
        String prefix = switch (khuVuc) {
            case "Indoor" -> "BI";
            case "Outdoor" -> "BO";
            case "VIP" -> "BV";
            default -> "B";
        };
        String sql = "SELECT TOP 1 maBan FROM Ban WHERE maBan LIKE ? ORDER BY maBan DESC";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("maBan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean conBanTrongTheoKhuVuc(String maKV, int soLuong, LocalDateTime selected) {
        String sql = """
        SELECT TOP 1 1
        FROM Ban b
        JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
        WHERE b.maBan like 'B%'
          AND  b.maKhuVuc = ?
          AND lb.soLuong >= ?
          AND NOT EXISTS (
              SELECT 1
              FROM HoaDon hd
              WHERE hd.maBan = b.maBan
                AND hd.trangThai IN (0,1)
                AND hd.tgCheckin IS NOT NULL
                AND hd.tgCheckin <= ?
                AND (hd.tgCheckout IS NULL OR hd.tgCheckout > ?)
          )
    """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKV);
            ps.setInt(2, soLuong);
            ps.setTimestamp(3, Timestamp.valueOf(selected));
            ps.setTimestamp(4, Timestamp.valueOf(selected));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("conBanTrongTheoKhuVuc: " + e.getMessage());
            return false;
        }
    }


    public static boolean conBanTrongTheoLoaiVaKV(String maKV, String maLB, int soLuongKhach, LocalDateTime selected) {
        String sql = """
        SELECT TOP 1 1
        FROM Ban b
        JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
        WHERE b.maBan like 'B%'
          AND b.maKhuVuc = ?
          AND b.maLoaiBan = ?      -- FIX: bàn trống
          AND lb.soLuong >= ?
          AND NOT EXISTS (
              SELECT 1
              FROM HoaDon hd
              WHERE hd.maBan = b.maBan
                AND hd.trangThai IN (0,1)
                AND hd.tgCheckin IS NOT NULL
                AND hd.tgCheckin <= ?
                AND (hd.tgCheckout IS NULL OR hd.tgCheckout > ?)
          )
    """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKV);
            ps.setString(2, maLB);
            ps.setInt(3, soLuongKhach);
            ps.setTimestamp(4, Timestamp.valueOf(selected));
            ps.setTimestamp(5, Timestamp.valueOf(selected));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("conBanTrongTheoLoaiVaKV: " + e.getMessage());
            return false;
        }
    }


    public static Ban getMotBanTrongTheoLoaiVaKV(String maKV, String maLB, int soLuongKhach, LocalDateTime selected) {
        String sql = """
        SELECT TOP 1 b.maBan, b.trangThai,
               lb.maLoaiBan, lb.tenLoaiBan, lb.soLuong,
               kv.maKhuVuc, kv.tenKhuVuc
        FROM Ban b
        JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
        JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
        WHERE b.maBan like 'B%'
          AND b.maKhuVuc = ?
          AND b.maLoaiBan = ?      -- FIX: bàn trống
          AND lb.soLuong >= ?
          AND NOT EXISTS (
              SELECT 1
              FROM HoaDon hd
              WHERE hd.maBan = b.maBan
                AND hd.trangThai IN (0,1)
                AND hd.tgCheckin IS NOT NULL
                AND hd.tgCheckin <= ?
                AND (hd.tgCheckout IS NULL OR hd.tgCheckout > ?)
          )
        ORDER BY b.maBan
    """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKV);
            ps.setString(2, maLB);
            ps.setInt(3, soLuongKhach);
            ps.setTimestamp(4, Timestamp.valueOf(selected));
            ps.setTimestamp(5, Timestamp.valueOf(selected));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBan(rs); // FIX: trả full object
            }
        } catch (Exception e) {
            System.err.println("getMotBanTrongTheoLoaiVaKV: " + e.getMessage());
        }
        return null;
    }

    public static int getMaxSucChuaTheoKhuVuc(String maKV) {
        String sql = """
        SELECT ISNULL(MAX(lb.soLuong), 0) AS maxSL
        FROM Ban b
        JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan
        WHERE b.maBan like 'B%'
          AND b.maKhuVuc = ?
    """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("maxSL");
            }
        } catch (Exception e) {
            System.err.println("getMaxSucChuaTheoKhuVuc: " + e.getMessage());
        }
        return 0;
    }


}
