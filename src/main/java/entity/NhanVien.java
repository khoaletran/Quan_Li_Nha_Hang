/*
 * @ (#) NhanVien.java    1.0     9/21/2025
 *
 *Copyright (c) 2025 IUH. All rights reserved
 */

package entity;

import java.time.LocalDate;

public class NhanVien {
    private String maNV;
    private String tenNV;
    private String sdt;
    private boolean gioiTinh;
    private boolean quanLi;
    private LocalDate ngayVaoLam;
    private boolean trangThai;
    private String matKhau;

    public NhanVien(String maNV, String matKhau, boolean trangThai, LocalDate ngayVaoLam, boolean quanLi, boolean gioiTinh, String sdt, String tenNV) {
        this.maNV = maNV;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
        this.ngayVaoLam = ngayVaoLam;
        this.quanLi = quanLi;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.tenNV = tenNV;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public boolean isQuanLi() {
        return quanLi;
    }

    public void setQuanLi(boolean quanLi) {
        this.quanLi = quanLi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNV='" + maNV + '\'' +
                ", tenNV='" + tenNV + '\'' +
                ", sdt='" + sdt + '\'' +
                ", gioiTinh=" + gioiTinh +
                ", quanLi=" + quanLi +
                ", ngayVaoLam=" + ngayVaoLam +
                ", trangThai=" + trangThai +
                ", matKhau='" + matKhau + '\'' +
                '}';
    }
}
