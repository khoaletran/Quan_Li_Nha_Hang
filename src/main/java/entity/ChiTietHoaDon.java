package entity;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private Mon mon;
    private int soLuong;
    private double thanhTien;

    public ChiTietHoaDon(HoaDon hd, Mon mon, int soLuong) {
        setHoaDon(hd);
        this.mon = mon;
        this.soLuong = soLuong;
        this.thanhTien = mon.giaBan() * soLuong;
    }

    public void setHoaDon(HoaDon hoaDon) {this.hoaDon = hoaDon;}

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public Mon getMon() {
        return mon;
    }

    public void setMon(Mon mon) {
        this.mon = mon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "mon=" + (mon != null ? mon.getTenMon() : "null") +
                ", soLuong=" + soLuong +
                ", thanhTien=" + thanhTien +
                '}';
    }
}
