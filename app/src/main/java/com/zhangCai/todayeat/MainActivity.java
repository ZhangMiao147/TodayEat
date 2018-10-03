package com.zhangCai.todayeat;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int EDIT_MAX = 10;

    private ImageView iv_add; //添加菜品按钮
    private LinearLayout ll_none; //还没有菜品时的界面
    private Dialog mDialog;
    private List<String> mNames; //菜品名称


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mNames = new ArrayList<>();
    }

    /**
     * 获取界面组件
     */
    private void initView() {
        iv_add = (ImageView) findViewById(R.id.activity_main_add_iv);
        ll_none = (LinearLayout) findViewById(R.id.activity_main_none_layout_ll);
        iv_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_add_iv:
                showAddDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 显示添加菜品对话框
     */
    private void showAddDialog() {
        if (mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.dialog_add, null);
            final EditText et_name = v.findViewById(R.id.dialog_add_name_et);
            TextView tv_cancel = v.findViewById(R.id.dialog_add_cancel_tv);
            TextView tv_sure = v.findViewById(R.id.dialog_add_sure_tv);
            mDialog = builder.create();
            mDialog.show();
            mDialog.getWindow().setContentView(v);
            et_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > EDIT_MAX) {
                        Toast.makeText(MainActivity.this, "最多输入10个字哦", Toast.LENGTH_SHORT).show();
                        et_name.setText(editable.toString().substring(0, EDIT_MAX));
                    }
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                }
            });
            tv_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (et_name != null) {
                        String name = et_name.getText().toString();
                        name = name.replace(" ", "");
                        if (!TextUtils.isEmpty(name)) {
                            mNames.add(name);
                        } else {
                            Toast.makeText(MainActivity.this, "没有输入菜品名称呀！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

    }

}
