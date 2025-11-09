package entity;

import dao.PhanTramGiaBanDAO;

import java.time.LocalDate;

public class Mon {
    private String maMon;
    private String tenMon;
    private String moTa;
    private String hinhAnh;
    private double giaGoc;
    private int soLuong;
    private LoaiMon loaiMon;



    public Mon() {
    }

    public Mon(String maMon, String tenMon, String moTa, String hinhAnh, double giaGoc, int soLuong, LoaiMon loaiMon) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.giaGoc = giaGoc;
        this.soLuong = soLuong;
        this.loaiMon = loaiMon;
    }

    // 🔹 Lấy phần trăm lời hiện tại (ưu tiên của món, nếu không có thì lấy loại món)
    public int getPhanTramGiaBanHienTai() {
        PhanTramGiaBan ptMon = PhanTramGiaBanDAO.getLatestForMon(maMon);

        if (ptMon != null) {
            return ptMon.getPhanTramLoi();
        }

        PhanTramGiaBan ptLoai = PhanTramGiaBanDAO.getLatestForLoaiMon(loaiMon.getMaLoaiMon());
        if (ptLoai != null) {
            return ptLoai.getPhanTramLoi();
        }

        return 0;
    }
    // 🔹 Tính giá bán thực tế
    public double getGiaBan() {
        int phanTram = getPhanTramGiaBanHienTai();
        return giaGoc * (1 + phanTram / 100.0);
    }

    public int getPTGBTaiHD(HoaDon hd) {
        if (hd == null) return 0;

        LocalDate ngayHD = hd.getTgCheckIn().toLocalDate();

        PhanTramGiaBan ptMon = PhanTramGiaBanDAO.getEffectiveForMonAtDate(maMon, ngayHD);
        if (ptMon != null) {
            return ptMon.getPhanTramLoi();
        }

        if (loaiMon != null) {
            PhanTramGiaBan ptLoai = PhanTramGiaBanDAO.getEffectiveForLoaiMonAtDate(loaiMon.getMaLoaiMon(), ngayHD);
            if (ptLoai != null) {
                return ptLoai.getPhanTramLoi();
            }
        }

        return 0;
    }

    public double getGiaBanTaiLucLapHD(HoaDon hd) {
        int phanTram = getPTGBTaiHD(hd);
        return giaGoc * (1 + phanTram / 100.0);
    }



    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        if(maMon == null || !maMon.matches("^MM\\d{4}$")) {
            throw new IllegalArgumentException("Mã món sai định dạng.");
        }this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        if (tenMon == null || tenMon.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên món không được để trống.");
        }this.tenMon = tenMon;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(LoaiMon loaiMon) {
        if (loaiMon == null) {
            throw new IllegalArgumentException("Loại món không được để trống.");
        }this.loaiMon = loaiMon;
    }

    public double getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(double giaGoc) {
        if (giaGoc < 0) {
            throw new IllegalArgumentException("Giá gốc không được nhỏ hơn 0.");
        }
        this.giaGoc = giaGoc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    @Override
    public String toString() {
        return "Mon{" +
                "maMon='" + maMon + '\'' +
                ", tenMon='" + tenMon + '\'' +
                ", moTa='" + moTa + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                ", giaGoc=" + giaGoc +
                ", soLuong=" + soLuong +
                ", loaiMon=" + loaiMon +
                '}';
    }
}
