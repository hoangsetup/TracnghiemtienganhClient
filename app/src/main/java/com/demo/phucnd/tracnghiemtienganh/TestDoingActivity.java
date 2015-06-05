package com.demo.phucnd.tracnghiemtienganh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.demo.phucnd.tracnghiemtienganh.appcontroller.AppController;
import com.demo.phucnd.tracnghiemtienganh.model.Cauhoi;
import com.demo.phucnd.tracnghiemtienganh.model.Dapan;
import com.demo.phucnd.tracnghiemtienganh.utilite.AppCfg;
import com.demo.phucnd.tracnghiemtienganh.utilite.MyPostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Hoangdv on 9:46 PM : 0005, Jun, 05, 2015.
 */
public class TestDoingActivity extends Activity implements View.OnClickListener {
    private Vector<Cauhoi> listCauhoi = new Vector<>();
    private int page, total = 20;
    private ImageButton imageButton_back, imageButton_next, imageButton_done;
    private TextView textView_time, textView_noidung, textView_page;
    private RadioButton radioButton_a, radioButton_b, radioButton_c, radioButton_d;
    private CountDownTimer countDownTimer;
    private int loaicauhoi;
    private ProgressDialog dialog;

    private int ketqua = 0;
    private String[] bailam = new String[20];

    private boolean isDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testdoing);

        loaicauhoi = getIntent().getIntExtra("loaicauhoi", 0);

        findView();
        regEvent();
        setUpData();

        countDownTimer = new CountDownTimer(AppCfg.TIMEINMILISECOND, 1000) {
            @Override
            public void onTick(long l) {
                textView_time.setText("Time remaining: " + (l / 1000 - 1) + "s");
            }

            @Override
            public void onFinish() {
                if(!isDone)
                    upKetqua();
                isDone = true;
            }
        }.start();
    }

    private void findView() {
        //
        imageButton_back = (ImageButton) findViewById(R.id.imageButton_back);
        imageButton_next = (ImageButton) findViewById(R.id.imageButton_next);
        imageButton_done = (ImageButton) findViewById(R.id.imageButton_done);

        //
        textView_time = (TextView) findViewById(R.id.textView_time);
        textView_noidung = (TextView) findViewById(R.id.textView_noidung);
        textView_page = (TextView) findViewById(R.id.textView_page);

        //
        radioButton_a = (RadioButton) findViewById(R.id.radioButton_a);
        radioButton_b = (RadioButton) findViewById(R.id.radioButton_b);
        radioButton_c = (RadioButton) findViewById(R.id.radioButton_c);
        radioButton_d = (RadioButton) findViewById(R.id.radioButton_d);
    }

    private void regEvent() {
        imageButton_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page + 1 < total)
                    page++;
                setUpFromPage(page);
            }
        });
        imageButton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page > 0)
                    page--;
                setUpFromPage(page);
            }
        });
        imageButton_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isDone){
                    upKetqua();
                }
                isDone = true;
                setUpFromPage(page);
                radioButton_a.setEnabled(false);
                radioButton_b.setEnabled(false);
                radioButton_c.setEnabled(false);
                radioButton_d.setEnabled(false);
                AppCfg.showToast(TestDoingActivity.this, "Result test: " + ketqua + "/" + total);
                countDownTimer.cancel();
            }
        });

        radioButton_a.setOnClickListener(this);
        radioButton_b.setOnClickListener(this);
        radioButton_c.setOnClickListener(this);
        radioButton_d.setOnClickListener(this);

    }

    private void setUpData() {
        dialog = ProgressDialog.show(this, "Thông báo", "Đang thực hiện...", true, false);
        Map<String, String> params = new HashMap<>();
        params.put("loaicauhoi", loaicauhoi + "");
        params.put("count", total + "");
        MyPostRequest request = new MyPostRequest(Request.Method.POST, AppCfg.API_CAUHOI, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dialog.dismiss();
                try {
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if (success == 1) {
                        JSONArray arrayCauhoi = jsonObject.getJSONArray("cauhoi");
                        for (int i = 0; i < arrayCauhoi.length(); i++) {
                            JSONObject obj = arrayCauhoi.getJSONObject(i);
                            Cauhoi cauhoi = new Cauhoi();
                            cauhoi.setId(obj.getInt("id"));
                            cauhoi.setNoidung(obj.getString("noidung"));
                            JSONArray arrayDapan = obj.getJSONArray("dapan");
                            for (int j = 0; j < arrayDapan.length(); j++) {
                                JSONObject o = arrayDapan.getJSONObject(j);
                                cauhoi.setDapan(new Dapan(o.getInt("dadung"), o.getString("noidung")));
                            }
                            listCauhoi.add(cauhoi);
                        }
                        total = listCauhoi.size();
                        setUpFromPage(page);
                    } else {
                        total = 0;
                        AppCfg.showToast(TestDoingActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                AppCfg.showToast(TestDoingActivity.this, "Network error!");
                volleyError.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", AppCfg.API_KEY);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    public void upKetqua(){
        dialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("ketqua", ketqua + "/" + total);
        params.put("userid", AppCfg.CURRENT_USER.getId()+"");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        params.put("thoigian", dateFormat.format(Calendar.getInstance().getTime()));
        MyPostRequest request = new MyPostRequest(Request.Method.POST, "", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dialog.dismiss();
                try {
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    AppCfg.showToast(TestDoingActivity.this, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                AppCfg.showToast(TestDoingActivity.this, "Network error!");
                volleyError.printStackTrace();
            }
        }){

        };
        AppController.getInstance().addToRequestQueue(request);
    }

    public void setUpFromPage(int page) {

        if (total == 0)
            return;
        Cauhoi cauhoi = listCauhoi.get(page);
        textView_noidung.setText(cauhoi.getNoidung());
        textView_page.setText((page + 1) + "/" + total);
        radioButton_a.setText("A. " + cauhoi.getDapans().get(0).getNoidung());
        radioButton_b.setText("B. " + cauhoi.getDapans().get(1).getNoidung());
        radioButton_c.setText("C. " + cauhoi.getDapans().get(2).getNoidung());
        radioButton_d.setText("D. " + cauhoi.getDapans().get(3).getNoidung());

        radioButton_a.setChecked(false);
        radioButton_b.setChecked(false);
        radioButton_c.setChecked(false);
        radioButton_d.setChecked(false);

        if (isDone) {
            if(!TextUtils.isEmpty(bailam[page])) {
                if (bailam[page].equalsIgnoreCase("A"))
                    radioButton_a.setChecked(true);
                if (bailam[page].equalsIgnoreCase("B"))
                    radioButton_b.setChecked(true);
                if (bailam[page].equalsIgnoreCase("C"))
                    radioButton_c.setChecked(true);
                if (bailam[page].equalsIgnoreCase("D"))
                    radioButton_d.setChecked(true);
            }


            if (listCauhoi.get(page).getDapans().get(0).isTrue() == 1)
                radioButton_a.setTextColor(Color.parseColor("#FF0000"));
            if (listCauhoi.get(page).getDapans().get(1).isTrue() == 1)
                radioButton_b.setTextColor(Color.parseColor("#FF0000"));
            if (listCauhoi.get(page).getDapans().get(2).isTrue() == 1)
                radioButton_c.setTextColor(Color.parseColor("#FF0000"));
            if (listCauhoi.get(page).getDapans().get(3).isTrue() == 1)
                radioButton_d.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioButton_a:
                if (listCauhoi.get(page).getDapans().get(0).isTrue() == 1){
                    if(TextUtils.isEmpty(bailam[page]) || !bailam[page].equalsIgnoreCase("A")){
                        ketqua++;
                    }
                }
                bailam[page] = "A";
                break;
            case R.id.radioButton_b:
                if (listCauhoi.get(page).getDapans().get(1).isTrue() == 1){
                    if(TextUtils.isEmpty(bailam[page]) || !bailam[page].equalsIgnoreCase("B")){
                        ketqua++;
                    }
                }
                bailam[page] = "B";
                break;
            case R.id.radioButton_c:
                if (listCauhoi.get(page).getDapans().get(2).isTrue() == 1){
                    if(TextUtils.isEmpty(bailam[page]) || !bailam[page].equalsIgnoreCase("C")){
                        ketqua++;
                    }
                }
                bailam[page] = "C";
            case R.id.radioButton_d:
                if (listCauhoi.get(page).getDapans().get(3).isTrue() == 1){
                    if(TextUtils.isEmpty(bailam[page]) || !bailam[page].equalsIgnoreCase("D")){
                        ketqua++;
                    }
                }
                bailam[page] = "D";
            default:
                break;
        }
    }
}
