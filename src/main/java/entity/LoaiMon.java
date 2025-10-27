package entity;

public class LoaiMon {
    private String maLoaiMon;
    private String tenLoaiMon;
    private String moTa;

    public LoaiMon(String maLoaiMon, String tenLoaiMon, String moTa) {
        this.maLoaiMon = maLoaiMon;
        this.tenLoaiMon = tenLoaiMon;
        this.moTa = moTa;
    }

    public LoaiMon() {

    }

    public LoaiMon(String maLoaiMon) {
        this.maLoaiMon = maLoaiMon;
    }

    public String getMaLoaiMon() {
        return maLoaiMon;
    }

    public void setMaLoaiMon(String maLoaiMon) {
        this.maLoaiMon = maLoaiMon;
    }

    public String getTenLoaiMon() {
        return tenLoaiMon;
    }

    public void setTenLoaiMon(String tenLoaiMon) {
        this.tenLoaiMon = tenLoaiMon;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return "LoaiMon{" +
                "maLoaiMon='" + maLoaiMon + '\'' +
                ", tenLoaiMon='" + tenLoaiMon + '\'' +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}
