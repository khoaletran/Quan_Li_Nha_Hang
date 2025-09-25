package entity;

public class ChinhSach {
    private String maChinhSach;

    public ChinhSach(String maChinhSach) {
        this.maChinhSach = maChinhSach;
    }

    public String getMaChinhSach() {
        return maChinhSach;
    }

    public void setMaChinhSach(String maChinhSach) {
        this.maChinhSach = maChinhSach;
    }

    @Override
    public String toString() {
        return "ChinhSach{" +
                "maChinhSach='" + maChinhSach + '\'' +
                '}';
    }
}
