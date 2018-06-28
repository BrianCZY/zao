package com.hzu.zao.view.numberPicker;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.hzu.zao.R;

/**
 * 选择数字
 * <p/>
 * Created by Nearby Yang on 2016-04-25.
 */
public class NumberPickerDialog extends Dialog implements View.OnClickListener {

    private View view;
    private Context context;
    private NumberPicker numberPicker;
    private TextView comfirBtn;
    private TextView cancelBtn;

    private int finalValue = 0;

    private OnResultListener onResultListener;


    public NumberPickerDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_number_picker, null);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        findView();
        setUpEvent();
    }


    private void findView() {
        numberPicker = (NumberPicker) view.findViewById(R.id.nb_picker);
        comfirBtn = (TextView) view.findViewById(R.id.tx_comfir);
        cancelBtn = (TextView) view.findViewById(R.id.tx_cancel);

    }

    private void setUpEvent() {
        numberPicker.setMaxValue(30);
        numberPicker.setMinValue(1);
        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);

        comfirBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker numberPicker, int old, int newVal) {
                finalValue = newVal;
            }
        });


    }


    /**
     * 点击事件监听
     *
     * @param onResultListener
     */
    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        onResultListener.onResult(view.getId() == R.id.tx_comfir,finalValue);

    }


    /**
     * 选择监听
     */
    public interface OnResultListener {

        void onResult(boolean isSelected,int finalValue);
    }

}
