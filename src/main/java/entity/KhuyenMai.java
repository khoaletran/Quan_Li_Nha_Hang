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
        if(maKM == null || !maKM.matches("^KM\\d{4}$")) {
            throw new IllegalArgumentException("Mã khuyến mãi sai định dạng.");
        }
        this.maKM = maKM;

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
        if(tenKM == null) {
            throw new IllegalArgumentException("Tên khuyến mãi không được rỗng.");
        }
        this.tenKM = tenKM;
    }

    public String getMaThayThe() {
        return maThayThe;
    }

    public void setMaThayThe(String maThayThe) {
        this.maThayThe = maThayThe;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if(soLuong < 0) {
            throw new IllegalArgumentException("Số lượng không âm");
        }
        this.soLuong = soLuong;
    }

    public int getPhanTRamGiamGia() {
        return phanTRamGiamGia;
    }

    public void setPhanTRamGiamGia(int phanTramGiamGia) {
        if(phanTramGiamGia < 0 || phanTramGiamGia >100) {
            throw new IllegalArgumentException("Phần trăm giảm giá từ 0 đến 100");
        }
        this.phanTRamGiamGia = phanTramGiamGia;
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
        if (ngayPhatHanh == null) {
            throw new IllegalArgumentException("Ngày phát hành không được rỗng");
        }
        if (ngayPhatHanh.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày phát hành phải là ngày hiện tại hoặc sau ngày hiện tại");
        }
        this.ngayPhatHanh = ngayPhatHanh;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        if (ngayKetThuc == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được rỗng");
        }
        if (!ngayKetThuc.isAfter(this.ngayPhatHanh)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày phát hành");
        }
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
