package com.hzu.zao.utils;

import android.util.Log;
import android.widget.Toast;

import com.hzu.zao.BuildConfig;
import com.hzu.zao.config.MyApplication;


/**
 * log调试类
 * Created by Nearby Yang on 2015-10-09.
 */
public class LogUtils {
    private final static String Tag = " ++ zao ++ ";
    private final static String ms = "msg -> ";

    /**
     * logcat Error
     * @param msg 信息，可以多个参数，最多支持两个
     */
    public static void e(String ...msg) {
        if (BuildConfig.DEBUG) {
            if (msg.length > 1 ){
                Log.e(Tag+msg[0], msg[1]);
            }else{
                Log.e(Tag, msg[0]);
            }
        }

    }
    /**
     * logcat debug
     * @param msg 信息，可以多个参数，最多支持两个
     */
    public static void d(String ...msg) {

        if (BuildConfig.DEBUG) {
            if (msg.length > 1 ){
                Log.d(Tag + msg[0], msg[1]);
            }else{
                Log.d(Tag, msg[0]);
            }

        }
    }

    /**
     * logcat info
     * @param msg 信息，可以多个参数，最多支持两个
     */
    public static void i(String ...msg) {

        if (BuildConfig.DEBUG) {
            if (msg.length > 1 ){
                Log.i(Tag + msg[0], msg[1]);
            }else{
                Log.i(Tag, msg[0]);
            }

        }
    }

    /**
     * toast 时长：Toast.LENGTH_LONG
     *
     * @param msg 需要显示的信息
     */
    public static void ts(String msg){
        if (BuildConfig.DEBUG){
            Toast.makeText(MyApplication.getContext(),ms+msg,Toast.LENGTH_LONG).show();
        }
    }

}
