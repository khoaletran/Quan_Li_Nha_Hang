/*
 * @ (#) LoaiBan.java    1.0     9/21/2025
 *
 *Copyright (c) 2025 IUH. All rights reserved
 */

package entity;

public class LoaiBan {
    private String maLoaiBan;
    private String tenLoaiBan;
    private int soLuong;

    public LoaiBan(String maLoaiBan, int soLuong, String tenLoaiBan) {
        this.maLoaiBan = maLoaiBan;
        this.soLuong = soLuong;
        this.tenLoaiBan = tenLoaiBan;
    }
    public LoaiBan(String tenLoaiBan){
        this.tenLoaiBan = tenLoaiBan;
    }

    public String getMaLoaiBan() {
        return maLoaiBan;
    }

    public void setMaLoaiBan(String maLoaiBan) {
        if(maLoaiBan == null || !maLoaiBan.matches("^LB\\d{4}$")) {
            throw new IllegalArgumentException("Mã loại bàn sai định dạng.");
        }this.maLoaiBan = maLoaiBan;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0) {
            throw new IllegalArgumentException("Số lượng không được nhỏ hơn 0.");
        }this.soLuong = soLuong;
    }

    public String getTenLoaiBan() {
        return tenLoaiBan;
    }

    public void setTenLoaiBan(String tenLoaiBan) {
        if (tenLoaiBan == null || tenLoaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại bàn không được để trống.");
        }
        this.tenLoaiBan = tenLoaiBan;
    }

    @Override
    public String toString() {
        return "LoaiBan{" +
                "maLoaiBan='" + maLoaiBan + '\'' +
                ", tenLoaiBan='" + tenLoaiBan + '\'' +
                ", soLuong=" + soLuong +
                '}';
    }
}
