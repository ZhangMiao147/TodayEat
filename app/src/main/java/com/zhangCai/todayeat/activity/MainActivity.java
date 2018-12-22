package com.zhangCai.todayeat.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangCai.todayeat.R;
import com.zhangCai.todayeat.adapter.MenuAdapter;
import com.zhangCai.todayeat.util.DefaultValueUtil;
import com.zhangCai.todayeat.util.SPUtils;
import com.zhangCai.todayeat.view.AddDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 主界面
 * <p>
 * 摇一摇 https://blog.csdn.net/u013144863/article/details/52958674
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String DATA_KEY = "menu";

    private ImageView iv_welcome; //欢迎界面
    private ImageView iv_add; //添加菜品按钮
    private LinearLayout ll_none; //还没有菜品时的界面
    private AddDialog mDialog; //添加的dialog
    private GridView gv_menu; //菜单
    private MenuAdapter mMenuAdapter; //菜单的adapter
    private TextView tv_shakeTips; //摇一摇提示
    private RelativeLayout rl_addLayout; //添加布局
    private RelativeLayout rl_chooseLayout; //选择布局
    private TextView tv_chooseFinish; //全选
    private TextView tv_chooseCount; //选择的个数
    private TextView tv_chooseAll; //全选
    private TextView tv_delete; //删除
    private ImageView iv_result; //结果图片
    private FrameLayout fl_resultLayout; //结果布局
    private TextView tv_result; //结果文字
    private TextView tv_sure; //确定

    private SensorManager mSensorManager; //
    private Sensor mAccelerometerSensor;
    private boolean isShake = false;
    private ShakeHandle mShakeHandle;

    SoundPool mSoundPool;
    Vibrator mVibrator; //震动服务

    boolean isCanShake; //是否可以摇一摇

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
//        setCustomDensity(this,getApplication());
        setContentView(R.layout.activity_main);
        initView();
        hideActionBar();
        iv_welcome.setVisibility(View.VISIBLE);
        iv_welcome.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_welcome.setVisibility(View.GONE);
            }
        },2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mShakeHandle = new ShakeHandle(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        mSoundPool.load(this, R.raw.shake, 1);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onPause() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        super.onPause();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isCanShake) {
            return;
        }

        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            //获取三个方向值
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            if ((Math.abs(x) > 17 || Math.abs(y) > 17 || Math.abs(z) > 17) && !isShake) {
                isShake = true;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            mShakeHandle.obtainMessage(ShakeHandle.START_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            mShakeHandle.obtainMessage(ShakeHandle.AGAIN_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            mShakeHandle.obtainMessage(ShakeHandle.END_SHAKE).sendToTarget();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 获取界面组件
     */
    private void initView() {
        iv_welcome = (ImageView)findViewById(R.id.activity_main_welcome_iv);
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
        iv_result = (ImageView) findViewById(R.id.activity_main_result_iv);
        fl_resultLayout = (FrameLayout) findViewById(R.id.activity_main_result_layout_fl);
        tv_result = (TextView) findViewById(R.id.activity_main_result_tv);
        tv_sure = (TextView) findViewById(R.id.activity_main_sure_tv);

        tv_chooseFinish.setOnClickListener(this);
        tv_chooseAll.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        tv_shakeTips.setOnClickListener(this);
        iv_result.setOnClickListener(this);
        tv_sure.setOnClickListener(this);

        if (mMenuAdapter.getCount() == 0) {
            isCanShake = false;
            ll_none.setVisibility(View.VISIBLE);
            gv_menu.setVisibility(View.GONE);
            tv_shakeTips.setVisibility(View.GONE);
        } else {
            isCanShake = true;
            ll_none.setVisibility(View.GONE);
            gv_menu.setVisibility(View.VISIBLE);
            tv_shakeTips.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏ActionBar
     */
    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_add_iv:
                //点击添加条目按钮
                showAddDialog();
                break;
            case R.id.activity_main_choose_finish_tv:
                //点击完成按钮
                rl_chooseLayout.setVisibility(View.GONE);
                rl_addLayout.setVisibility(View.VISIBLE);
                tv_delete.setVisibility(View.GONE);
                tv_shakeTips.setVisibility(View.VISIBLE);
                if (mMenuAdapter != null) {
                    mMenuAdapter.cancelDelete();
                }
                break;
            case R.id.activity_main_choose_all_tv:
                //点击全选按钮
                if (mMenuAdapter != null) {
                    mMenuAdapter.chooseAll();
                }
                break;

//            case R.id.activity_main_shake_tips_tv:
//                shake();
//                break;
            case R.id.activity_main_delete_tv:
                //点击删除
                delete();
                break;
            case R.id.activity_main_result_iv:
                //显示选择的答案
                int result = (int) (Math.random() * (mMenuAdapter.getCount() - 1) + 0.5);
                Log.d(TAG, "result:" + result);
                String name = (String) mMenuAdapter.getItem(result);
                tv_result.setVisibility(View.VISIBLE);
                tv_result.setText(name);
                tv_result.setBackgroundResource(DefaultValueUtil.MENU_COLORS[result % DefaultValueUtil.MENU_COLORS.length]);
                tv_sure.setVisibility(View.VISIBLE);
                AnimatorSet inAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.rotate_in_anim);
                AnimatorSet outAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.rotate_out_anim);
                int distance = 16000;
                float scale = getResources().getDisplayMetrics().density * distance;
                iv_result.setCameraDistance(scale);
                tv_result.setCameraDistance(scale);
                outAnimator.setTarget(iv_result);
                inAnimator.setTarget(tv_result);
                outAnimator.start();
                inAnimator.start();
                outAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        iv_result.setVisibility(View.GONE);
                        iv_result.setAlpha(1.0f);
                        iv_result.setRotationY(0.0f);
                    }
                });
                break;
            case R.id.activity_main_sure_tv:
                //确定答案
                isCanShake = true;
                iv_add.setVisibility(View.VISIBLE);
                fl_resultLayout.setVisibility(View.GONE);
                tv_sure.setVisibility(View.GONE);
                tv_shakeTips.setVisibility(View.VISIBLE);
                gv_menu.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    //连续删除有问题，需查看
    private void delete() {
        if (mMenuAdapter != null) {
            List<Integer> deleteList = mMenuAdapter.delete();
            if (deleteList != null && deleteList.size() > 0 && mMenuAdapter.getCount() > 0) {
                animateReorder(deleteList, mMenuAdapter.getCount());
            }
        }
        saveData();
    }

    /**
     * 删除item后，其他的item需要自动补齐
     *
     * @param deleteList
     * @param itemCount
     */
    private void animateReorder(List<Integer> deleteList, int itemCount) {
        if (deleteList == null || deleteList.size() < 1 || itemCount < 1) {
            return;
        }

        List list = new ArrayList();
        Collections.sort(deleteList);
        int[] beforeDelete = new int[itemCount + deleteList.size()];

        for (int i = 0; i < deleteList.size(); i++) {
            beforeDelete[deleteList.get(i)] = -1;
        }
        for (int i = 0; i < itemCount; i++) {
            if (beforeDelete[i] == -1 || beforeDelete[i] != i) {
                for (int j = i; j < beforeDelete.length; j++) {
                    if (beforeDelete[j] == 0) {
                        beforeDelete[j] = i;
                        break;
                    }
                }
            } else if (beforeDelete[i] != -1) {
                beforeDelete[i] = i;
            }
        }
        Log.d(TAG, "beforeDelete：" + Arrays.toString(beforeDelete));

        for (int pos = deleteList.get(0); pos < itemCount; pos++) {
            int beforeDeleteCount = 0;
            for (int i = 0; i < beforeDelete.length; i++) {
                if (beforeDelete[i] != pos) {
                    if (beforeDelete[i] == -1) {
                        beforeDeleteCount++;
                    }
                } else {
                    break;
                }
            }
            Log.d(TAG, "pos:" + pos + ",beforeDeleteCount:" + beforeDeleteCount);
            View view = gv_menu.getChildAt(pos - gv_menu.getFirstVisiblePosition());
            if ((pos + beforeDeleteCount) % gv_menu.getNumColumns() == 0) {
                int removeHeightCount = beforeDeleteCount / gv_menu.getNumColumns();
                if (beforeDeleteCount % gv_menu.getNumColumns() != 0) {
                    removeHeightCount += 1;
                }
                list.add(createAnimator(view, -view.getWidth() * (beforeDeleteCount % gv_menu.getNumColumns()), 0, view.getHeight() * removeHeightCount, 0));
            } else {
                list.add(createAnimator(view, view.getWidth() * (beforeDeleteCount % gv_menu.getNumColumns()), 0, view.getHeight() * (beforeDeleteCount / gv_menu.getNumColumns()), 0));
            }
        }
        AnimatorSet set = new AnimatorSet();
        set.playTogether(list);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(300);
        set.start();
    }

    /**
     * 平移动画
     *
     * @param view   需要移动的view
     * @param startX 开始的X坐标
     * @param endX   结束的X坐标
     * @param startY 开始的Y坐标
     * @param endY   结束的Y坐标
     * @return
     */
    private AnimatorSet createAnimator(View view, float startX,
                                       float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
                startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
                startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    /**
     * 摇一摇的反应
     */
    public void shake() {
        isCanShake = false;
        gv_menu.setVisibility(View.GONE);
        tv_shakeTips.setVisibility(View.GONE);
        fl_resultLayout.setVisibility(View.VISIBLE);
        tv_result.setVisibility(View.GONE);
        iv_result.setVisibility(View.VISIBLE);
        iv_add.setVisibility(View.GONE);
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
                        isCanShake = true;
                        mMenuAdapter.addItem(input);
                        saveData();
                        mDialog.dismiss();
                    } else {
                        if (mMenuAdapter != null) {
                            List<String> dataList = mMenuAdapter.getData();
                            if (dataList != null && dataList.contains(input)) {
                                Toast.makeText(MainActivity.this, "列表中已经有这个了哦！", Toast.LENGTH_SHORT).show();
                            } else {
                                isCanShake = true;
                                mMenuAdapter.addItem(input);
                                mDialog.dismiss();
                                saveData();
                            }
                        } else {
                            isCanShake = true;
                            mDialog.dismiss();
                        }
                    }
                }

                @Override
                public void showPrompt(String message) {
                    isCanShake = true;
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
        isCanShake = false;
    }

    /**
     * 保存数据
     */
    private void saveData() {
        if (mMenuAdapter != null) {
            List<String> data = mMenuAdapter.getData();
            Log.d(TAG, "onDestroy data:" + data);
            SPUtils.saveStringList(this, DATA_KEY, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    private static class ShakeHandle extends Handler {
        private WeakReference<MainActivity> mReference;
        private MainActivity mActivity;

        public static final int START_SHAKE = 1;
        public static final int AGAIN_SHAKE = 2;
        public static final int END_SHAKE = 3;

        public ShakeHandle(MainActivity activity) {
            mReference = new WeakReference<MainActivity>(activity);
            if (mReference != null) {
                mActivity = mReference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage msg:" + msg);
            switch (msg.what) {
                case START_SHAKE:
                    mActivity.mVibrator.vibrate(300);
                    mActivity.mSoundPool.play(R.raw.shake, 1, 1, 0, 0, 1);
                    break;
                case AGAIN_SHAKE:
                    mActivity.mVibrator.vibrate(300);
                    break;
                case END_SHAKE:
                    mActivity.isShake = false;
                    mActivity.shake();
                    break;
                default:
                    break;
            }
        }
    }


    private static float sNoncompatDensity;
    private static float sNoncompatScaleDensity;

    /**
     * 适配UI，没有找到合适的机器去测试
     *
     * @param activity
     * @param application
     */
    private void setCustomDensity(@NonNull Activity activity, @NonNull final Application application) {
        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

        if (sNoncompatDensity == 0) {
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaleDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        Log.d(TAG, "sNoncompatDensity:" + sNoncompatDensity + ",sNoncompatScaleDensity:" + sNoncompatScaleDensity);
        final float targetDensity = appDisplayMetrics.widthPixels / 720;
        final float targetScaledDensity = targetDensity * (sNoncompatScaleDensity / sNoncompatDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);
        Log.d(TAG, "targetDensity:" + targetDensity + ",targetScaledDensity:" + targetScaledDensity + ",targetDensityDpi:" + targetDensityDpi);
        Log.d(TAG, "appDisplayMetrics.density:" + appDisplayMetrics.density + ",appDisplayMetrics.scaledDensity:" + appDisplayMetrics.scaledDensity + ",appDisplayMetrics.densityDpi:" + appDisplayMetrics.densityDpi);
        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }


}
