package com.demo.phucnd.tracnghiemtienganh.model;

import java.util.Vector;

/**
 * Created by Hoangdv on 9:13 PM : 0005, Jun, 05, 2015.
 */
public class Cauhoi {
    private int id;
    private Loaicauhoi loaicauhoi;
    private String noidung;
    private Vector<Dapan> dapans = new Vector<>();


    public Cauhoi() {
    }

    public Cauhoi(int id, Loaicauhoi loaicauhoi, String noidung) {
        this.id = id;
        this.loaicauhoi = loaicauhoi;
        this.noidung = noidung;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Loaicauhoi getLoaicauhoi() {
        return loaicauhoi;
    }

    public void setLoaicauhoi(Loaicauhoi loaicauhoi) {
        this.loaicauhoi = loaicauhoi;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public Vector<Dapan> getDapans() {
        return dapans;
    }

    public void setDapans(Vector<Dapan> dapans) {
        this.dapans = dapans;
    }

    public void setDapan(Dapan dapan){
        this.dapans.add(dapan);
    }
}
