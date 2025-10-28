package dao;

import connectDB.connectDB;
import entity.Mon;
import entity.PhanTramGiaBan;
import entity.LoaiMon;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhanTramGiaBanDAO {

    // ===================== LẤY TOÀN BỘ =====================
    public static List<PhanTramGiaBan> getAll() {
        List<PhanTramGiaBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhanTramGiaBan";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                LoaiMon loaiMon = null;
                Mon mon = null;

                String maLoaiMon = rs.getString("maLoaiMon");
                String maMon = rs.getString("maMon");

                if (maLoaiMon != null)
                    loaiMon = LoaiMonDAO.getByID(maLoaiMon);

                if (maMon != null) {
                    mon = new Mon();
                    mon.setMaMon(maMon);
                }

                PhanTramGiaBan pt = new PhanTramGiaBan(
                        rs.getString("maPTGB"),
                        rs.getInt("phanTramLoi"),
                        rs.getDate("ngayApDung").toLocalDate(),
                        loaiMon,
                        mon
                );

                ds.add(pt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }

    // ===================== LẤY THEO MÃ =====================
    public static PhanTramGiaBan getByID(String maPTGB) {
        String sql = "SELECT * FROM PhanTramGiaBan WHERE maPTGB = ?";
        PhanTramGiaBan pt = null;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maPTGB);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                LoaiMon loaiMon = null;
                Mon mon = null;

                String maLoaiMon = rs.getString("maLoaiMon");
                String maMon = rs.getString("maMon");

                if (maLoaiMon != null)
                    loaiMon = LoaiMonDAO.getByID(maLoaiMon);

                if (maMon != null) {
                    mon = new Mon();
                    mon.setMaMon(maMon);
                }

                pt = new PhanTramGiaBan(
                        rs.getString("maPTGB"),
                        rs.getInt("phanTramLoi"),
                        rs.getDate("ngayApDung").toLocalDate(),
                        loaiMon,
                        mon
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pt;
    }

    // ===================== LẤY MỚI NHẤT CHO MÓN =====================
    public static PhanTramGiaBan getLatestForMon(String maMon) {
        String sql = """
                    SELECT TOP 1 * 
                    FROM PhanTramGiaBan 
                    WHERE maMon IS not NULL AND  maMon = ? 
                    ORDER BY maPTGB DESC
                """;
        try {
            // Mở Connection mới trước khi query
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            if (con == null || con.isClosed()) {
                System.out.println("Connection chưa mở!");
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, maMon);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Mon mon = new Mon();
                    mon.setMaMon(rs.getString("maMon"));

                    return new PhanTramGiaBan(
                            rs.getString("maPTGB"),
                            rs.getInt("phanTramLoi"),
                            rs.getDate("ngayApDung").toLocalDate(),
                            null,
                            mon
                    );
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===================== LẤY MỚI NHẤT CHO LOẠI MÓN =====================
    public static PhanTramGiaBan getLatestForLoaiMon(String maLoaiMon) {
        String sql = """
                    SELECT TOP 1 * 
                    FROM PhanTramGiaBan 
                    WHERE maLoaiMon = ? AND maMon IS NULL
                    ORDER BY maPTGB DESC
                """;

        try {
            // Mở Connection mới trước khi query
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            if (con == null || con.isClosed()) {
                System.out.println("Connection chưa mở!");
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maLoaiMon);
                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        LoaiMon loaiMon = new LoaiMon(rs.getString("maLoaiMon"));
                        return new PhanTramGiaBan(
                                rs.getString("maPTGB"),
                                rs.getInt("phanTramLoi"),
                                rs.getDate("ngayApDung").toLocalDate(),
                                loaiMon,
                                null
                        );
                    }

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //  hàm này dùng cho phát sinh mã tự động
    public static PhanTramGiaBan getLatest() {
        String sql = "SELECT TOP 1 * FROM PhanTramGiaBan ORDER BY maPTGB DESC";

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            if (con == null || con.isClosed()) {
                System.out.println("Connection chưa mở!");
                return null;
            }

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                if (rs.next()) {
                    LoaiMon loaiMon = new LoaiMon(rs.getString("maLoaiMon"));
                    return new PhanTramGiaBan(
                            rs.getString("maPTGB"),
                            rs.getInt("phanTramLoi"),
                            rs.getDate("ngayApDung").toLocalDate(),
                            loaiMon,
                            null
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // ===================== THÊM MỚI =====================
    public static boolean insert(PhanTramGiaBan pt) {
        String sql = "INSERT INTO PhanTramGiaBan (maPTGB, maLoaiMon, maMon, phanTramLoi, ngayApDung) VALUES (?, ?, ?, ?, ?)";

        try {
            // Mở Connection
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                System.out.println("Connection chưa mở!");
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pt.getMaPTGB());

                // Áp dụng cho LoaiMon hay Mon
                ps.setString(2, pt.getLoaiMon() != null ? pt.getLoaiMon().getMaLoaiMon() : null);
                ps.setString(3, pt.getMon() != null ? pt.getMon().getMaMon() : null);

                ps.setInt(4, pt.getPhanTramLoi());
                ps.setDate(5, Date.valueOf(pt.getNgayApDung()));

                int rows = ps.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ===================== CẬP NHẬT =====================
    public boolean update(PhanTramGiaBan pt) {
        String sql = "UPDATE PhanTramGiaBan SET phanTramLoi = ?, ngayApDung = ? WHERE maPTGB = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pt.getPhanTramLoi());
            ps.setDate(2, Date.valueOf(pt.getNgayApDung()));
            ps.setString(3, pt.getMaPTGB());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== XÓA =====================
    public boolean delete(String maPTGB) {
        String sql = "DELETE FROM PhanTramGiaBan WHERE maPTGB = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maPTGB);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static PhanTramGiaBan getEffectiveForMonAtDate(String maMon, LocalDate ngayHD) {
        String sql = """
        SELECT TOP 1 * FROM PhanTramGiaBan
        WHERE maMon = ? AND ngayApDung <= ?
        ORDER BY ngayApDung DESC
    """;
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maMon);
            ps.setDate(2, java.sql.Date.valueOf(ngayHD));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PhanTramGiaBan(
                        rs.getString("maPTGB"),
                        rs.getInt("phanTramLoi"),
                        rs.getDate("ngayApDung").toLocalDate(),
                        LoaiMonDAO.getByID(rs.getString("maLoaiMon")),
                        MonDAO.findByID(rs.getString("maMon"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PhanTramGiaBan getEffectiveForLoaiMonAtDate(String maLoaiMon, LocalDate ngayHD) {
        String sql = """
        SELECT TOP 1 * FROM PhanTramGiaBan
        WHERE maLoaiMon = ? AND ngayApDung <= ?
        ORDER BY ngayApDung DESC
    """;
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoaiMon);
            ps.setDate(2, java.sql.Date.valueOf(ngayHD));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PhanTramGiaBan(
                        rs.getString("maPTGB"),
                        rs.getInt("phanTramLoi"),
                        rs.getDate("ngayApDung").toLocalDate(),
                        LoaiMonDAO.getByID(rs.getString("maLoaiMon")),
                        MonDAO.findByID(rs.getString("maMon"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
