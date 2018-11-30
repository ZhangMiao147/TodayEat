package com.zhangCai.todayeat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangCai.todayeat.R;
import com.zhangCai.todayeat.util.DefaultValueUtil;

import java.util.ArrayList;
import java.util.Collections;
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
    private boolean mShowChoose = false; //显示删除选择
    private List<String> mItemChooseList; //被选择的链表

    public MenuAdapter(Context context) {
        mContext = context;
        mItemChooseList = new ArrayList<>();
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
            menuHolder = new MenuHolder(convertView);
            convertView.setTag(menuHolder);
        } else {
            menuHolder = (MenuHolder) convertView.getTag();
        }
        menuHolder.tv_name.setText(mData.get(position));
        menuHolder.tv_name.setBackgroundResource(DefaultValueUtil.MENU_COLORS[position % DefaultValueUtil.MENU_COLORS.length]);
        menuHolder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "name:" + mData.get(position));
            }
        });
        menuHolder.tv_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mShowChoose) {
                    mShowChoose = true;
                    mItemChooseList.add(position + "");
                    if (mOnChooseListener != null) {
                        mOnChooseListener.onChooseSize(mItemChooseList.size());
                    }
                    Log.d(TAG, "onLongClick");
                    notifyDataSetChanged();
                }
                return true;
            }
        });
        Log.d(TAG, "position:" + position + ",mShowChoose:" + mShowChoose);
        if (mShowChoose) {
            menuHolder.iv_choose.setVisibility(View.VISIBLE);
        } else {
            menuHolder.iv_choose.setVisibility(View.GONE);
        }
        if (mItemChooseList.contains(position + "")) {
            menuHolder.iv_choose.setBackgroundResource(R.mipmap.pick);
        } else {
            menuHolder.iv_choose.setBackgroundResource(R.mipmap.choose);
        }
        menuHolder.iv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemChooseList.contains(position + "")) {
                    v.setBackgroundResource(R.mipmap.choose);
                    mItemChooseList.remove(position + "");
                } else {
                    v.setBackgroundResource(R.mipmap.pick);
                    mItemChooseList.add(position + "");
                }
                if (mOnChooseListener != null) {
                    mOnChooseListener.onChooseSize(mItemChooseList.size());
                }
            }
        });
        return convertView;
    }

    /**
     * 全选
     */
    public void chooseAll() {
        for (int i = 0; i < mData.size(); i++) {
            if (!mItemChooseList.contains(i + "")) {
                mItemChooseList.add(i + "");
            }
        }
        if (mOnChooseListener != null) {
            mOnChooseListener.onChooseSize(mItemChooseList.size());
        }
        notifyDataSetChanged();
    }

    /**
     * 删除
     */
    public List<Integer> delete() {
        List<Integer> deleteList = new ArrayList<>();
        Collections.sort(mItemChooseList);
        if (mItemChooseList != null && mItemChooseList.size() > 0) {
            for (int i = mItemChooseList.size() - 1; i >= 0; i--) {
                int index = Integer.parseInt(mItemChooseList.get(i));
                deleteList.add(index);
                mData.remove(index);
            }
            mItemChooseList.clear();
            notifyDataSetChanged();
        }
        return deleteList;
    }

    /**
     * 取消删除
     */
    public void cancelDelete() {
        mShowChoose = false;
        notifyDataSetChanged();
    }

    private class MenuHolder {
        private TextView tv_name;
        private ImageView iv_choose;

        public MenuHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.grid_menu_name_tv);
            iv_choose = (ImageView) view.findViewById(R.id.grid_menu_choose_iv);
        }
    }

    private OnChooseListener mOnChooseListener;

    public void setOnChooseListener(OnChooseListener onChooseListener) {
        this.mOnChooseListener = onChooseListener;
    }

    public interface OnChooseListener {
        void onChooseSize(int size);
    }


}
