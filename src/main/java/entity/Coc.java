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
        if(maCoc == null || !maCoc.matches("^CO\\d{4}$")) {
            throw new IllegalArgumentException("Mã cọc sai định dạng.");
        }
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
        if(phanTramCoc < 0 || phanTramCoc > 100) {
            throw new IllegalArgumentException("Phần trăm cọc từ 0 đến 100");
        }
        this.phanTramCoc = phanTramCoc;
    }

    public double getSoTienCoc() {
        return soTienCoc;
    }

    public void setSoTienCoc(double soTienCoc) {
        if(soTienCoc <0) {
            throw new IllegalArgumentException("Số tiền cọc lớn hơn 0");
        }
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

    @Override
    public String toString() {
        return "Coc{" +
                "maCoc='" + maCoc + '\'' +
                ", loaiCoc=" + loaiCoc +
                ", phanTramCoc=" + phanTramCoc +
                ", soTienCoc=" + soTienCoc +
                ", khuVuc=" + khuVuc +
                ", loaiBan=" + loaiBan +
                '}';
    }
}
