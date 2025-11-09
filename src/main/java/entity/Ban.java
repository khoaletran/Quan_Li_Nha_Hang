/*
 * @ (#) Ban.java    1.0     9/21/2025
 *
 *Copyright (c) 2025 IUH. All rights reserved
 */

package entity;

public class Ban {
    private String maBan;
    private LoaiBan loaiBan;
    private KhuVuc khuVuc;
    private boolean trangThai;

    public Ban(String maBan, KhuVuc khuVuc, LoaiBan loaiBan, boolean trangThai) {
        setMaBan(maBan);
        setLoaiBan(loaiBan);
        setKhuVuc(khuVuc);
        setTrangThai(trangThai);
    }

    public Ban() {

    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        if (!maBan.matches("^[BW][OIV]\\d{4}$")) {
            throw new IllegalArgumentException(
                    "Mã bàn không hợp lệ. Phải gồm 6 ký tự: B hoặc W + (O/I/V) + 4 chữ số."
            );
        }this.maBan = maBan;
    }

    public KhuVuc getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(KhuVuc khuVuc) {
        if (khuVuc == null) {
            throw new IllegalArgumentException("Khu vực không được để trống.");
        }this.khuVuc = khuVuc;
    }

    public LoaiBan getLoaiBan() {
        return loaiBan;
    }

    public void setLoaiBan(LoaiBan loaiBan) {
        if (loaiBan == null) {
            throw new IllegalArgumentException("Loại bàn không được để trống.");
        }this.loaiBan = loaiBan;
    }

    public boolean isTrangThai() {return trangThai;}

    public void setTrangThai(boolean trangThai) {this.trangThai = trangThai;}

    @Override
    public String toString() {
        return "Ban{" +
                "maBan='" + maBan + '\'' +
                ", loaiBan=" + loaiBan +
                ", khuVuc=" + khuVuc +
                '}';
    }
}
