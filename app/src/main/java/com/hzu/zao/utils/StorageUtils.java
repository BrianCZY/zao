package com.hzu.zao.utils;

import android.content.Context;
import android.os.Environment;

import com.hzu.zao.config.Contants;

import java.io.File;

/**
 * 涉及存储的操作类
 * Created by Nearby Yang on 2016-03-07.
 */
public class StorageUtils {


    /**
     * 存放图片对应的文件夹
     *
     * @param context 上下文对象
     * @return file对象
     */
    public static File createImageFile(Context context) {

        return new File(createdDir(context, Contants.FILE_IMAGE_PATH));
    }

    /**
     * 存放图片对应的文件夹
     *
     * @param context 上下文对象
     * @return file对象
     */
    public static File createCacheFile(Context context) {

        return new File(createdDir(context, Contants.FILE_CACHE_PATH));
    }



    /**
     * 下载文件夹
     * @param context
     * @return
     */
    public static File createDownloadFile(Context context) {

        return new File(createdDir(context, Contants.FILE_PAHT_DOWNLOAD));
    }

    /**
     * 创建或者是获取文件夹
     *
     * @param context
     * @param dirName
     * @return
     */
    private static String createdDir(Context context, String dirName) {

        String path = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() + dirName :
                context.getCacheDir().getAbsolutePath() + dirName;
        File filePath = new File(path);

        if (!filePath.exists()) {
            filePath.mkdirs();
        }


        return path;
    }


    /**
     * 获取文件名,视频文件
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
//        fileName = fileName.substring(0, fileName.lastIndexOf("?") - 2);
//        return fileName;
    }

    /**
     * 照片
     * @param url
     * @return
     */
    public static String getImageName(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("?") );
        return fileName;
    }
}
