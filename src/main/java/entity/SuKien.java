package entity;

public class SuKien {
    private String maSK;
    private String tenSK;
    private String mota;
    private double gia;

    public SuKien(String maSK, String tenSK, String mota, double gia) {
        this.maSK = maSK;
        this.tenSK = tenSK;
        this.mota = mota;
        this.gia = gia;
    }

    public SuKien() {

    }

    public String getMaSK() {
        return maSK;
    }

    public void setMaSK(String maSK) {
        this.maSK = maSK;
    }

    public String getTenSK() {
        return tenSK;
    }

    public void setTenSK(String tenSK) {
        this.tenSK = tenSK;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    @Override
    public String toString() {
        return "SuKien{" +
                "maSK='" + maSK + '\'' +
                ", tenSK='" + tenSK + '\'' +
                ", mota='" + mota + '\'' +
                ", gia=" + gia +
                '}';
    }
}
