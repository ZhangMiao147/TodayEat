package com.zhangCai.todayeat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangCai.todayeat.R;
import com.zhangCai.todayeat.adapter.MenuAdapter;
import com.zhangCai.todayeat.view.AddDialog;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView iv_add; //添加菜品按钮
    private LinearLayout ll_none; //还没有菜品时的界面
    private AddDialog mDialog;
    private GridView gv_menu;
    private MenuAdapter mMenuAdapter;
    private TextView tv_shakeTips; //摇一摇提醒


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
        gv_menu = (GridView) findViewById(R.id.activity_main_menu_gl);
        tv_shakeTips = (TextView) findViewById(R.id.activity_main_shake_tips_tv);
        iv_add.setOnClickListener(this);
        mMenuAdapter = new MenuAdapter(getApplicationContext());
        gv_menu.setAdapter(mMenuAdapter);
        if (mMenuAdapter.getCount() == 0) {
            ll_none.setVisibility(View.VISIBLE);
            gv_menu.setVisibility(View.GONE);
            tv_shakeTips.setVisibility(View.GONE);
        } else {
            ll_none.setVisibility(View.GONE);
            gv_menu.setVisibility(View.VISIBLE);
            tv_shakeTips.setVisibility(View.VISIBLE);
        }
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
            mDialog = new AddDialog();
            mDialog.setInputResultListener(new AddDialog.InputResultListener() {
                @Override
                public void result(String input) {
                    Log.d(TAG, "result input:" + input);
                    if (mMenuAdapter.getCount() == 0) {
                        if (ll_none.getVisibility() != View.GONE) {
                            ll_none.setVisibility(View.GONE);
                        }
                        if (gv_menu.getVisibility() != View.VISIBLE) {
                            gv_menu.setVisibility(View.VISIBLE);
                        }
                        if (tv_shakeTips.getVisibility() != View.VISIBLE) {
                            tv_shakeTips.setVisibility(View.VISIBLE);
                        }
                    }
                    mMenuAdapter.addItem(input);
                }

                @Override
                public void showPrompt(String message) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (mDialog.isVisible()) {
                mDialog.dismiss();
            }
        }
        if (mDialog.getDialog() == null || !mDialog.getDialog().isShowing()) {
            mDialog.show(getSupportFragmentManager(), AddDialog.class.getSimpleName());
        }
    }

}
