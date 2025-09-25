package entity;

import java.time.LocalDate;

public class PhanTramGiaBan {
    private LoaiMon loaiMon;
    private int phanTramLoi;
    private LocalDate ngayApDung;

    public PhanTramGiaBan(int phanTramLoi, LoaiMon loaiMon, LocalDate ngayApDung) {
        this.phanTramLoi = phanTramLoi;
        this.loaiMon = loaiMon;
        this.ngayApDung = ngayApDung;
    }

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
