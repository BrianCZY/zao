package com.hzu.zao.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.utils.NetworkUtils;
import com.hzu.zao.utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * function ：下载数据，这里做小文件的下载，作为用户保存照片，下载小视频
 * Created by young fuJin on 2016/1/7.
 */
public class SmallFiledownloadRequest extends Request<String> {

    private Response.Listener<String> mListener;
    private Map<String, String> params;
    private String url;
    private int fileType = 0;

    public static final int FILE_TYPE_VIDEO = 10;
    public static final int FILE_TYPE_IMAGE = 11;
    public static final int FILE_TYPE_DOWNLOAD = 12;

    /**
     * 下载图片，图片地址直接可以使用，不需要进行编码
     *
     * @param url
     * @param mListener
     * @param listener
     */
    public SmallFiledownloadRequest(String url, Response.Listener<String> mListener, Response.ErrorListener listener) {
        super(url, listener);
        this.url = url;
        this.fileType = FILE_TYPE_IMAGE;
        this.params = new HashMap<>();
        this.mListener = mListener;
    }

    /**
     * 下载图片，图片地址直接可以使用，不需要进行编码
     *
     * @param url
     * @param fileType
     * @param mListener
     * @param listener
     */
    public SmallFiledownloadRequest(String url, int fileType, Response.Listener<String> mListener, Response.ErrorListener listener) {
        super(url, listener);
        this.url = url;
        this.fileType = fileType;
        this.params = new HashMap<>();
        this.mListener = mListener;
    }



    /**
     * 下载文件
     * 默认get方法就可以
     *
     * @param context
     * @param url      未进行添加key的url
     * @param fileType
     * @param listener
     */
    public SmallFiledownloadRequest(Context context, String url, int fileType, Response.Listener<String> mListener, Response.ErrorListener listener) {
        super(NetworkUtils.getRealUrl(context, url, false), listener);
        this.url = url;
        this.fileType = fileType;
        this.params = new HashMap<>();
        this.mListener = mListener;
    }

    public SmallFiledownloadRequest(int fileType,
                                    String url, Response.Listener<String> mListener, Response.ErrorListener listener) {
        super(Method.GET, url, listener);
        this.url = url;
        this.fileType = fileType;
        this.mListener = mListener;

    }

    public SmallFiledownloadRequest(int method, int fileType, Map<String, String> params,
                                    String url, Response.Listener<String> mListener, Response.ErrorListener listener) {
        super(method, url, listener);
        this.url = url;
        this.fileType = fileType;
        this.params = params;
        this.mListener = mListener;

    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        //            String jsonStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        LogUtils.e("url = " + url);
        String filePath;
//区分文件夹
         if (fileType == FILE_TYPE_DOWNLOAD) {//下载图片文件，分享出去
            filePath = StorageUtils.createDownloadFile(MyApplication.getContext()) + "/" + StorageUtils.getImageName(url);
        } else {//保存文件
            filePath = StorageUtils.createImageFile(MyApplication.getContext()) + "/" + StorageUtils.getImageName(url);
        }
//        filePath += url.substring(url.lastIndexOf('/') + 1);
//        filePath += getFileName(url);

        LogUtils.e("SmallFile　Download  download filePath = " + filePath);
        File file = new File(filePath.substring(0, filePath.lastIndexOf('/')));

        //创建文件夹
        if (!file.exists()) {
            boolean created = file.mkdirs();
        }


        try {
            FileOutputStream out = new FileOutputStream(filePath);
            out.write(response.data);
            out.close();
        } catch (IOException e) {
            LogUtils.e("io erro = " + e.toString());
        }

        return Response.success(filePath, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put(Contants.REST_APP_KEY, Contants.APPID);
        headers.put(Contants.REST_APP_REST_KEY, Contants.APP_RESET_KEY);
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }


}
