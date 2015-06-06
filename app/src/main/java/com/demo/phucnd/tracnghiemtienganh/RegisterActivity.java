package com.demo.phucnd.tracnghiemtienganh;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.demo.phucnd.tracnghiemtienganh.appcontroller.AppController;
import com.demo.phucnd.tracnghiemtienganh.model.User;
import com.demo.phucnd.tracnghiemtienganh.utilite.AppCfg;
import com.demo.phucnd.tracnghiemtienganh.utilite.LruBitmapCache;
import com.demo.phucnd.tracnghiemtienganh.utilite.MyPostRequest;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Hoangdv on 9:12 PM : 0003, Jun, 03, 2015.
 */
public class RegisterActivity extends Activity {
    private EditText editText_name, editText_user, editText_pass, editText_email, editText_sdt, editText_ngaysinh;
    private Button button_reg;
    private ImageView imageView_avatar;

    private DatePickerDialog datePickerDialog;
    private static Calendar calendar;

    private String pathImage = "";
    private ProgressDialog dialog;

    static {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1994);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editText_email = (EditText) findViewById(R.id.editText_email);
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_ngaysinh = (EditText) findViewById(R.id.editText_ngaysinh);
        editText_pass = (EditText) findViewById(R.id.editText_password);
        editText_sdt = (EditText) findViewById(R.id.editText_sdt);
        editText_user = (EditText) findViewById(R.id.editText_user);

        button_reg = (Button) findViewById(R.id.button_register);
        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegisterUser();
            }
        });

        imageView_avatar = (ImageView) findViewById(R.id.imageView_avatar);
        imageView_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This always works
                Intent i = new Intent(RegisterActivity.this, FilePickerActivity.class);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                startActivityForResult(i, 222);
            }
        });

        initDatePicker();

        if(AppCfg.CURRENT_USER != null){
            initUpdateInfo();
        }
    }

    public void initUpdateInfo(){
        User user = AppCfg.CURRENT_USER;
        ImageLoader imageLoader = new ImageLoader(AppController.getInstance().getRequestQueue(), new LruBitmapCache());
        String linkAvatar = AppCfg.API_LINK_IMG + user.getImage().replace(" ", "%20");
        imageLoader.get(linkAvatar, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                Bitmap bitmap = imageContainer.getBitmap();
                if (bitmap != null)
                    imageView_avatar.setImageBitmap(imageContainer.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        editText_name.setText(user.getName());
        editText_ngaysinh.setText(user.getNgaysinh());
        editText_sdt.setText(user.getSdt());
        editText_email.setText(user.getEmail());
        editText_pass.setText("");
        editText_user.setText(user.getUser());
        pathImage = user.getImage();

    }

    private void doRegisterUser() {

        String name = editText_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            editText_name.setError("*");
            return;
        }
        String user = editText_user.getText().toString();
        if (TextUtils.isEmpty(user)) {
            editText_user.setError("*");
            return;
        }
        String password = editText_pass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editText_pass.setError("*");
            return;
        }
        String email = editText_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editText_email.setError("*");
            return;
        } else if (!AppCfg.isEmailValid(email)) {
            editText_email.setError("Email is required!");
            return;
        }
        String sdt = editText_sdt.getText().toString();
        if (TextUtils.isEmpty(sdt)) {
            editText_sdt.setError("*");
            return;
        } else if (!AppCfg.isPhoneNumberValid(sdt)) {
            editText_sdt.setError("Number phone is required!");
            return;
        }

        String ngaysinh = editText_ngaysinh.getText().toString();
        if (TextUtils.isEmpty(ngaysinh)) {
            editText_ngaysinh.setError("*");
            return;
        }

        registerUser(name, user, password, email, ngaysinh, sdt);
    }

    private void registerUser(String name, final String user, final String password, String email, String ngaysinh, String sdt) {
        dialog = ProgressDialog.show(RegisterActivity.this, "Thông báo", "Đang thực hiện...", true, false);
        Map<String, String> params = new HashMap<>();
        params.put("username", user);
        params.put("password", password);
        params.put("name", name);
        params.put("email", email);
        params.put("ngaysinh", ngaysinh);
        params.put("sdt", sdt);
        try{
            File file = new File(pathImage);
            params.put("image", file.getName());
        }catch (Exception ex){
            params.put("image", "");
        }

        // Update
        if(AppCfg.CURRENT_USER != null){
            params.put("id", AppCfg.CURRENT_USER.getId()+"");
        }

        MyPostRequest request = new MyPostRequest(Request.Method.POST, AppCfg.API_REGISTER, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //dialog.dismiss();
                try {
                    int success = jsonObject.getInt("success");
                    String msg = jsonObject.getString("message");
                    if (success == 1) {
                        AppCfg.showToast(RegisterActivity.this, msg);
                        Thread.sleep(500);
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("username", user);
                        bundle.putString("password", password);
                        new uploadImageTask().execute();
                        intent.putExtra("data", bundle);
                        setResult(111, intent);
                        finish();
                    } else {
                        AppCfg.showToast(RegisterActivity.this, msg);
                        dialog.dismiss();
                    }
                } catch (JSONException | InterruptedException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                volleyError.printStackTrace();
                AppCfg.showToast(RegisterActivity.this, "Network error!");
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

    private void initDatePicker() {
        editText_ngaysinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        editText_ngaysinh.setText(dateFormatter.format(calendar.getTime()));
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                editText_ngaysinh.setText(dateFormatter.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
    }

    class uploadImageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg) {
            AppCfg.uploadFile(pathImage);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            reLogin(editText_user.getText().toString(), editText_pass.getText().toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 222 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {

                Bitmap bitmap = null;
                File f = new File(uri.getPath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                options.inSampleSize = AppCfg.calculateInSampleSize(options, 100, 100);
                options.inJustDecodeBounds = false;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    imageView_avatar.setImageBitmap(bitmap);
                    pathImage = uri.getPath();
                } else {
                    AppCfg.showToast(this, "Can not cast file to image!");
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    private void reLogin(String user, final String pass) {
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
                        AppCfg.CURRENT_USER = user;
                        AppCfg.PASSWORD = pass;
                        finish();
                    }else {
                        AppCfg.showToast(RegisterActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                AppCfg.showToast(RegisterActivity.this, "Network error!");
                finish();
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
}
