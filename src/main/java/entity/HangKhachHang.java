/*
 * @ (#) HangKhachHang.java    1.0     9/21/2025
 *
 *Copyright (c) 2025 IUH. All rights reserved
 */

package entity;

public class HangKhachHang {
    private String maHang;
    private int diemHang;
    private int giamGia;
    private String moTa;

    public HangKhachHang(String maHang, String moTa, int giamGia, int diemHang) {
        setMaHang(maHang);
        setMoTa(moTa);
        setDiemHang(diemHang);
        setGiamGia(giamGia);
    }

    public String getMaHang() {
        return maHang;
    }

    public void setMaHang(String maHang) {
        this.maHang = maHang;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(int giamGia) {
        this.giamGia = giamGia;
    }

    public int getDiemHang() {
        return diemHang;
    }

    public void setDiemHang(int diemHang) {
        this.diemHang = diemHang;
    }

    @Override
    public String toString() {
        return "HangKhachHang{" +
                "maHang='" + maHang + '\'' +
                ", diemHang=" + diemHang +
                ", giamGia=" + giamGia +
                ", moTa='" + moTa + '\'' +
                '}';
    }

}
