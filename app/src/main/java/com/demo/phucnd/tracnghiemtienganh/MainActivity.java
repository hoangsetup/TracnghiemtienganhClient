package com.demo.phucnd.tracnghiemtienganh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.demo.phucnd.tracnghiemtienganh.adapter.ExpandListAdapter;
import com.demo.phucnd.tracnghiemtienganh.model.Child;
import com.demo.phucnd.tracnghiemtienganh.model.Group;
import com.demo.phucnd.tracnghiemtienganh.utilite.AppCfg;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private TextView textView_name;
    ArrayList<Group> expListItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpActivity();
    }

    private void setUpActivity(){
        ExpandableListView expandList = (ExpandableListView) findViewById(R.id.expandableListView_info);
        expListItems = SetStandardGroups();
        ExpandListAdapter expAdapter = new ExpandListAdapter(MainActivity.this, expListItems);
        expandList.setAdapter(expAdapter);

        textView_name = (TextView)findViewById(R.id.textView_name);
        textView_name.setText(AppCfg.CURRENT_USER.getName());

        expandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if(expListItems.get(i).getItems().get(i1).getName().equalsIgnoreCase("Đăng xuất")){
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
                return false;
            }
        });
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
        childrens2.add(new Child("http://hiworld.com.vn", R.drawable.ic_internet));
        childrens2.add(new Child("01676 322 963", R.drawable.ic_phone));
        childrens2.add(new Child("hoang.dv@outlook.com", R.drawable.ic_email));
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
}
