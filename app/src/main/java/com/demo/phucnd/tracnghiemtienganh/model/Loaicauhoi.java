package com.demo.phucnd.tracnghiemtienganh.model;

import java.util.Vector;

/**
 * Created by Hoangdv on 9:13 PM : 0005, Jun, 05, 2015.
 */
public class Loaicauhoi {
    private int id;
    private String tenloai;
    private Vector<Cauhoi> listCauhoi;

    public Loaicauhoi() {
        listCauhoi = new Vector<>();
    }

    public Loaicauhoi(int id, String tenloai, Vector<Cauhoi> listCauhoi) {
        listCauhoi = new Vector<>();
        this.id = id;
        this.tenloai = tenloai;
        this.listCauhoi = listCauhoi;
    }

    public String getTenloai() {
        return tenloai;
    }

    public void setTenloai(String tenloai) {
        this.tenloai = tenloai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vector<Cauhoi> getListCauhoi() {
        return listCauhoi;
    }

    public void setListCauhoi(Vector<Cauhoi> listCauhoi) {
        this.listCauhoi = listCauhoi;
    }


    public void setLoaicauhoi(Cauhoi cauhoi) {
        cauhoi.setLoaicauhoi(this);
    }
}
