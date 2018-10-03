package com.zhangCai.todayeat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView iv_add; //添加菜品按钮
    private LinearLayout ll_none; //还没有菜品时的界面


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
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

    }

}
