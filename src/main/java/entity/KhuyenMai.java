package entity;

import java.time.LocalDate;

public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private int soLuong;
    private Mon sanPhamKM;
    private LocalDate ngayPhatHanh;
    private LocalDate ngayKetThuc;
    private String maThayThe;
    private int phanTRamGiamGia;
    private boolean uuDai;

    public KhuyenMai() {

    }

    public boolean isUuDai() {
        return uuDai;
    }

    public void setUuDai(boolean uuDai) {
        this.uuDai = uuDai;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public String getMaThayThe() {
        return maThayThe;
    }

    public void setMaThayThe(String maThayThe) {
        this.maThayThe = maThayThe;
    }

    public KhuyenMai(String maKM, int soLuong, int phanTRamGiamGia, Mon sanPhamKM, LocalDate ngayPhatHanh, LocalDate ngayKetThuc) {
        this.maKM = maKM;
        this.soLuong = soLuong;
        this.phanTRamGiamGia = phanTRamGiamGia;
        this.sanPhamKM = sanPhamKM;
        this.ngayPhatHanh = ngayPhatHanh;
        this.ngayKetThuc = ngayKetThuc;
    }

    public KhuyenMai(String maKM, int soLuong, int phanTRamGiamGia, LocalDate ngayPhatHanh, LocalDate ngayKetThuc) {
        this.maKM = maKM;
        this.soLuong = soLuong;
        this.phanTRamGiamGia = phanTRamGiamGia;
        this.ngayPhatHanh = ngayPhatHanh;
        this.ngayKetThuc = ngayKetThuc;
    }

    public KhuyenMai(String maKM, String tenKM, int soLuong, LocalDate ngayPhatHanh, LocalDate ngayKetThuc, String maThayThe, int phanTRamGiamGia, boolean uuDai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.soLuong = soLuong;
        this.ngayPhatHanh = ngayPhatHanh;
        this.ngayKetThuc = ngayKetThuc;
        this.maThayThe = maThayThe;
        this.phanTRamGiamGia = phanTRamGiamGia;
        this.uuDai = uuDai;
    }
    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getPhanTRamGiamGia() {
        return phanTRamGiamGia;
    }

    public void setPhanTRamGiamGia(int phanTRamGiamGia) {
        this.phanTRamGiamGia = phanTRamGiamGia;
    }

    public Mon getSanPhamKM() {
        return sanPhamKM;
    }

    public void setSanPhamKM(Mon sanPhamKM) {
        this.sanPhamKM = sanPhamKM;
    }

    public LocalDate getNgayPhatHanh() {
        return ngayPhatHanh;
    }

    public void setNgayPhatHanh(LocalDate ngayPhatHanh) {
        this.ngayPhatHanh = ngayPhatHanh;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    @Override
    public String toString() {
        return "KhuyenMai{" +
                "maKM='" + maKM + '\'' +
                ", soLuong=" + soLuong +
                ", phanTRamGiamGia=" + phanTRamGiamGia +
                ", sanPhamKM=" + sanPhamKM +
                ", ngayPhatHanh=" + ngayPhatHanh +
                ", ngayKetThuc=" + ngayKetThuc +
                '}';
    }
}
