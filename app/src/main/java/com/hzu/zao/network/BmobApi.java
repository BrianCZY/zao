package com.hzu.zao.network;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzu.zao.R;
import com.hzu.zao.config.Contants;
import com.hzu.zao.interfaces.AsyncListener;
import com.hzu.zao.interfaces.GoToUploadImages;
import com.hzu.zao.model.BaseModel;
import com.hzu.zao.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * bmob
 * Created by Nearby Yang on 2016-04-21.
 */
public class BmobApi {

    public static final String FUNCTION_SET_THUMBNAIL = "SetThumbnail";
    public static final String FUNCTION_UPDATE_USERLOGIN = "UpdateUserLogin";

    private static Gson gson = new Gson();

    /**
     * 云端代码
     *
     * @param ctx
     * @param params        JSONObject 请求参数
     * @param funcationName 方法名
     */
    public static void AsyncFunction(final Context ctx, JSONObject params, String funcationName,
                                     final Class clazz, final AsyncListener listener) {

        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();

//第一个参数是上下文对象，
// 第二个参数是云端代码的方法名称，
// 第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），
// 第四个参数是回调类
        ace.callEndpoint(ctx, funcationName, params,
                new CloudCodeListener() {
                    @Override
                    public void onSuccess(Object object) {
/*在json解析出现错误的时候，这里会显示两次*/
                        LogUtils.i("data clazz = " + clazz);
                        LogUtils.i("data string =　" + object.toString());

//                        Object obj = gson.fromJson(object.toString(), clazz);
//                        LogUtils.i("返回JSON数据:" + obj.toString());
                        listener.onSuccess(gson.fromJson(object.toString(), clazz));

                    }

                    @Override
                    public void onFailure(int code, String msg) {

                        listener.onFailure(code, msg);
                        mToast(ctx, R.string.network_erro);

                        LogUtils.i("访问云端方法失败:" + msg);
                    }
                });
    }

    /**
     * 没有更多返回结果
     * 云端代码
     *
     * @param ctx
     * @param params        JSONObject 请求参数
     * @param funcationName 方法名
     */
    public static void AsyncFunction(Context ctx, JSONObject params, String funcationName,
                                     final AsyncListener listener) {
        AsyncFunction(ctx, params, funcationName, BaseModel.class
                , listener);

//第一个参数是上下文对象，
// 第二个参数是云端代码的方法名称，
// 第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），
// 第四个参数是回调类
    }

    public static void AsyncFunction(Context ctx, JSONObject params, String funcationName) {
        AsyncFunction(ctx, params, funcationName, BaseModel.class
                , new AsyncListener() {
                    @Override
                    public void onSuccess(Object object) {

                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
    }


    /**
     * Bmob批量上传t图片文件
     * 先修正文件地址，再进行操作
     *
     * @param file     文件地址，有效的文件地址
     * @param context
     * @param listener 回调
     */
    public static void UploadImages(final Context context, final String[] file, final GoToUploadImages listener) {


        BmobFile.uploadBatch(context, file, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                if (listener != null && file.length == urls.size()) {
                    listener.Result(urls, files);

                    for (String url : urls) {
                        setThumbnail(context, url);

                    }
                }

            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                LogUtils.i("uploadBatch", "onProgress :" + curIndex + "---" + curPercent + "---" + total + "----" + totalPercent);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                if (listener != null) {
                    listener.onError(statuscode, errormsg);
                }
                LogUtils.i("uploadBatch", "批量上传出错：" + statuscode + "--" + errormsg);
            }
        });
    }


    /**
     * 生成缩略图
     *
     * @param context
     * @param url
     */
    private static void setThumbnail(Context context, String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Contants.PARAMS_URL, url);
        } catch (JSONException e) {
            LogUtils.e("thumbnail  add params failure");
        }
        AsyncFunction(context, jsonObject, FUNCTION_SET_THUMBNAIL, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                //此处得到的缩略图地址（thumbnailUrl）不一定能够请求的到，此方法为异步方法
                LogUtils.i("thumbnailName = " + object.toString());
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.e("setThumbnail faile  code = " + code + "  errormsg = " + msg);
            }
        });

    }

    /**
     * toast
     *
     * @param context
     * @param strId   提示语
     */
    private static void mToast(Context context, int strId) {
        Toast.makeText(context, strId, Toast.LENGTH_LONG).show();
    }

    /**
     * toast
     *
     * @param context
     * @param str     提示语
     */
    private static void mToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }


}
