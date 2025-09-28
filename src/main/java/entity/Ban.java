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

    public Ban(String maBan, KhuVuc khuVuc, LoaiBan loaiBan) {
        this.maBan = maBan;
        this.khuVuc = khuVuc;
        this.loaiBan = loaiBan;
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

    @Override
    public String toString() {
        return "Ban{" +
                "maBan='" + maBan + '\'' +
                ", loaiBan=" + loaiBan +
                ", khuVuc=" + khuVuc +
                '}';
    }
}
