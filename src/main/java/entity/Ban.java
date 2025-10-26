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

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
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
