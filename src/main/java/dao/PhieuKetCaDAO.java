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

        String moTa = rs.getString("moTa");

        // Load nhân viên đầy đủ
        NhanVien nv = NhanVienDAO.getByID(maNV);

        return new PhieuKetCa(
                maPhieu,
                nv,
                ca,
                soHoaDon,
                tienMat,
                tienCK,
                tienChenhLech,
                ngayKetCa,
                moTa
        );
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

    // ============================================
    // INSERT
    // ============================================
    public boolean insert(PhieuKetCa phieu) {
        String sql = """
            INSERT INTO PhieuKetCa
            (maPhieu, maNV, ca, soHoaDon, tienMat, tienCK, tienChenhLech, ngayKetCa, moTa)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
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
        if (max != null && max.startsWith("PK")) {
            next = Integer.parseInt(max.substring(2)) + 1;
        }

        return String.format("PK%04d", next);
    }
}
