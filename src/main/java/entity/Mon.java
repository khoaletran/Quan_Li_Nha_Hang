package entity;

public class Mon {
    private String maMon;
    private String tenMon;
    private String moTa;
    private String hinhAnh;
    private LoaiMon loaiMon;
    private PhanTramGiaBan phanTramLai;
    private double giaGoc;
    public double giaBan(){
        return giaGoc*(1+(double)phanTramLai.getPhanTramLoi()/100);
    }

    public Mon(String maMon, String tenMon, String moTa, String hinhAnh, LoaiMon loaiMon, PhanTramGiaBan phanTramLai, double giaGoc) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.loaiMon = loaiMon;
        this.phanTramLai = phanTramLai;
        this.giaGoc = giaGoc;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
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
        this.loaiMon = loaiMon;
    }

    public PhanTramGiaBan getPhanTramLai() {
        return phanTramLai;
    }

    public void setPhanTramLai(PhanTramGiaBan phanTramLai) {
        this.phanTramLai = phanTramLai;
    }

    public double getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(double giaGoc) {
        this.giaGoc = giaGoc;
    }

    @Override
    public String toString() {
        return "Mon{" +
                "maMon='" + maMon + '\'' +
                ", tenMon='" + tenMon + '\'' +
                ", moTa='" + moTa + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                ", loaiMon=" + loaiMon +
                ", phanTramLai=" + phanTramLai +
                ", giaGoc=" + giaGoc +
                '}';
    }
}
