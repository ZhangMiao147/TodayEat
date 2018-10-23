package com.zhangCai.todayeat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangCai.todayeat.R;
import com.zhangCai.todayeat.adapter.MenuAdapter;
import com.zhangCai.todayeat.util.SPUtils;
import com.zhangCai.todayeat.view.AddDialog;

import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String DATA_KEY = "menu";

    private ImageView iv_add; //添加菜品按钮
    private LinearLayout ll_none; //还没有菜品时的界面
    private AddDialog mDialog;
    private GridView gv_menu;
    private MenuAdapter mMenuAdapter;
    private TextView tv_shakeTips; //摇一摇提示
    private RelativeLayout rl_addLayout; //添加布局
    private RelativeLayout rl_chooseLayout; //选择布局
    private TextView tv_chooseFinish; //全选
    private TextView tv_chooseCount; //选择的个数
    private TextView tv_chooseAll; //全选
    private TextView tv_delete; //删除

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
        mMenuAdapter.setOnChooseListener(new MenuAdapter.OnChooseListener() {
            @Override
            public void onChooseSize(int size) {
                if (rl_chooseLayout.getVisibility() != View.VISIBLE) {
                    rl_chooseLayout.setVisibility(View.VISIBLE);
                    rl_addLayout.setVisibility(View.GONE);
                    tv_shakeTips.setVisibility(View.GONE);
                    tv_delete.setVisibility(View.VISIBLE);
                }
                tv_chooseCount.setText("已选择" + size + "项");
            }
        });
        List<String> meuns = SPUtils.getStringList(this, DATA_KEY);
        if (meuns != null) {
            mMenuAdapter.addItemList(meuns);
        }
        gv_menu.setAdapter(mMenuAdapter);
        rl_addLayout = (RelativeLayout) findViewById(R.id.activity_main_add_layout_rl);
        rl_chooseLayout = (RelativeLayout) findViewById(R.id.activity_main_choose_layout_rl);
        tv_chooseFinish = (TextView) findViewById(R.id.activity_main_choose_finish_tv);
        tv_chooseCount = (TextView) findViewById(R.id.activity_main_choose_count_tv);
        tv_chooseAll = (TextView) findViewById(R.id.activity_main_choose_all_tv);
        tv_delete = (TextView) findViewById(R.id.activity_main_delete_tv);
        tv_chooseFinish.setOnClickListener(this);
        tv_chooseAll.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
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
            case R.id.activity_main_choose_finish_tv:
                rl_chooseLayout.setVisibility(View.GONE);
                rl_addLayout.setVisibility(View.VISIBLE);
                tv_delete.setVisibility(View.GONE);
                tv_shakeTips.setVisibility(View.VISIBLE);
                if (mMenuAdapter != null) {
                    mMenuAdapter.cancelDelete();
                }
                break;
            case R.id.activity_main_choose_all_tv:
                if (mMenuAdapter != null) {
                    mMenuAdapter.chooseAll();
                }
                break;
            case R.id.activity_main_delete_tv:
                if (mMenuAdapter != null) {
                    mMenuAdapter.delete();
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMenuAdapter != null) {
            List<String> data = mMenuAdapter.getData();
            SPUtils.saveStringList(this, DATA_KEY, data);
        }
    }
}
