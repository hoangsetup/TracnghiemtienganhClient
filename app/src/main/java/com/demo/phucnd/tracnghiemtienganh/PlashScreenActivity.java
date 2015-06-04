package com.demo.phucnd.tracnghiemtienganh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


/**
 * Created by Hoangdv on 8:26 PM : 0003, Jun, 03, 2015.
 */
public class PlashScreenActivity extends Activity {
    private int time = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PlashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, time);
    }
}
