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

    public String getMaLoaiBan() {
        return maLoaiBan;
    }

    public void setMaLoaiBan(String maLoaiBan) {
        this.maLoaiBan = maLoaiBan;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getTenLoaiBan() {
        return tenLoaiBan;
    }

    public void setTenLoaiBan(String tenLoaiBan) {
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
