package com.demo.phucnd.tracnghiemtienganh.model;

/**
 * Created by Hoangdv on 3:38 PM : 0024, May, 24, 2015.
 */
public class Child {

    private String Name;
    private int Image;

    public Child(String name, int image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int Image) {
        this.Image = Image;
    }
}
