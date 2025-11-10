/*
 * @ (#) KhachHang.java    1.0     9/21/2025
 *
 *Copyright (c) 2025 IUH. All rights reserved
 */

package entity;

public class KhachHang {
    private String maKhachHang;
    private String tenKhachHang;
    private String sdt;
    private boolean gioiTinh;
    private int diemTichLuy =0;
    HangKhachHang hangKhachHang;

    public KhachHang(String maKhachHang, int diemTichLuy, boolean gioiTinh, String sdt, String tenKhachHang, HangKhachHang hangKhachHang) {
        setMaKhachHang(maKhachHang);
        setTenKhachHang(tenKhachHang);
        setDiemTichLuy(diemTichLuy);
        setGioiTinh(gioiTinh);
        setSdt(sdt);
        setHangKhachHang(hangKhachHang);
    }

    public KhachHang() {

    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        if(maKhachHang == null || !maKhachHang.matches("^KH\\d{4}$")) {
            throw new IllegalArgumentException("Mã khách hàng sai định dạng.");
        }this.maKhachHang = maKhachHang;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        if (sdt == null || !sdt.matches("^0[3-9]\\d{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ. Phải gồm 10 chữ số và bắt đầu bằng 03–09.");
        }this.sdt = sdt;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        if (tenKhachHang == null || tenKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }this.tenKhachHang = tenKhachHang.trim();
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public HangKhachHang getHangKhachHang() {return hangKhachHang; }

    public void setHangKhachHang(HangKhachHang hangKhachHang) {this.hangKhachHang = hangKhachHang; }

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maKhachHang + '\'' +
                ", tenKhachHang='" + tenKhachHang + '\'' +
                ", sdt='" + sdt + '\'' +
                ", gioiTinh=" + gioiTinh +
                ", diemTichLuy=" + diemTichLuy +
                '}';
    }


}
