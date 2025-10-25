package entity;

import java.time.LocalDate;

public class PhanTramGiaBan {
    private String maPTGB;
    private LoaiMon loaiMon;
    private int phanTramLoi;
    private LocalDate ngayApDung;

    public PhanTramGiaBan(String maPTGB, LoaiMon loaiMon, int phanTramLoi, LocalDate ngayApDung) {
        this.maPTGB = maPTGB;
        this.phanTramLoi = phanTramLoi;
        this.loaiMon = loaiMon;
        this.ngayApDung = ngayApDung;
    }

    public void setMaPTGB(String maPTGB) {this.maPTGB = maPTGB;}

    public String getMaPTGB() {return maPTGB;}

    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(LoaiMon loaiMon) {
        this.loaiMon = loaiMon;
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

    @Override
    public String toString() {
        return "PhanTramGiaBan{" +
                "loaiMon=" + loaiMon +
                ", phanTramLoi=" + phanTramLoi +
                ", ngayApDung=" + ngayApDung +
                '}';
    }
}
