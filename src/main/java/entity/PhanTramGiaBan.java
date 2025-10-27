package entity;

import entity.LoaiMon;
import entity.Mon;

import java.time.LocalDate;

public class PhanTramGiaBan {
    private String maPTGB;
    private int phanTramLoi;       // % lời (int)
    private LocalDate ngayApDung;
    private LoaiMon loaiMon;       // nếu != null → áp dụng cho loại món
    private Mon mon;           // Hoặc áp dụng riêng cho món

    public PhanTramGiaBan(){

    }

    public PhanTramGiaBan(String maPTGB, int phanTramLoi, LocalDate ngayApDung, LoaiMon loaiMon, Mon mon) {
        this.maPTGB = maPTGB;
        this.phanTramLoi = phanTramLoi;
        this.ngayApDung = ngayApDung;
        this.loaiMon = loaiMon;
        this.mon = mon;
    }

    public String getMaPTGB() {
        return maPTGB;
    }

    public void setMaPTGB(String maPTGB) {
        this.maPTGB = maPTGB;
    }

    public int getPhanTramLoi() {
        return phanTramLoi;
    }

    public void setPhanTramLoi(int phanTramLoi) {
        this.phanTramLoi = phanTramLoi;
    }

    public LocalDate getNgayApDung() {
        return ngayApDung;
    }

    public void setNgayApDung(LocalDate ngayApDung) {
        this.ngayApDung = ngayApDung;
    }

    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(LoaiMon loaiMon) {
        this.loaiMon = loaiMon;
    }

    public Mon getMon() {
        return mon;
    }

    public void setMon(Mon mon) {
        this.mon = mon;
    }

    @Override
    public String toString() {
        return "PhanTramGiaBan{" +
                "maPTGB='" + maPTGB + '\'' +
                ", phanTramLoi=" + phanTramLoi +
                ", ngayApDung=" + ngayApDung +
                ", loaiMon=" + loaiMon +
                ", mon=" + mon +
                '}';
    }
}
