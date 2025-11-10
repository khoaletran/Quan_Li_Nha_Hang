package dao;

import connectDB.connectDB;
import entity.Ban;
import entity.HoaDon;
import entity.KhuVuc;
import entity.LoaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BanDAO {

    // ==============================
    // LẤY TOÀN BỘ DANH SÁCH BÀN
    // ==============================
    public static List<Ban> getAll() {
        List<Ban> ds = new ArrayList<>();
        String sql = """
        SELECT b.maBan, b.trangThai, b.maLoaiBan, b.maKhuVuc
        FROM Ban b
    """;
        Connection con = null;
        try {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                connectDB.getInstance().connect();
                con = connectDB.getConnection();
            }
            List<LoaiBan> dsLoaiBan = LoaiBanDAO.getAll();
            List<KhuVuc> dsKhuVuc = KhuVucDAO.getAll();

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    String maBan = rs.getString("maBan");
                    String maLoaiBan = rs.getString("maLoaiBan");
                    String maKhuVuc = rs.getString("maKhuVuc");
                    boolean trangThai = rs.getBoolean("trangThai");

                    // Tìm đối tượng liên kết
                    LoaiBan loaiBan = dsLoaiBan.stream()
                            .filter(lb -> lb.getMaLoaiBan().equals(maLoaiBan))
                            .findFirst()
                            .orElse(null);

                    KhuVuc khuVuc = dsKhuVuc.stream()
                            .filter(kv -> kv.getMaKhuVuc().equals(maKhuVuc))
                            .findFirst()
                            .orElse(null);

                    ds.add(new Ban(maBan, khuVuc, loaiBan, trangThai));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }



    // ==============================
    // THÊM BÀN MỚI
    // ==============================
    public boolean insert(Ban ban, boolean trangThai) {
        String sql = "INSERT INTO Ban (maBan, trangThai, maLoaiBan, maKhuVuc) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, ban.getMaBan());
            ps.setBoolean(2, trangThai);
            ps.setString(3, ban.getLoaiBan().getMaLoaiBan());
            ps.setString(4, ban.getKhuVuc().getMaKhuVuc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // CẬP NHẬT BÀN
    // ==============================
    public static boolean update(Ban ban, boolean trangThai) {
        String sql = "UPDATE Ban SET trangThai=?, maLoaiBan=?, maKhuVuc=? WHERE maBan=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setString(2, ban.getLoaiBan().getMaLoaiBan());
            ps.setString(3, ban.getKhuVuc().getMaKhuVuc());
            ps.setString(4, ban.getMaBan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // XOÁ BÀN
    // ==============================
    public static boolean delete(String maBan) {
        String sql = "DELETE FROM Ban WHERE maBan=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Ban getBanTrong(String kv, String lb) {
        Ban banTrong = null;
        String sql = """
        SELECT TOP 1 *
        FROM Ban
        WHERE maKhuVuc = ? 
          AND maLoaiBan = ? 
          AND trangThai = 0
    """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kv);
            ps.setString(2, lb);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maBan = rs.getString("maBan");
                    boolean trangThai = rs.getBoolean("trangThai");
                    String maLoaiBan = rs.getString("maLoaiBan");
                    String maKhuVuc = rs.getString("maKhuVuc");

                    LoaiBan loaiBan = LoaiBanDAO.getById(maLoaiBan);
                    KhuVuc khuVuc = KhuVucDAO.getById(maKhuVuc);

                    banTrong = new Ban(maBan, khuVuc, loaiBan, trangThai);
                    System.out.println("Bàn trống: " + maBan);
                } else {
                    System.out.println("Không có bàn trống trong KV=" + kv + " LB=" + lb);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banTrong;
    }

    public static Ban getByID(String maBan) {
        String sql = "SELECT * FROM Ban WHERE maBan = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maLoaiBan = rs.getString("maLoaiBan");
                    String maKhuVuc = rs.getString("maKhuVuc");
                    boolean trangThai = rs.getBoolean("trangThai");

                    // Gọi DAO con để lấy đối tượng liên kết
                    LoaiBan loaiBan = LoaiBanDAO.getById(maLoaiBan);
                    KhuVuc khuVuc = KhuVucDAO.getById(maKhuVuc);

                    return new Ban(maBan, khuVuc, loaiBan, trangThai);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy bàn theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String taoMaBanChoTheoKhuVuc(KhuVuc khuVuc) {
        String prefix;
        String maKV = khuVuc.getMaKhuVuc();
        switch (maKV) {
            case "KV0001": prefix = "WO"; break; // Outdoor
            case "KV0002": prefix = "WI"; break; // Indoor
            case "KV0003": prefix = "WV"; break; // VIP
            default: prefix = "WX"; break;
        }

        String sql = "SELECT TOP 1 maBan FROM Ban WHERE maBan LIKE ? ORDER BY maBan DESC";

        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();

            int nextId = 1;
            if (rs.next()) {
                String lastId = rs.getString("maBan");
                String numPart = lastId.substring(2);
                nextId = Integer.parseInt(numPart) + 1;
            }

            return prefix + String.format("%04d", nextId);

        } catch (Exception e) {
            System.err.println("Lỗi khi tạo mã bàn chờ: " + e.getMessage());
            return prefix + "0001"; // fallback
        }
    }


    public static boolean conBanTrongTheoKhuVuc(String maKhuVuc, int soLuongKhach) {
        return getAll().stream()
                .anyMatch(b -> b.getKhuVuc().getMaKhuVuc().equals(maKhuVuc)
                        && !b.isTrangThai()
                        && b.getLoaiBan() != null
                        && b.getLoaiBan().getSoLuong() >= soLuongKhach);
    }



    public static List<Ban> getAllTrong() {
        List<Ban> list = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE trangThai = 0 AND maBan NOT LIKE 'W%'";

        try (Connection conn = connectDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<LoaiBan> dsLoaiBan = LoaiBanDAO.getAll();
            List<KhuVuc> dsKhuVuc = KhuVucDAO.getAll();
            while (rs.next()) {
                String maBan = rs.getString("maBan");
                String maLoaiBan = rs.getString("maLoaiBan");
                String maKhuVuc = rs.getString("maKhuVuc");
                boolean trangThai = rs.getBoolean("trangThai");

                // Tìm đối tượng liên kết
                LoaiBan loaiBan = dsLoaiBan.stream()
                        .filter(lb -> lb.getMaLoaiBan().equals(maLoaiBan))
                        .findFirst()
                        .orElse(null);

                KhuVuc khuVuc = dsKhuVuc.stream()
                        .filter(kv -> kv.getMaKhuVuc().equals(maKhuVuc))
                        .findFirst()
                        .orElse(null);

                list.add(new Ban(maBan, khuVuc, loaiBan, trangThai));
            }

        } catch (Exception e) {
            System.err.println("Lỗi getAllTrong: " + e.getMessage());
        }

        return list;
    }



}
