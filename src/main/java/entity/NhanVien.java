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

    public NhanVien(String maNV, String tenNV, String sdt, boolean gioiTinh, boolean quanLi, LocalDate ngayVaoLam, boolean trangThai, String matKhau) {
        this.maNV = maNV;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
        this.ngayVaoLam = ngayVaoLam;
        this.quanLi = quanLi;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.tenNV = tenNV;
    }

    public NhanVien() {
    }


    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        if(maNV == null || !maNV.matches("^NV\\d{4}$")) {
            throw new IllegalArgumentException("Mã nhân viên sai định dạng.");
        }this.maNV = maNV;
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
        if (ngayVaoLam == null || !ngayVaoLam.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày vào làm phải trước ngày hiện tại.");
        }this.ngayVaoLam = ngayVaoLam;
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
        if (sdt == null || !sdt.matches("^0[3-9]\\d{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ. Phải gồm 10 chữ số và bắt đầu bằng 03–09.");
        }this.sdt = sdt;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        if (tenNV == null || tenNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống.");
        }this.tenNV = tenNV;
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
        if (matKhau == null || !matKhau.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            throw new IllegalArgumentException(
                    "Mật khẩu không hợp lệ. Phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@$!%*?&)."
            );
        }this.matKhau = matKhau;
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
