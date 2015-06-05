package com.demo.phucnd.tracnghiemtienganh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.demo.phucnd.tracnghiemtienganh.adapter.ExpandListAdapter;
import com.demo.phucnd.tracnghiemtienganh.appcontroller.AppController;
import com.demo.phucnd.tracnghiemtienganh.model.Child;
import com.demo.phucnd.tracnghiemtienganh.model.Group;
import com.demo.phucnd.tracnghiemtienganh.model.Loaicauhoi;
import com.demo.phucnd.tracnghiemtienganh.utilite.AppCfg;
import com.demo.phucnd.tracnghiemtienganh.utilite.LruBitmapCache;
import com.demo.phucnd.tracnghiemtienganh.utilite.MyPostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {
    private TextView textView_name;
    private ImageView imageView_avatar;
    ArrayList<Group> expListItems = new ArrayList<>();

    ExpandListAdapter expAdapter = null;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpActivity();
    }

    private void setUpActivity(){
        ExpandableListView expandList = (ExpandableListView) findViewById(R.id.expandableListView_info);
        expListItems = SetStandardGroups();
        expAdapter = new ExpandListAdapter(MainActivity.this, expListItems);
        expandList.setAdapter(expAdapter);

        textView_name = (TextView)findViewById(R.id.textView_name);
        textView_name.setText(AppCfg.CURRENT_USER.getName());

        imageView_avatar = (ImageView)findViewById(R.id.imageView_avatar);

        ImageLoader imageLoader = new ImageLoader(AppController.getInstance().getRequestQueue(), new LruBitmapCache());
        String linkAvatar = AppCfg.API_LINK_IMG + AppCfg.CURRENT_USER.getImage().replace(" ", "%20");
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

        expandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String cmd = expListItems.get(i).getItems().get(i1).getName();
                if (cmd.equalsIgnoreCase("Đăng xuất")) {
                    ProgressDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Thông báo").setMessage("Bạn muốn thoát?").setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AppCfg.CURRENT_USER = null;
                            finish();
                        }
                    }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                if (cmd.equalsIgnoreCase("Chỉnh sửa thông tin")) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                if (i == 1) {
                    Intent intent = new Intent(MainActivity.this, TestDoingActivity.class);
                    intent.putExtra("loaicauhoi", AppCfg.LOAICAUHOI.get(i1).getId());
                    startActivity(intent);
                }
                return false;
            }
        });

        getLoaicauhoi();
    }

    public ArrayList<Group> SetStandardGroups() {
        ArrayList<Group> list = new ArrayList<>();
        Group group = new Group();
        group.setName("Cá nhân");
        ArrayList<Child> childrens = new ArrayList<>();
        childrens.add(new Child(AppCfg.CURRENT_USER.getUser(), R.drawable.ic_user));
        childrens.add(new Child(AppCfg.CURRENT_USER.getSdt(), R.drawable.ic_phone));
        childrens.add(new Child(AppCfg.CURRENT_USER.getEmail(), R.drawable.ic_email));
        childrens.add(new Child("Chỉnh sửa thông tin", R.drawable.ic_edit));
        childrens.add(new Child("Đăng xuất", R.drawable.ic_logout));
        group.setItems(childrens);
        list.add(group);

        group = new Group();
        group.setName("Làm bài thi thử");
        ArrayList<Child> childrens2 = new ArrayList<>();
        if(AppCfg.LOAICAUHOI.size() > 0){
            for (Loaicauhoi loaicauhoi : AppCfg.LOAICAUHOI){
                childrens2.add(new Child(loaicauhoi.getTenloai(), R.drawable.ic_question));
            }
        }
        group.setItems(childrens2);
        list.add(group);

        group = new Group();
        group.setName("Hồ sơ kết quả thi");
        ArrayList<Child> childrens3 = new ArrayList<>();
        childrens3.add(new Child("Hoàng Rapper", R.drawable.ic_wifi));
        group.setItems(childrens3);
        list.add(group);

        return list;
    }

    public void getLoaicauhoi(){
        dialog = ProgressDialog.show(this, "Thông báo", "Đang tải...", true, true);
        MyPostRequest request = new MyPostRequest(Request.Method.POST, AppCfg.API_GETLOAICAUHOI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dialog.dismiss();
                try {
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if(success == 1){
                        JSONArray array = jsonObject.getJSONArray("loaicauhoi");
                        for (int i = 0; i < jsonObject.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            Loaicauhoi loaicauhoi = new Loaicauhoi();
                            loaicauhoi.setId(object.getInt("id"));
                            loaicauhoi.setTenloai(object.getString("tenloai"));
                            AppCfg.LOAICAUHOI.add(loaicauhoi);
                        }
                        expListItems.clear();
                        expListItems.addAll(SetStandardGroups());
                        expAdapter.notifyDataSetChanged();
                    }else{
                        AppCfg.showToast(MainActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                volleyError.printStackTrace();
                AppCfg.showToast(MainActivity.this, "Network error!");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //AppCfg.CURRENT_USER = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
