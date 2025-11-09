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
        if(maSK == null || !maSK.matches("^SK\\d{4}$")) {
            throw new IllegalArgumentException("Mã sự kiện sai định dạng.");
        }
        this.maSK = maSK;
    }

    public String getTenSK() {
        return tenSK;
    }

    public void setTenSK(String tenSK) {
        if(tenSK == null) {
            throw new IllegalArgumentException("Tên sự kiện không được rỗng.");
        }
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
        if(gia < 0) {
            throw new IllegalArgumentException("Giá không được âm");
        }
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
