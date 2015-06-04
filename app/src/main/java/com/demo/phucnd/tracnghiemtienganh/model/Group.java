package com.demo.phucnd.tracnghiemtienganh.model;

/**
 * Created by Hoangdv on 3:38 PM : 0024, May, 24, 2015.
 */

import java.util.ArrayList;

public class Group {

    private String Name;
    private ArrayList<Child> Items;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public ArrayList<Child> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Child> Items) {
        this.Items = Items;
    }

}

