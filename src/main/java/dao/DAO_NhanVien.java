package dao;

import connectDB.connectDB;
import entity.NhanVien;
import java.sql.*;
import java.util.ArrayList;

public class DAO_NhanVien {

    public static ArrayList<NhanVien> getAllNhanVien() {
        ArrayList<NhanVien> list = new ArrayList<>();

        try {
            connectDB.getInstance().connect();
            Connection con = connectDB.getConnection();

            String sql = "SELECT * FROM NhanVien";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String id = rs.getString("maNV");
                String name = rs.getString("tenNV");
                String sdt = rs.getString("sdt");
                boolean gioitinh = rs.getBoolean("gioiTinh");
                boolean quanLi = rs.getBoolean("quanLi");
                Date ngayVaoLam = rs.getDate("ngayVaoLam");
                boolean trangThai = rs.getBoolean("trangThai");
                String matKhau = rs.getString("matKhau");

                NhanVien nv = new NhanVien(
                        id, name, sdt, gioitinh, quanLi,
                        ((java.sql.Date) ngayVaoLam).toLocalDate(),
                        trangThai, matKhau
                );
                list.add(nv);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.err.println("Lỗi khi truy xuất NhanVien: " + e.getMessage());
        }
        return list;
    }
}
