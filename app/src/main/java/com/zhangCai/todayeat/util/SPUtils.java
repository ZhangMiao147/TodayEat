package com.zhangCai.todayeat.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SharedPreferences存储或获取数据
 * Author: zhangmiao
 * Date: 2018/10/23
 */
public class SPUtils {

    private static SharedPreferences sSp;
    private static final String SP_NAME = "eat";

    /**
     * 保存List<String>菜单数据到SharedPreferences中
     *
     * @param context
     * @param key
     * @param stringList
     */
    public static void saveStringList(Context context, String key, List<String> stringList) {
        if (stringList == null) {
            return;
        }
        if (sSp == null) {
            sSp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sSp.edit();
        Set<String> data = new HashSet<>(stringList);
        editor.putStringSet(key, data);
        editor.commit();
    }

    /**
     * 从SharedPreferences中获取List<String>数据
     *
     * @param context
     * @param key
     * @return
     */
    public static List<String> getStringList(Context context, String key) {
        if (sSp == null) {
            sSp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        Set<String> datas = sSp.getStringSet(key, null);
        if (datas == null) {
            return null;
        } else {
            return new ArrayList<>(datas);
        }
    }
}
