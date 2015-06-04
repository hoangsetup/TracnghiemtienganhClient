package com.demo.phucnd.tracnghiemtienganh.utilite;

import android.content.Context;
import android.widget.Toast;

import com.demo.phucnd.tracnghiemtienganh.model.User;

/**
 * Created by Hoangdv on 9:17 PM : 0003, Jun, 03, 2015.
 */
public class AppCfg {
    public static String API_KEY = "2d30ff242f8650954bfe8c993f084f4f";
    public static String API_SERVER = "http://hoangfithou.esy.es/Tracnghiemtienganh/api/";
    // public static String API_SERVER = "http://hoangfithou.esy.es/Tracnghiemtienganh/api/";
    public static String API_LOGIN = API_SERVER+"login.php";

    public static User CURRENT_USER = null;

    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
