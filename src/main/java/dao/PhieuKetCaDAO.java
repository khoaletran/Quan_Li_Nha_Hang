package dao;

import connectDB.connectDB;
import entity.NhanVien;
import entity.PhieuKetCa;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuKetCaDAO {

    public static List<PhieuKetCa> getAll() {
        List<PhieuKetCa> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuKetCa";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maPhieu = rs.getString("maPhieu");
                String maNV = rs.getString("maNV");
                boolean ca = rs.getBoolean("ca");
                int soHoaDon = rs.getInt("soHoaDon");
                double tienMat = rs.getDouble("tienMat");
                double tienCK = rs.getDouble("tienCK");
                double tienChenhLech = rs.getDouble("tienChenhLech");
                Timestamp ts = rs.getTimestamp("ngayKetCa");
                LocalDateTime ngayKetCa = (ts != null) ? ts.toLocalDateTime() : null;
                String moTa = rs.getString("moTa");

                NhanVien nhanVien = NhanVienDAO.getByID(maNV);

                PhieuKetCa phieu = new PhieuKetCa(maPhieu, nhanVien, ca, soHoaDon,
                        tienMat, tienCK, tienChenhLech, ngayKetCa, moTa);

                ds.add(phieu);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phiếu kết ca: " + e.getMessage());
            e.printStackTrace();
        }

        return ds;
    }

    public boolean insert(PhieuKetCa phieu) {
        String sql = "INSERT INTO PhieuKetCa " +
                "(maPhieu, maNV, ca, soHoaDon, tienMat, tienCK, tienChenhLech, ngayKetCa, moTa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phieu.getMaPhieu());
            ps.setString(2, phieu.getNhanVien().getMaNV());
            ps.setBoolean(3, phieu.isCa());
            ps.setInt(4, phieu.getSoHoaDon());
            ps.setDouble(5, phieu.getTienMat());
            ps.setDouble(6, phieu.getTienCK());
            ps.setDouble(7, phieu.getTienChenhLech());
            ps.setTimestamp(8, (phieu.getNgayKetCa() != null) ? Timestamp.valueOf(phieu.getNgayKetCa()) : null);
            ps.setString(9, phieu.getMoTa());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm phiếu kết ca: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getMaxMaPhieu() {
        String sql = "SELECT TOP 1 maPhieu FROM PhieuKetCa ORDER BY maPhieu DESC";
        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("maPhieu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateNewMaPhieu() {
        String maxMa = getMaxMaPhieu();
        int nextNumber = 1;
        if (maxMa != null && maxMa.startsWith("PK")) {
            nextNumber = Integer.parseInt(maxMa.substring(2)) + 1;
        }
        return String.format("PK%04d", nextNumber);
    }


}
