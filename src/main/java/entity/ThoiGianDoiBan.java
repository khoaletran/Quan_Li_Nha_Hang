package entity;

public class ThoiGianDoiBan {
    private String maTGDB;
    private boolean loaiDatBan; //true=Online (Đặt bàn trước);false=Offline (thêm vào waitlist nếu khách đến cửa hàng mà hết bàn)
    private int thoiGian;

    public String getMaTGDB() {
        return maTGDB;
    }

    public void setMaTGDB(String maTGDB) {
        this.maTGDB = maTGDB;
    }

    public boolean isLoaiDatBan() {
        return loaiDatBan;
    }

    public void setLoaiDatBan(boolean loaiDatBan) {
        this.loaiDatBan = loaiDatBan;
    }

    public int getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(int thoiGian) {
        this.thoiGian = thoiGian;
    }
    public ThoiGianDoiBan(){

    }
    public ThoiGianDoiBan(String maTGDB, boolean loaiDatBan, int thoiGian) {
        this.maTGDB = maTGDB;
        this.loaiDatBan = loaiDatBan;
        this.thoiGian = thoiGian;
    }

    @Override
    public String toString() {
        return "ThoiGianDoiBan{" +
                "maTGDB='" + maTGDB + '\'' +
                ", loaiDatBan=" + loaiDatBan +
                ", thoiGian=" + thoiGian +
                '}';
    }
}
