package com.zhangCai.todayeat.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.zhangCai.todayeat.R;
import com.zhangCai.todayeat.util.DisplayUtils;
import com.zhangCai.todayeat.util.SPUtils;

/**
 * Author: zhangmiao
 * Date: 2018/10/8
 */
public class AddDialog extends DialogFragment {

    private static final String TAG = AddDialog.class.getSimpleName();

    private static final int EDIT_MAX = 10;

    EditText et_name;
    TextView tv_cancel;
    TextView tv_sure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        View view = inflater.inflate(R.layout.dialog_add, ((ViewGroup) window.findViewById(android.R.id.content)), false);//需要用android.R.id.content这个view
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(DisplayUtils.dp2px(getContext(), 296), DisplayUtils.dp2px(getContext(), 170));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_name = view.findViewById(R.id.dialog_add_name_et);
        tv_cancel = view.findViewById(R.id.dialog_add_cancel_tv);
        tv_sure = view.findViewById(R.id.dialog_add_sure_tv);
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
                    if (mInputResultListener != null) {
                        mInputResultListener.showPrompt("最多输入10个字哦");
                    }
                    et_name.setText(editable.toString().substring(0, EDIT_MAX));
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_name != null) {
                    String name = et_name.getText().toString();
                    name = name.replace(" ", "");
                    if (!TextUtils.isEmpty(name)) {
                        if (mInputResultListener != null) {
                            mInputResultListener.result(name);
                        } else {
                            dismiss();
                        }
                        et_name.setText("");
                    } else {
                        if (mInputResultListener != null) {
                            mInputResultListener.showPrompt("没有输入菜品名称呀！");
                        }
                    }
                }
            }
        });
    }

    private InputResultListener mInputResultListener;

    public void setInputResultListener(InputResultListener inputResultListener) {
        this.mInputResultListener = inputResultListener;
    }

    public interface InputResultListener {
        void result(String input);

        void showPrompt(String message);
    }
}
