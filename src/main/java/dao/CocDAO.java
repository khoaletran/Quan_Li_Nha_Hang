package dao;

import connectDB.connectDB;
import entity.Coc;
import entity.KhuVuc;
import entity.LoaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CocDAO {

    private final KhuVucDAO khuVucDAO = new KhuVucDAO();
    private final LoaiBanDAO loaiBanDAO = new LoaiBanDAO();

    // Lấy tất cả cọc
    public List<Coc> getAll() {
        List<Coc> list = new ArrayList<>();
        String sql = "SELECT * FROM Coc";
        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Coc coc = new Coc();
                coc.setMaCoc(rs.getString("maCoc"));
                coc.setLoaiCoc(rs.getBoolean("loaiCoc"));

                if (coc.isLoaiCoc()) {
                    coc.setPhanTramCoc(rs.getInt("phanTramCoc"));
                    coc.setSoTienCoc(0);
                } else {
                    coc.setSoTienCoc(rs.getDouble("soTienCoc"));
                    coc.setPhanTramCoc(0);
                }

                String maKhuVuc = rs.getString("maKhuVuc");
                if (maKhuVuc != null) {
                    coc.setKhuVuc(khuVucDAO.getById(maKhuVuc));
                }

                String maLoaiBan = rs.getString("maLoaiBan");
                if (maLoaiBan != null) {
                    coc.setLoaiBan(loaiBanDAO.getById(maLoaiBan));
                }

                list.add(coc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public Coc getLatest() {
        Coc latestCoc = null;
        String sql = "SELECT TOP 1 * FROM Coc ORDER BY MaCoc DESC";

        try (Statement st = connectDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                latestCoc = new Coc();
                latestCoc.setMaCoc(rs.getString("maCoc"));
                latestCoc.setLoaiCoc(rs.getBoolean("loaiCoc"));
                latestCoc.setPhanTramCoc(rs.getInt("phanTramCoc"));
                latestCoc.setSoTienCoc(rs.getDouble("soTienCoc"));
                latestCoc.setKhuVuc(khuVucDAO.getById(rs.getString("maKhuVuc")));
                latestCoc.setLoaiBan(loaiBanDAO.getById(rs.getString("maLoaiBan")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return latestCoc;
    }


    // Insert cọc mới
    public boolean insert(Coc coc) {
        String sql = "INSERT INTO Coc(maCoc, loaiCoc, phanTramCoc, soTienCoc, maKhuVuc, maLoaiBan) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, coc.getMaCoc());
            ps.setBoolean(2, coc.isLoaiCoc());
            ps.setInt(3, coc.isLoaiCoc() ? coc.getPhanTramCoc() : 0);
            ps.setDouble(4, coc.isLoaiCoc() ? 0 : coc.getSoTienCoc());
            ps.setString(5, coc.getKhuVuc() != null ? coc.getKhuVuc().getMaKhuVuc() : null);
            ps.setString(6, coc.getLoaiBan() != null ? coc.getLoaiBan().getMaLoaiBan() : null);
            boolean ok = ps.executeUpdate() > 0;

            // Load full object sau khi insert để hiển thị
            if (ok) {
                if (coc.getKhuVuc() != null)
                    coc.setKhuVuc(khuVucDAO.getById(coc.getKhuVuc().getMaKhuVuc()));
                if (coc.getLoaiBan() != null)
                    coc.setLoaiBan(loaiBanDAO.getById(coc.getLoaiBan().getMaLoaiBan()));
            }

            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update cọc
    public boolean update(Coc coc) {
        String sql = "UPDATE Coc SET loaiCoc=?, phanTramCoc=?, soTienCoc=?, maKhuVuc=?, maLoaiBan=? WHERE maCoc=?";
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, coc.isLoaiCoc());
            ps.setInt(2, coc.isLoaiCoc() ? coc.getPhanTramCoc() : 0);
            ps.setDouble(3, coc.isLoaiCoc() ? 0 : coc.getSoTienCoc());
            ps.setString(4, coc.getKhuVuc() != null ? coc.getKhuVuc().getMaKhuVuc() : null);
            ps.setString(5, coc.getLoaiBan() != null ? coc.getLoaiBan().getMaLoaiBan() : null);
            ps.setString(6, coc.getMaCoc());
            boolean ok = ps.executeUpdate() > 0;

            // Load full object sau khi update để hiển thị
            if (ok) {
                if (coc.getKhuVuc() != null)
                    coc.setKhuVuc(khuVucDAO.getById(coc.getKhuVuc().getMaKhuVuc()));
                if (coc.getLoaiBan() != null)
                    coc.setLoaiBan(loaiBanDAO.getById(coc.getLoaiBan().getMaLoaiBan()));
            }

            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Coc getByKhuVucVaLoaiBan(String maKV, String maLB) {
        String sql = """
            SELECT * FROM Coc 
            WHERE maKhuVuc = ? AND maLoaiBan = ?
        """;

        Coc coc = null;

        try (Connection conn = connectDB.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKV);
            ps.setString(2, maLB);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    coc = new Coc();
                    coc.setMaCoc(rs.getString("maCoc"));
                    coc.setLoaiCoc(rs.getBoolean("loaiCoc"));
                    coc.setPhanTramCoc(rs.getInt("phanTramCoc"));
                    coc.setSoTienCoc(rs.getDouble("soTienCoc"));

                    // 🔸 Gán khu vực và loại bàn nếu có
                    KhuVuc kv = KhuVucDAO.getById(maKV);
                    LoaiBan lb = LoaiBanDAO.getById(maLB);
                    coc.setKhuVuc(kv);
                    coc.setLoaiBan(lb);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin cọc theo KV & LB: " + e.getMessage());
        }

        return coc;
    }
}
