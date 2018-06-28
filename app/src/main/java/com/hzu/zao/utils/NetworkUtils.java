package com.hzu.zao.utils;

import android.content.Context;

import com.hzu.zao.config.Contants;

import cn.bmob.v3.datatype.BmobFile;


/**
 * 网络请求工具类
 * 获取网址或者截取网址
 * Created by Nearby Yang on 2015-11-13.
 */
public class NetworkUtils {

    /**
     * 获取签名url
     *
     * @param context
     * @param url
     * @param isLocation
     * @return
     */
    public static String getRealUrl(Context context, String url, boolean isLocation) {

        if (isLocation) {
            return url;
        } else {
            String filename = url.substring(url.lastIndexOf('/') + 1);
            BmobFile file = new BmobFile(filename, "", url);
            return file.getFileUrl(context);
        }
    }

    /**
     * 载入缩略图
     *
     * @param context
     * @param url
     * @return
     */
    public static String getRealUrl(Context context, String url) {

        return getRealUrl(context, url + "_" + Contants.MODEL_ID, false);

    }

}
