package com.demo.phucnd.tracnghiemtienganh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.demo.phucnd.tracnghiemtienganh.appcontroller.AppController;
import com.demo.phucnd.tracnghiemtienganh.model.User;
import com.demo.phucnd.tracnghiemtienganh.utilite.AppCfg;
import com.demo.phucnd.tracnghiemtienganh.utilite.MyPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Hoangdv on 8:36 PM : 0003, Jun, 03, 2015.
 */
public class LoginActivity extends Activity {
    Button button_login;
    EditText editText_user, editText_pass;
    TextView textView_reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWidgetControl();
        registryEvent();
        getSavedData();
    }

    private void getWidgetControl() {
        button_login = (Button) findViewById(R.id.button_login);
        editText_user = (EditText) findViewById(R.id.editText_user);
        editText_pass = (EditText) findViewById(R.id.editText_password);
        textView_reg = (TextView)findViewById(R.id.textView_regLink);
    }

    private void registryEvent(){
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = editText_user.getText().toString();
                if(TextUtils.isEmpty(user)){
                    editText_user.setError("*");
                    return;
                }
                String pass = editText_pass.getText().toString();
                if(TextUtils.isEmpty(pass)){
                    editText_pass.setError("*");
                    return;
                }
                login(user, pass);
            }
        });

        textView_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 111);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 111){
            try {
                Bundle bundle = data.getBundleExtra("data");
                String username = bundle.getString("username");
                String password = bundle.getString("password");
                editText_user.setText(username);
                editText_pass.setText(password);
                Thread.sleep(300);
                login(username, password);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void login(String user, String pass) {
        final ProgressDialog dialog = ProgressDialog.show(this, "Thông báo", "Đang đăng nhập...", true, false);
        Map<String, String> params = new HashMap<>();
        params.put("username", user);
        params.put("password", pass);
        MyPostRequest request = new MyPostRequest(Request.Method.POST,AppCfg.API_LOGIN, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dialog.dismiss();
                try {
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if(success == 1){
                        JSONObject obj = jsonObject.getJSONArray("user").getJSONObject(0);
                        User user  = new User();
                        user.setUser(obj.getString("sUser"));
                        user.setEmail(obj.getString("sEmail"));
                        user.setId(obj.getInt("PK_iUserId"));
                        user.setName(obj.getString("sName"));
                        user.setNgaysinh(obj.getString("sNgaysinh"));
                        user.setSdt(obj.getString("sSdt"));
                        user.setImage(obj.getString("image"));
                        saveData();
                        AppCfg.CURRENT_USER = user;
                        AppCfg.PASSWORD = editText_pass.getText().toString();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        AppCfg.showToast(LoginActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                AppCfg.showToast(LoginActivity.this, "Network error!");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", AppCfg.API_KEY);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    public void saveData(){

        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox_keeplogin);
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(checkBox.isChecked()){
            editor.putString("username", editText_user.getText().toString());
            editor.putString("password", editText_pass.getText().toString());
            editor.putBoolean("checked", true);
        }else{
            editor.putString("username", "");
            editor.putString("password", "");
            editor.putBoolean("checked", false);
        }
        editor.apply();

    }
    public void getSavedData(){
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox_keeplogin);
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        editText_user.setText(preferences.getString("username", ""));
        editText_pass.setText(preferences.getString("password", ""));
        checkBox.setChecked(preferences.getBoolean("checked", false));
    }
}
