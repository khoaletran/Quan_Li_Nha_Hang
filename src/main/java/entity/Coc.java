package entity;

public class Coc extends ChinhSach{
    private boolean loaiDatBan;
    private int thoiGian;

    public Coc(String maChinhSach, boolean loaiDatBan, int thoiGian) {
        super(maChinhSach);
        this.loaiDatBan = loaiDatBan;
        this.thoiGian = thoiGian;
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

    @Override
    public String toString() {
        return "Coc{" +
                "loaiDatBan=" + loaiDatBan +
                ", thoiGian=" + thoiGian +
                '}';
    }
}
