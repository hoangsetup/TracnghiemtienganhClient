package com.demo.phucnd.tracnghiemtienganh.model;

/**
 * Created by Hoangdv on 11:21 PM : 0005, Jun, 05, 2015.
 */
public class Dapan {
    private String noidung;
    private int isTrue;

    public Dapan() {
    }

    public Dapan(int isTrue, String noidung) {
        this.isTrue = isTrue;
        this.noidung = noidung;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public int isTrue() {
        return isTrue;
    }

    public void setIsTrue(int isTrue) {
        this.isTrue = isTrue;
    }
}

