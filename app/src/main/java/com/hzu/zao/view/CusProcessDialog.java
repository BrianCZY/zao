package com.hzu.zao.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.hzu.zao.R;

/**
 * 通用的progressDialog
 *
 * Created by Nearby Yang on 2016-04-23.
 */
public class CusProcessDialog {

    /**
     * 创建一个progress Dialog
     * @param context
     * @param message
     * @return
     */
    public static ProgressDialog commenProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(TextUtils.isEmpty(message) ?
                context.getResources().getString(R.string.xlistview_header_hint_loading) :
                message);

        return progressDialog;
    }

    /**
     * 默认是正在加载的文字
     * @param context
     * @return
     */

    public static ProgressDialog commenProgressDialog(Context context) {
        return commenProgressDialog(context,"");
    }
}
