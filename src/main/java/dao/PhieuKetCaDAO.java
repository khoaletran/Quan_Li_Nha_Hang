package dao;

import connectDB.connectDB;
import entity.NhanVien;
import entity.PhieuKetCa;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuKetCaDAO {

    // ============================================
    // MAP OBJECT
    // ============================================
    private static PhieuKetCa map(ResultSet rs) throws SQLException {

        String maPhieu = rs.getString("maPhieu");
        String maNV = rs.getString("maNV");

        boolean ca = rs.getBoolean("ca");
        int soHoaDon = rs.getInt("soHoaDon");
        double tienMat = rs.getDouble("tienMat");
        double tienCK = rs.getDouble("tienCK");
        double tienChenhLech = rs.getDouble("tienChenhLech");

        Timestamp ts = rs.getTimestamp("ngayKetCa");
        LocalDateTime ngayKetCa = ts != null ? ts.toLocalDateTime() : null;

        Timestamp ls = rs.getTimestamp("tgLogIn");
        LocalDateTime tgLogIn = ts != null ? ts.toLocalDateTime() : null;

        String moTa = rs.getString("moTa");

        // Load nhân viên đầy đủ
        NhanVien nv = NhanVienDAO.getByID(maNV);

//        return new PhieuKetCa(
//                maPhieu,
//                nv,
//                ca,
//                soHoaDon,
//                tienMat,
//                tienCK,
//                tienChenhLech,
//                ngayKetCa,
//                tgLogIn,
//                moTa
//        );
        PhieuKetCa p = new PhieuKetCa();

        p.setMaPhieu(maPhieu);
        p.setNhanVien(nv);
        p.setCa(ca);
        p.setSoHoaDon(soHoaDon);
        p.setTienMat(tienMat);
        p.setTienCK(tienCK);
        p.setTienChenhLech(tienChenhLech);

        p.setNgayKetCaFromDB(ngayKetCa);
        p.setTgLogIn(tgLogIn);

        p.setMoTa(moTa);

        return p;

    }

    // ============================================
    // GET ALL
    // ============================================
    public static List<PhieuKetCa> getAll() {
        List<PhieuKetCa> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuKetCa ORDER BY maPhieu DESC";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) ds.add(map(rs));

        } catch (SQLException e) {
            System.err.println("PhieuKetCaDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }

    public static List<PhieuKetCa> getAllForTraCuu() {

        List<PhieuKetCa> ds = new ArrayList<>();

        String sql = """
        SELECT p.*, n.tenNV,n.sdt
        FROM PhieuKetCa p
        JOIN NhanVien n ON p.maNV = n.maNV
        ORDER BY p.maPhieu DESC
    """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                PhieuKetCa p = new PhieuKetCa();

                p.setMaPhieu(rs.getString("maPhieu"));
                p.setCa(rs.getBoolean("ca"));
                p.setSoHoaDon(rs.getInt("soHoaDon"));
                p.setTienMat(rs.getDouble("tienMat"));
                p.setTienCK(rs.getDouble("tienCK"));
                p.setTienChenhLech(rs.getDouble("tienChenhLech"));

                Timestamp ts = rs.getTimestamp("ngayKetCa");
                p.setNgayKetCaFromDB(ts != null ? ts.toLocalDateTime() : null);

                Timestamp lg = rs.getTimestamp("tgLogIn");
                p.setTgLogIn(lg != null ? lg.toLocalDateTime() : null);

                p.setMoTa(rs.getString("moTa"));

                // ===== Nhân viên
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setSdt(rs.getString("sdt"));

                p.setNhanVien(nv);

                ds.add(p);
            }

        } catch (SQLException e) {
            System.err.println("PhieuKetCaDAO.getAll(): " + e.getMessage());
        }

        return ds;
    }


    // ============================================
    // INSERT
    // ============================================
    public boolean insert(PhieuKetCa phieu) {
        String sql = """
            INSERT INTO PhieuKetCa
            (maPhieu, maNV, ca, soHoaDon, tienMat, tienCK, tienChenhLech, ngayKetCa, moTa,tgLogIn)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phieu.getMaPhieu());
            ps.setString(2, phieu.getNhanVien().getMaNV());
            ps.setBoolean(3, phieu.isCa());
            ps.setInt(4, phieu.getSoHoaDon());
            ps.setDouble(5, phieu.getTienMat());
            ps.setDouble(6, phieu.getTienCK());
            ps.setDouble(7, phieu.getTienChenhLech());

            ps.setTimestamp(8,
                    phieu.getNgayKetCa() != null
                            ? Timestamp.valueOf(phieu.getNgayKetCa())
                            : null
            );

            ps.setString(9, phieu.getMoTa());
            ps.setTimestamp(10,
                    phieu.getTgLogIn() != null
                            ? Timestamp.valueOf(phieu.getTgLogIn())
                            : null
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("PhieuKetCaDAO.insert(): " + e.getMessage());
            return false;
        }
    }

    // ============================================
    // GET MAX MA PHIEU
    // ============================================
    public String getMaxMaPhieu() {
        String sql = "SELECT TOP 1 maPhieu FROM PhieuKetCa ORDER BY maPhieu DESC";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getString("maPhieu");

        } catch (SQLException e) {
            System.err.println("PhieuKetCaDAO.getMaxMaPhieu(): " + e.getMessage());
        }

        return null;
    }

    // ============================================
    // PHÁT SINH MÃ
    // ============================================
    public String generateNewMaPhieu() {
        String max = getMaxMaPhieu();

        int next = 1;
        if (max != null && max.startsWith("MP")) {
            next = Integer.parseInt(max.substring(2)) + 1;
        }

        return String.format("MP%04d", next);
    }

    public static String getMaPhieuKCCuoiTheoNgay(String ca, String ngay) {
        String prefix = "MP" + ca + ngay;

        String sql = """
        SELECT TOP 1 maPhieu
        FROM PhieuKetCa
        WHERE maPhieu LIKE ?
        ORDER BY maPhieu DESC
    """;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("maPhieu");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getMaPhieuKCCuoiTheoNgay: " + e.getMessage());
        }

        return null;
    }

}
