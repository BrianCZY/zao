package com.hzu.zao.network;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.hzu.zao.R;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

;


/**
 * Volley网络请求类
 * <p/>
 * Created by Nearby Yang on 2016-03-03.
 */
public class NetworkReuqest {
    private static Gson gson = new Gson();

    private static final String BAIDU_GEOCODER = "http://api.map.baidu.com/geocoder/v2/";
    private static final String BAIDU_PLACE_SUGGESTION = "http://api.map.baidu.com/place/v2/suggestion";//周围
    private static final String BAIDU_PLACE_SEARCH = "http://api.map.baidu.com/place/v2/search";//服务
    public static final String BMOB_HOST = "https://api.bmob.cn/1/";//bmob
    public static final String ADVERTISERMENT = "classes/Advertisement";
    static int downloadNumber = 0;

    /**
     * 下载文件，下载完成返回url
     *
     * @param context
     * @param url
     * @param jsonResponse
     */
    public static void call(Context context, String url, final JsonRequstCallback<String> jsonResponse) {

        SmallFiledownloadRequest smallFiledownloadRequest = new SmallFiledownloadRequest(
                SmallFiledownloadRequest.FILE_TYPE_VIDEO,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d("success = " + response);
                if (jsonResponse != null) {
                    jsonResponse.onSuccess(response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (jsonResponse != null) {
                    jsonResponse.onFaile(error);
                }
                LogUtils.e("failure = " + error.toString());

            }
        });


        smallFiledownloadRequest.setTag(url);

//启动
        VolleyApi.getInstence(context).add(smallFiledownloadRequest);

    }

    /**
     * 下载图片
     *
     * @param context
     * @param url     未进行添加key的url,原始的url
     */
    public static void call(final Context context, String url) {
        SmallFiledownloadRequest iamgeDownload = new SmallFiledownloadRequest(context,
                url,
                SmallFiledownloadRequest.FILE_TYPE_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, context.getString(R.string.toast_save_iamge_success) + response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, R.string.toast_save_iamge_failure, Toast.LENGTH_LONG).show();
                    }
                }
        );

        iamgeDownload.setTag(url);

//启动
        VolleyApi.getInstence(context).add(iamgeDownload);

    }

    /**
     * 保存图片
     * 已经下载过的图片不会再重新下载
     * @param context
     * @param url     修改过uri的请求，添加了bmob的前面的
     */
    public static void call2(final Context context, String url) {
        String filePath = StorageUtils.createImageFile(context).getAbsoluteFile() + "/" + StorageUtils.getImageName(url);

        if ((new File(filePath)).exists()) {
            Toast.makeText(context,
                    context.getString(R.string.toast_save_iamge_success) + filePath,
                    Toast.LENGTH_LONG).show();
        } else {

            SmallFiledownloadRequest iamgeDownload = new SmallFiledownloadRequest(
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(context,
                                    context.getString(R.string.toast_save_iamge_success) + response,
                                    Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, R.string.toast_save_iamge_failure,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );

            iamgeDownload.setTag(url);

//启动
            VolleyApi.getInstence(context).add(iamgeDownload);

        }
    }


    /**
     * 批量下载图片到download文件夹
     *
     * @param context
     * @param url
     * @param jsR
     */
    public static void call(final Context context, String url, final JSONRespond jsR) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("urlList is null...,string must not be null");

        }
        //复用，创建list
        List<String> urlList = new ArrayList<>();
        urlList.add(url);

        call(context, urlList, jsR);

    }
    /**
     * 下载图片,返回图片地址，用作分享
     *
     * @param context
     * @param urlList list 修改过uri的请求，添加了bmob的前面的
     */
    public static void call(final Context context, List<String> urlList, final JSONRespond jsR) {
        if (urlList == null) {
            throw new IllegalArgumentException("urlList is null...");

        }

        final int number = urlList.size();
        final List<String> imagePath = new ArrayList<>();
        for (String url : urlList) {

            String filePath = StorageUtils.createDownloadFile(context).getAbsoluteFile() + "/" + StorageUtils.getImageName(url);
            File file = new File(filePath);
            if (file.exists()) {
                downloadNumber++;
                imagePath.add(filePath);

                if (jsR != null && number == downloadNumber) {
                    jsR.onSuccess(imagePath);
                }

            } else {

            SmallFiledownloadRequest iamgeDownload = new SmallFiledownloadRequest(
                    url, SmallFiledownloadRequest.FILE_TYPE_DOWNLOAD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            imagePath.add(response);
                            downloadNumber++;

                            if (jsR != null && number == downloadNumber) {
                                jsR.onSuccess(imagePath);
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            downloadNumber++;
                            if (jsR != null && number == downloadNumber) {
                                jsR.onFailure(error.toString());
                            }

//                        Toast.makeText(context, R.string.toast_save_iamge_failure, Toast.LENGTH_LONG).show();
                        }
                    }
            );

            iamgeDownload.setTag(url);
//启动
            VolleyApi.getInstence(context).add(iamgeDownload);

            }
        }


    }








    /**
     * 最简单的回调
     */
    public interface JSONRespond {
        void onSuccess(List<String> response);

        void onFailure(String erroMsg);
    }

    public interface JsonRequstCallback<T> {
        void onSuccess(T t);

        void onFaile(VolleyError error);

    }

    public interface SimpleRequestCallback<T> {
        void response(T t);
    }

}
