package entity;

import dao.PhanTramGiaBanDAO;

public class Mon {
    private String maMon;
    private String tenMon;
    private String moTa;
    private String hinhAnh;
    private double giaGoc;
    private LoaiMon loaiMon;



    // 🔹 Lấy phần trăm lời hiện tại (ưu tiên của món, nếu không có thì lấy loại món)
    public int getPhanTramGiaBanHienTai() {
        // Ưu tiên lấy chính sách riêng cho món
        PhanTramGiaBan ptMon = PhanTramGiaBanDAO.getLatestForMon(maMon);

        if (ptMon != null) {
            return ptMon.getPhanTramLoi();
        }

        // Nếu không có riêng → dùng chính sách loại món
        PhanTramGiaBan ptLoai = PhanTramGiaBanDAO.getLatestForLoaiMon(loaiMon.getMaLoaiMon());
        if (ptLoai != null) {
            return ptLoai.getPhanTramLoi();
        }

        // Nếu không có gì luôn
        return 0;
    }
    // 🔹 Tính giá bán thực tế
    public double getGiaBan() {
        int phanTram = getPhanTramGiaBanHienTai();
        return giaGoc * (1 + phanTram / 100.0);
    }

    public Mon() {
    }
    public Mon(String maMon, String tenMon, String moTa, String hinhAnh, double giaGoc, LoaiMon loaiMon) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.loaiMon = loaiMon;
        this.giaGoc = giaGoc;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(LoaiMon loaiMon) {
        this.loaiMon = loaiMon;
    }

    public double getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(double giaGoc) {
        this.giaGoc = giaGoc;
    }

    @Override
    public String toString() {
        return "Mon{" +
                "maMon='" + maMon + '\'' +
                ", tenMon='" + tenMon + '\'' +
                ", moTa='" + moTa + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                ", giaGoc=" + giaGoc +
                ", loaiMon=" + loaiMon +
                '}' + " Giá bán "+getGiaBan();
    }
}
