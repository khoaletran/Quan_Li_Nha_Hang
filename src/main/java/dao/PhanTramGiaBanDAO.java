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

    // ============================================
    // MAP OBJECT
    // ============================================
    private static PhanTramGiaBan map(ResultSet rs) throws SQLException {

        LoaiMon loaiMon = null;
        Mon mon = null;

        String maLoaiMon = rs.getString("maLoaiMon");
        String maMon = rs.getString("maMon");

        if (maLoaiMon != null)
            loaiMon = LoaiMonDAO.getByID(maLoaiMon);

        if (maMon != null)
            mon = MonDAO.findByID(maMon);

        return new PhanTramGiaBan(
                rs.getString("maPTGB"),
                rs.getInt("phanTramLoi"),
                rs.getDate("ngayApDung").toLocalDate(),
                loaiMon,
                mon
        );
    }

    // ============================================
    // GET ALL
    // ============================================
    public static List<PhanTramGiaBan> getAll() {
        List<PhanTramGiaBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhanTramGiaBan ORDER BY maPTGB DESC";

        try (Connection con = connectDB.getInstance().getNewConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) ds.add(map(rs));

        } catch (SQLException e) {
            System.err.println("PTGB.getAll(): " + e.getMessage());
        }

        return ds;
    }

    // ============================================
    // GET BY ID
    // ============================================
    public static PhanTramGiaBan getByID(String maPTGB) {
        String sql = "SELECT * FROM PhanTramGiaBan WHERE maPTGB = ?";

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maPTGB);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("PTGB.getByID(): " + e.getMessage());
        }

        return null;
    }

    // ============================================
    // GET LATEST FOR MON
    // ============================================
    public static PhanTramGiaBan getLatestForMon(String maMon) {
        String sql = """
            SELECT TOP 1 * FROM PhanTramGiaBan
            WHERE maMon = ?
            ORDER BY ngayApDung DESC
        """;

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maMon);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("PTGB.getLatestForMon(): " + e.getMessage());
        }

        return null;
    }

    // ============================================
    // GET LATEST FOR LOAI MON
    // ============================================
    public static PhanTramGiaBan getLatestForLoaiMon(String maLoaiMon) {
        String sql = """
            SELECT TOP 1 * FROM PhanTramGiaBan
            WHERE maLoaiMon = ? AND maMon IS NULL
            ORDER BY ngayApDung DESC
        """;

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoaiMon);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("PTGB.getLatestForLoaiMon(): " + e.getMessage());
        }

        return null;
    }

    // ============================================
    // GET LATEST (PHÁT SINH MÃ)
    // ============================================
    public static PhanTramGiaBan getLatest() {
        String sql = "SELECT TOP 1 * FROM PhanTramGiaBan ORDER BY maPTGB DESC";

        try (Connection con = connectDB.getInstance().getNewConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.err.println("PTGB.getLatest(): " + e.getMessage());
        }

        return null;
    }

    // ============================================
    // INSERT
    // ============================================
    public static boolean insert(PhanTramGiaBan pt) {
        String sql = """
            INSERT INTO PhanTramGiaBan(maPTGB, maLoaiMon, maMon, phanTramLoi, ngayApDung)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pt.getMaPTGB());
            ps.setString(2, pt.getLoaiMon() != null ? pt.getLoaiMon().getMaLoaiMon() : null);
            ps.setString(3, pt.getMon() != null ? pt.getMon().getMaMon() : null);
            ps.setInt(4, pt.getPhanTramLoi());
            ps.setDate(5, Date.valueOf(pt.getNgayApDung()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("PTGB.insert(): " + e.getMessage());
            return false;
        }
    }

    // ============================================
    // UPDATE
    // ============================================
    public boolean update(PhanTramGiaBan pt) {
        String sql = """
            UPDATE PhanTramGiaBan
            SET phanTramLoi=?, ngayApDung=?
            WHERE maPTGB=?
        """;

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pt.getPhanTramLoi());
            ps.setDate(2, Date.valueOf(pt.getNgayApDung()));
            ps.setString(3, pt.getMaPTGB());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("PTGB.update(): " + e.getMessage());
            return false;
        }
    }

    // ============================================
    // DELETE
    // ============================================
    public boolean delete(String maPTGB) {
        String sql = "DELETE FROM PhanTramGiaBan WHERE maPTGB = ?";

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maPTGB);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("PTGB.delete(): " + e.getMessage());
            return false;
        }
    }

    // ============================================
    // GET EFFECTIVE FOR MON (AT DATE)
    // ============================================
    public static PhanTramGiaBan getEffectiveForMonAtDate(String maMon, LocalDate ngayHD) {
        String sql = """
            SELECT TOP 1 * 
            FROM PhanTramGiaBan
            WHERE maMon = ? AND ngayApDung <= ?
            ORDER BY ngayApDung DESC
        """;

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maMon);
            ps.setDate(2, Date.valueOf(ngayHD));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);

        } catch (Exception e) {
            System.err.println("PTGB.getEffectiveForMonAtDate(): " + e.getMessage());
        }
        return null;
    }

    // ============================================
    // GET EFFECTIVE FOR LOAI MON (AT DATE)
    // ============================================
    public static PhanTramGiaBan getEffectiveForLoaiMonAtDate(String maLoaiMon, LocalDate ngayHD) {
        String sql = """
            SELECT TOP 1 *
            FROM PhanTramGiaBan
            WHERE maLoaiMon = ? AND ngayApDung <= ?
            ORDER BY ngayApDung DESC
        """;

        try (Connection con = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLoaiMon);
            ps.setDate(2, Date.valueOf(ngayHD));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);

        } catch (Exception e) {
            System.err.println("PTGB.getEffectiveForLoaiMonAtDate(): " + e.getMessage());
        }
        return null;
    }
}
