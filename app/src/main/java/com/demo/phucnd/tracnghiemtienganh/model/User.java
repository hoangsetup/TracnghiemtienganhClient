package com.demo.phucnd.tracnghiemtienganh.model;

/**
 * Created by Hoangdv on 11:31 PM : 0003, Jun, 03, 2015.
 */
public class User {
    private String Name, User, Ngaysinh, Sdt, Email, Image;
    private int Permission, Id;

    public User() {
    }

    public User(String name, String user, String ngaysinh, String sdt, String email, String image, int permission, int id) {
        Name = name;
        User = user;
        Ngaysinh = ngaysinh;
        Sdt = sdt;
        Email = email;
        Image = image;
        Permission = permission;
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNgaysinh() {
        return Ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        Ngaysinh = ngaysinh;
    }

    public int getPermission() {
        return Permission;
    }

    public void setPermission(int permission) {
        Permission = permission;
    }

    public String getSdt() {
        return Sdt;
    }

    public void setSdt(String sdt) {
        Sdt = sdt;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
