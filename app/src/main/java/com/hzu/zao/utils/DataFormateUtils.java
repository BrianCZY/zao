package com.hzu.zao.utils;

import android.content.Context;

import com.hzu.zao.model.PictureInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 格式化公用类
 * Created by Nearby Yang on 2015-12-04.
 */
public class DataFormateUtils {

    /**
     * 将只是图片地址的list转化成pictureInfo的list
     * 处理图片地址,添加缩略图地址与高清大图地址
     *
     * @param context
     * @param list
     * @return 图片缩率图与高清大图的链表
     */
    public static List<PictureInfo> formate2PictureInfo(Context context, List<String> list) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();
        if (list != null) {
            for (String url : list) {
                PictureInfo pictureInfo = new PictureInfo(NetworkUtils.getRealUrl(context, url, false), NetworkUtils.getRealUrl(context, url));
                pictureInfoList.add(pictureInfo);
            }
        }
        return pictureInfoList;
    }

    /**
     * 转化成本地的文件地址
     *
     * @param list 要转化的地址
     * @return
     */
    public static List<PictureInfo> formate2PictureInfo4Local( List<String> list) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();
        if (list != null) {
            for (String url : list) {
                PictureInfo pictureInfo = new PictureInfo(url);
                pictureInfoList.add(pictureInfo);
            }
        }
        return pictureInfoList;
    }

    /**
     * 批量将网址转化为缩略图地址
     * bmob中需要使用
     *
     * @param context
     * @param list
     * @return
     */
    public static List<String> thumbnailList(Context context, List<String> list) {
        List<String> urlList = new ArrayList<>();
        if (list != null) {
            for (String thumbnailUrl : list) {
                urlList.add(NetworkUtils.getRealUrl(context, thumbnailUrl));
//            LogUtils.d(" 真实的url = " + NetworkUtils.getRealUrl(context, thumbnailUrl));
            }
        }
        return urlList;
    }
    /**
     * 批量将网址转化为缩略图地址
     * bmob中需要使用
     *
     * @param context
     * @param list
     * @return
     */
    public static List<String> bigImagesList(Context context, List<String> list) {
        List<String> urlList = new ArrayList<>();
        if (list != null) {
            for (String thumbnailUrl : list) {
                urlList.add(NetworkUtils.getRealUrl(context, thumbnailUrl,false));
//            LogUtils.d(" 真实的url = " + NetworkUtils.getRealUrl(context, thumbnailUrl));
            }
        }
        return urlList;
    }




    /**
     * 格式化数据
     *
     * @param uriList
     * @param context
     * @return
     */
    public static List<PictureInfo> formateStringInfoList(Context context, List<String> uriList) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();

        if (uriList != null && uriList.size() > 0) {

            for (String uri : uriList) {

                //大图uri、小图uri
                PictureInfo pictureInfo = new PictureInfo(NetworkUtils.getRealUrl(context, uri, false),
                        NetworkUtils.getRealUrl(context, uri));
                pictureInfoList.add(pictureInfo);
            }

        }
        return pictureInfoList;
    }

    /**
     * 格式化数据
     *
     * @param uriList
     * @return
     */
    public static List<PictureInfo> formateLocalImage(List<String> uriList) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();

        if (uriList != null && uriList.size() > 0) {

            for (String uri : uriList) {

                //大图uri、小图uri
                PictureInfo pictureInfo = new PictureInfo(uri);
                pictureInfoList.add(pictureInfo);
            }

        }
        return pictureInfoList;
    }
}
