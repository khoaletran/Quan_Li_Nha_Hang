/*
 * @ (#) HoaDon.java    1.0     9/21/2025
 *
 *Copyright (c) 2025 IUH. All rights reserved
 */

package entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HoaDon {
   private String maHD;
   private KhachHang khachHang;
   private NhanVien nhanVien;
   private Ban ban;
   private LocalDateTime tgCheckIn;
   private LocalDateTime tgCheckOut;
   private KhuyenMai khuyenMai;
   private SuKien suKien;
   private int trangthai;
   private boolean kieuThanhToan;
   private boolean kieuDatBan;
   private double tongTienTruoc;
   private double tongTienKhuyenMai;
   private double thue=0;
   private double coc=0;
   private double tongTienSau=0;
   private int soLuong;

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public HoaDon(String maHD, double tongTienKhuyenMai, double tongTienSau, double tongTienTruoc, double coc, double thue, boolean kieuDatBan, boolean kieuThanhToan, KhuyenMai khuyenMai, SuKien suKien, int trangthai, LocalDateTime tgCheckOut, LocalDateTime tgCheckIn, Ban ban, NhanVien nhanVien, KhachHang khachHang, int soLuong) {
        this.maHD = maHD;
        this.tongTienKhuyenMai = tongTienKhuyenMai;
        this.tongTienSau = tongTienSau;
        this.tongTienTruoc = tongTienTruoc;
        this.coc = coc;
        this.thue = thue;
        this.kieuDatBan = kieuDatBan;
        this.kieuThanhToan = kieuThanhToan;
        this.khuyenMai = khuyenMai;
        this.suKien = suKien;
        setTrangthai(trangthai);
        this.tgCheckOut = tgCheckOut;
        this.tgCheckIn = tgCheckIn;
        this.ban = ban;
        this.nhanVien = nhanVien;
        this.khachHang = khachHang;
        this.soLuong = soLuong;
    }

    public HoaDon() {}

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public double getTongTienKhuyenMai() {
        return tongTienKhuyenMai;
    }

    public void setTongTienKhuyenMai(double tongTienKhuyenMai) {
        this.tongTienKhuyenMai = tongTienKhuyenMai;
    }

    public double getTongTienSau() {
        return tongTienSau;
    }

    public void setTongTienSau(double tongTienSau) {
        this.tongTienSau = tongTienSau;
    }

    public double getTongTienTruoc() {
        return tongTienTruoc;
    }

    public void setTongTienTruoc(double tongTienTruoc) {
        this.tongTienTruoc = tongTienTruoc;
    }

    public double getCoc() {
        return coc;
    }

    public void setCoc(double coc) {
        this.coc = coc;
    }

    public double getThue() {
        return thue;
    }

    public void setThue(double thue) {
        this.thue = thue;
    }

    public boolean isKieuDatBan() {
        return kieuDatBan;
    }

    public void setKieuDatBan(boolean kieuDatBan) {
        this.kieuDatBan = kieuDatBan;
    }

    public boolean isKieuThanhToan() {
        return kieuThanhToan;
    }

    public void setKieuThanhToan(boolean kieuThanhToan) {
        this.kieuThanhToan = kieuThanhToan;
    }

    public SuKien getSuKien() {
        return suKien;
    }

    public void setSuKien(SuKien suKien) {
        this.suKien = suKien;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public LocalDateTime getTgCheckOut() {
        return tgCheckOut;
    }

    public void setTgCheckOut(LocalDateTime tgCheckOut) {
        this.tgCheckOut = tgCheckOut;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public LocalDateTime getTgCheckIn() {
        return tgCheckIn;
    }

    public void setTgCheckIn(LocalDateTime tgCheckIn) {
        this.tgCheckIn = tgCheckIn;
    }

    public void setTrangthai(int trangthai) {this.trangthai = trangthai;}

    public int getTrangthai() {return trangthai;}

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD='" + maHD + '\'' +
                ", khachHang=" + khachHang +
                ", nhanVien=" + nhanVien +
                ", ban=" + ban +
                ", tgCheckIn=" + tgCheckIn +
                ", tgCheckOut=" + tgCheckOut +
                ", khuyenMai=" + khuyenMai +
                ", suKien=" + suKien +
                ", kieuThanhToan=" + kieuThanhToan +
                ", kieuDatBan=" + kieuDatBan +
                ", thue=" + thue +
                ", coc=" + coc +
                ", tongTienTruoc=" + tongTienTruoc +
                ", tongTienSau=" + tongTienSau +
                ", tongTienKhuyenMai=" + tongTienKhuyenMai +
                '}';
    }
}
