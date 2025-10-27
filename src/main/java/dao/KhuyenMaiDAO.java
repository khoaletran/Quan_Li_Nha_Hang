package dao;

import connectDB.connectDB;
import entity.KhuyenMai;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    public static List<KhuyenMai> getAll() {
        ArrayList<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai";

        // Không gọi connect() nhiều lần nếu connectDB đã manage connection;
        // tuy nhiên để an toàn vẫn gọi
        try {
            connectDB.getInstance().connect();
        } catch (Exception e) {
            System.err.println("connect() thất bại:");
            e.printStackTrace();
        }

        try (Connection con = connectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            while (rs.next()) {
                String maKM = rs.getString("maKM");
                String tenKM = rs.getString("tenKM");
                int soLuong = rs.getInt("soLuong");

                Date dbNgayPhatHanh = rs.getDate("ngayPhatHanh");
                LocalDate ngayPhatHanh = (dbNgayPhatHanh == null) ? null : dbNgayPhatHanh.toLocalDate();

                Date dbNgayKetThuc = rs.getDate("ngayKetThuc");
                LocalDate ngayKetThuc = (dbNgayKetThuc == null) ? null : dbNgayKetThuc.toLocalDate();

                String maThayThe = rs.getString("maThayThe");
                int phanTram = rs.getInt("phanTramGiamGia");
                Boolean uuDai = rs.getBoolean("uuDai");

                KhuyenMai km = new KhuyenMai(maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTram, uuDai);
                list.add(km);
                count++;
            }
            System.out.println("KhuyenMaiDAO.getAll -> read rows: " + count);
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy xuất KhuyenMai: ");
            e.printStackTrace(); // in stacktrace đầy đủ
        } catch (Exception e) {
            System.err.println("Unexpected error in KhuyenMaiDAO.getAll:");
            e.printStackTrace();
        }

        return list;
    }

    // --- phần insert/update/delete: bạn cũng nên add try-with-resources và in stacktrace ---
    public static boolean insert(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai(maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setInt(3, km.getSoLuong());
            if (km.getNgayPhatHanh() != null) ps.setDate(4, Date.valueOf(km.getNgayPhatHanh()));
            else ps.setNull(4, Types.DATE);
            if (km.getNgayKetThuc() != null) ps.setDate(5, Date.valueOf(km.getNgayKetThuc()));
            else ps.setNull(5, Types.DATE);
            ps.setString(6, km.getMaThayThe());
            ps.setInt(7, km.getPhanTRamGiamGia());
            ps.setBoolean(8, true); // hoặc lấy từ entity nếu có

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm KhuyenMai:");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET tenKM=?, soLuong=?, ngayPhatHanh=?, ngayKetThuc=?, maThayThe=?, phanTramGiamGia=?, uuDai=? WHERE maKM=?";
        try {
            connectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, km.getTenKM());
            ps.setInt(2, km.getSoLuong());
            if (km.getNgayPhatHanh() != null) ps.setDate(3, Date.valueOf(km.getNgayPhatHanh()));
            else ps.setNull(3, Types.DATE);
            if (km.getNgayKetThuc() != null) ps.setDate(4, Date.valueOf(km.getNgayKetThuc()));
            else ps.setNull(4, Types.DATE);
            ps.setString(5, km.getMaThayThe());
            ps.setInt(6, km.getPhanTRamGiamGia());
            ps.setBoolean(7, km.isUuDai());
            ps.setString(8, km.getMaKM());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật KhuyenMai:");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(String maKM) {
        String sql = "DELETE FROM KhuyenMai WHERE maKM=?";
        try {
            connectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa KhuyenMai:");
            e.printStackTrace();
            return false;
        }
    }

    public static KhuyenMai findById(String maKM) {
        String sql = "SELECT * FROM KhuyenMai WHERE maKM=?";
        KhuyenMai km = null;

        try {
            connectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date dbNgayPhatHanh = rs.getDate("ngayPhatHanh");
                    LocalDate ngayPhatHanh = (dbNgayPhatHanh == null) ? null : dbNgayPhatHanh.toLocalDate();

                    Date dbNgayKetThuc = rs.getDate("ngayKetThuc");
                    LocalDate ngayKetThuc = (dbNgayKetThuc == null) ? null : dbNgayKetThuc.toLocalDate();

                    km = new KhuyenMai(
                            rs.getString("maKM"),
                            rs.getString("tenKM"),
                            rs.getInt("soLuong"),
                            ngayPhatHanh,
                            ngayKetThuc,
                            rs.getString("maThayThe"),
                            rs.getInt("phanTramGiamGia"),
                            rs.getBoolean("uuDai")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm KhuyenMai theo mã:");
            e.printStackTrace();
        }
        return km;
    }
    public static String maKMCuoi() {
        String sql = "SELECT TOP 1 maKM FROM KhuyenMai ORDER BY maKM DESC";
        String maKMCuoi = null;

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                maKMCuoi = rs.getString("maKM");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            System.err.println("Lỗi khi lấy mã khuyen mai cuối: " + e.getMessage());
        }

        return maKMCuoi;
    }

    public static KhuyenMai getByID(String maKM) {
        String sql = "SELECT * FROM KhuyenMai WHERE maKM = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date dbNgayPhatHanh = rs.getDate("ngayPhatHanh");
                    Date dbNgayKetThuc = rs.getDate("ngayKetThuc");

                    LocalDate ngayPhatHanh = (dbNgayPhatHanh != null) ? dbNgayPhatHanh.toLocalDate() : null;
                    LocalDate ngayKetThuc = (dbNgayKetThuc != null) ? dbNgayKetThuc.toLocalDate() : null;

                    return new KhuyenMai(
                            rs.getString("maKM"),
                            rs.getString("tenKM"),
                            rs.getInt("soLuong"),
                            ngayPhatHanh,
                            ngayKetThuc,
                            rs.getString("maThayThe"),
                            rs.getInt("phanTramGiamGia"),
                            rs.getBoolean("uuDai")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy khuyến mãi theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
