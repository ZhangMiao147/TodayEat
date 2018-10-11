package com.zhangCai.todayeat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhangCai.todayeat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单列表的adapter
 * Author: zhangmiao
 * Date: 2018/10/8
 */
public class MenuAdapter extends BaseAdapter {

    private static final String TAG = MenuAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> mData;
    private int[] mColors;

    public MenuAdapter(Context context) {
        mContext = context;
        mColors = new int[]{R.color.menuBlue, R.color.menuYellow, R.color.menuRed, R.color.menuGreen};
    }

    public void addItem(String name) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(name);
        notifyDataSetChanged();
    }

    public void addItemList(List<String> datas) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.addAll(datas);
    }

    public List<String> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MenuHolder menuHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.grid_menu_item, null);
            menuHolder = new MenuHolder((TextView) convertView.findViewById(R.id.grid_menu_name_tv));
            convertView.setTag(menuHolder);
        } else {
            menuHolder = (MenuHolder) convertView.getTag();
        }
        menuHolder.tv_name.setText(mData.get(position));
        menuHolder.tv_name.setBackgroundResource(mColors[position % mColors.length]);
        menuHolder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "name:" + mData.get(position));
            }
        });
        menuHolder.tv_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });
        return convertView;
    }

    private class MenuHolder {
        private TextView tv_name;

        public MenuHolder(TextView textView) {
            tv_name = textView;
        }

    }
}
