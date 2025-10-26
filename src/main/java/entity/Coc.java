package entity;

public class Coc {
    private String maCoc;
    private boolean loaiCoc;
    private int phanTramCoc;
    private double soTienCoc;
    private KhuVuc khuVuc;
    private LoaiBan loaiBan;

    public String getMaCoc() {
        return maCoc;
    }

    public void setMaCoc(String maCoc) {
        this.maCoc = maCoc;
    }

    public boolean isLoaiCoc() {
        return loaiCoc;
    }

    public void setLoaiCoc(boolean loaiCoc) {
        this.loaiCoc = loaiCoc;
    }

    public int getPhanTramCoc() {
        return phanTramCoc;
    }

    public void setPhanTramCoc(int phanTramCoc) {
        this.phanTramCoc = phanTramCoc;
    }

    public double getSoTienCoc() {
        return soTienCoc;
    }

    public void setSoTienCoc(double soTienCoc) {
        this.soTienCoc = soTienCoc;
    }

    public KhuVuc getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(KhuVuc khuVuc) {
        this.khuVuc = khuVuc;
    }

    public LoaiBan getLoaiBan() {
        return loaiBan;
    }

    public void setLoaiBan(LoaiBan loaiBan) {
        this.loaiBan = loaiBan;
    }

    public Coc() {
    }

    public Coc(String maCoc, boolean loaiCoc, int phanTramCoc, double soTienCoc, KhuVuc khuVuc, LoaiBan loaiBan) {
        this.maCoc = maCoc;
        this.loaiCoc = loaiCoc;
        this.phanTramCoc = phanTramCoc;
        this.soTienCoc = soTienCoc;
        this.khuVuc = khuVuc;
        this.loaiBan = loaiBan;
    }
}
