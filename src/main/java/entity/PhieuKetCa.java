package entity;

import java.time.LocalDateTime;

public class PhieuKetCa {
    private String maPhieu;
    private NhanVien nhanVien;
    private boolean ca;             // false: ca sáng, true: ca tối
    private int soHoaDon;
    private double tienMat;
    private double tienCK;
    private double tienChenhLech;
    private LocalDateTime ngayKetCa;
    private String moTa;

    public PhieuKetCa() {}

    public PhieuKetCa(String maPhieu, NhanVien nhanVien, boolean ca, int soHoaDon,
                      double tienMat, double tienCK, double tienChenhLech,
                      LocalDateTime ngayKetCa, String moTa) {
        setMaPhieu(maPhieu);
        setNhanVien(nhanVien);
        setCa(ca);
        setSoHoaDon(soHoaDon);
        setTienMat(tienMat);
        setTienCK(tienCK);
        setTienChenhLech(tienChenhLech);
        setNgayKetCa(ngayKetCa);
        setMoTa(moTa);
    }

    // ===== Getter & Setter =====
    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public boolean isCa() { return ca;}

    public void setCa(boolean ca) { this.ca = ca; }

    public int getSoHoaDon() { return soHoaDon; }

    public void setSoHoaDon(int soHoaDon) { this.soHoaDon = soHoaDon; }

    public double getTienMat() { return tienMat; }

    public void setTienMat(double tienMat) { this.tienMat = tienMat; }

    public double getTienCK() { return tienCK; }

    public void setTienCK(double tienCK) { this.tienCK = tienCK; }

    public double getTienChenhLech() { return tienChenhLech; }

    public void setTienChenhLech(double tienChenhLech) { this.tienChenhLech = tienChenhLech; }

    public LocalDateTime getNgayKetCa() { return ngayKetCa; }

    public void setNgayKetCa(LocalDateTime ngayKetCa) { this.ngayKetCa = ngayKetCa; }

    public String getMoTa() { return moTa; }

    public void setMoTa(String moTa) { this.moTa = moTa; }

    @Override
    public String toString() {
        return "PhieuKetCa{" +
                "maPhieu='" + maPhieu + '\'' +
                ", nhanVien=" + nhanVien +
                ", ca=" + ca +
                ", soHoaDon=" + soHoaDon +
                ", tienMat=" + tienMat +
                ", tienCK=" + tienCK +
                ", tienChenhLech=" + tienChenhLech +
                ", ngayKetCa=" + ngayKetCa +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}

