package com.zhangCai.todayeat.util;

import android.content.Context;

/**
 * 像素单位之间的转换
 * Created by zhangmiao on 2018/11/26.
 */

public class DisplayUtils {

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dp      需要转换的数值
     * @return
     */
    public static final int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param context 上下文
     * @param px      需要转换的数值
     * @return
     */
    public static final int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

}
