/*
 * @ (#) KhuVuc.java    1.0     9/21/2025
 *
 *Copyright (c) 2025 IUH. All rights reserved
 */

package entity;

public class KhuVuc {
    private  String maKhuVuc;
    private String tenKhuVuc;

    public KhuVuc(String maKhuVuc, String tenKhuVuc) {
        this.maKhuVuc = maKhuVuc;
        this.tenKhuVuc = tenKhuVuc;
    }
    public KhuVuc(String tenKhuVuc){
        this.tenKhuVuc = tenKhuVuc;
    }

    public KhuVuc() {

    }

    public String getMaKhuVuc() {
        return maKhuVuc;
    }

    public void setMaKhuVuc(String maKhuVuc) {
        if(maKhuVuc == null || !maKhuVuc.matches("^KV\\d{4}$")) {
            throw new IllegalArgumentException("Mã khu vực sai định dạng.");
        }this.maKhuVuc = maKhuVuc;
    }

    public String getTenKhuVuc() {
        return tenKhuVuc;
    }

    public void setTenKhuVuc(String tenKhuVuc) {
        if (tenKhuVuc == null || tenKhuVuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khu vực không được để trống.");
        }this.tenKhuVuc = tenKhuVuc;
    }

    @Override
    public String toString() {
        return "KhuVuc{" +
                "maKhuVuc='" + maKhuVuc + '\'' +
                ", tenKhuVuc='" + tenKhuVuc + '\'' +
                '}';
    }
}
