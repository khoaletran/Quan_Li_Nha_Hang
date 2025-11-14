/*
 * @ (#) HoaDon.java    2.1     10/27/2025
 */

package entity;

import dao.ChiTietHDDAO;
import dao.CocDAO;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDon {
    private String maHD;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private Ban ban;
    private LocalDateTime tgLapHD;
    private LocalDateTime tgCheckIn;
    private LocalDateTime tgCheckOut;
    private KhuyenMai khuyenMai;
    private SuKien suKien;
    private int trangthai;
    private boolean kieuThanhToan;
    private boolean kieuDatBan;
    private int soLuong;
    private String moTa;

    // Derived fields
    private double tongTienTruoc;
    private double tongTienKhuyenMai;
    private double thue;
    private double coc;
    private double tongTienSau;

    public HoaDon() {}

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) {
        if (maHD == null || !maHD.matches("^HD[01]\\d{6}\\d{4}$")) {
            throw new IllegalArgumentException("Mã hóa đơn sai định dạng!"); // (Định dạng đúng: HDxDDMMYYnnnn)
        }
        this.maHD = maHD;
    }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public Ban getBan() { return ban; }
    public void setBan(Ban ban) { this.ban = ban; }

    public LocalDateTime getTgLapHD() { return tgLapHD; }

    public void setTgLapHD(LocalDateTime tgLapHD) {
        this.tgLapHD = tgLapHD;
    }

    public LocalDateTime getTgCheckIn() { return tgCheckIn; }
    public void setTgCheckIn(LocalDateTime tgCheckIn) {
        if (tgCheckIn == null) {
            this.tgCheckIn = null;
            return;
        }
        if (tgLapHD == null || tgCheckOut == null || tgCheckIn.isAfter(tgLapHD)) {
            this.tgCheckIn = tgCheckIn;
        }

    }

    public LocalDateTime getTgCheckOut() { return tgCheckOut; }
    public void setTgCheckOut(LocalDateTime tgCheckOut) {
        if (tgCheckOut== null) {
            this.tgCheckOut = null;
            return;
        }
        if (this.tgCheckIn != null &&!tgCheckOut.isAfter(this.tgCheckIn)) {
            throw new IllegalArgumentException("Thời gian check out phải sau thời gian check in");
        }
        this.tgCheckOut = tgCheckOut;
    }

    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(KhuyenMai khuyenMai) { this.khuyenMai = khuyenMai; }

    public SuKien getSuKien() { return suKien; }
    public void setSuKien(SuKien suKien) { this.suKien = suKien; }

    public boolean isKieuThanhToan() { return kieuThanhToan; }
    public void setKieuThanhToan(boolean kieuThanhToan) { this.kieuThanhToan = kieuThanhToan; }

    public boolean isKieuDatBan() { return kieuDatBan; }
    public void setKieuDatBan(boolean kieuDatBan) { this.kieuDatBan = kieuDatBan; }

    public int getTrangthai() { return trangthai; }
    public void setTrangthai(int trangthai) { this.trangthai = trangthai; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) {
        if(soLuong <=0){
            throw new IllegalArgumentException("Số lượng người phải lớn hơn 0");
        }
        this.soLuong = soLuong;
    }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public double getTongTienTruoc() {
        tongTienTruoc = 0;
        List<ChiTietHoaDon> ds = ChiTietHDDAO.getByMaHD(this.maHD);
        for (ChiTietHoaDon ct : ds) {
            tongTienTruoc += ct.getThanhTien();
        }
        if (suKien != null) {
            tongTienTruoc += getSuKien().getGia();
        }
        return tongTienTruoc;
    }

    public double getTongTienKhuyenMai() {
        double giamKH = 0;
        double giamKM = 0;

        if (khachHang != null && khachHang.getHangKhachHang() != null)
            giamKH = khachHang.getHangKhachHang().getGiamGia();
        if (khuyenMai != null)
            giamKM = khuyenMai.getPhanTRamGiamGia();

        tongTienKhuyenMai = getTongTienTruoc() * (giamKH + giamKM) / 100;
        return tongTienKhuyenMai;
    }

    public double getThue() {
        thue = getTongTienTruoc() * 0.1; // 10% VAT
        return thue;
    }

    public double getCoc() {
        if (ban == null) return 0;
        String maKV = ban.getKhuVuc().getMaKhuVuc();
        String maLB = ban.getLoaiBan().getMaLoaiBan();

        Coc c = new CocDAO().getByKhuVucVaLoaiBan(maKV, maLB);
        if (c != null) {
            if (c.isLoaiCoc()) coc = getTongTienTruoc() * c.getPhanTramCoc() / 100;
            else coc = c.getSoTienCoc();
        }
        return coc;
    }

    public double getTongTienSau() {
        tongTienSau = getTongTienTruoc() - getTongTienKhuyenMai() + getThue();
        return tongTienSau;
    }

    // Tổng cuối cùng (nếu cần)
    public double tinhTongSauThue() {
        return getTongTienSau();
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD='" + maHD + '\'' +
                ", khachHang=" + (khachHang != null ? khachHang.getMaKhachHang() : "null") +
                ", nhanVien=" + (nhanVien != null ? nhanVien.getMaNV() : "null") +
                ", ban=" + (ban != null ? ban.getMaBan() : "null") +
                ", tgCheckIn=" + tgCheckIn +
                ", tgCheckOut=" + tgCheckOut +
                ", trangthai=" + trangthai +
                ", tongTienSau=" + getTongTienSau() +
                '}';
    }
}
