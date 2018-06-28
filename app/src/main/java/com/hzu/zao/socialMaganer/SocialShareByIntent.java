package com.hzu.zao.socialMaganer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.hzu.zao.network.NetworkReuqest;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.view.CusProcessDialog;

import java.util.List;

/**
 * 下载图片，进行分享
 * Created by Nearby Yang on 2016-04-18.
 */
public class SocialShareByIntent {


    /**
     * 下载图片并且分享
     */
    public static void downloadImagesAndShare(final Context context, List<String> list) {
        for (String url : list) {
            LogUtils.e("share images = " + url);
        }


        final ProgressDialog progressDialog = CusProcessDialog.commenProgressDialog(context, "正在下载图片");
        progressDialog.show();
        NetworkReuqest.call(context, list, new NetworkReuqest.JSONRespond() {
            @Override
            public void onSuccess(List<String> response) {
                progressDialog.dismiss();
                SocialShareManager.shareImagesByIntent(context, response);

            }

            @Override
            public void onFailure(String erroMsg) {
                progressDialog.setMessage("下载图片失败，请稍后再试");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 2000);
            }
        });


    }

    /**
     * 下载单张图片
     * 通过意图进行分享
     */
    public static void downloadImageAndShare(final Context context, String imageUrl) {
        LogUtils.e("share image = " + imageUrl);

        final ProgressDialog progressDialog = CusProcessDialog.commenProgressDialog(context, "正在下载图片");
        progressDialog.setMessage("正在下载图片");
        progressDialog.show();

        NetworkReuqest.call(context, imageUrl, new NetworkReuqest.JSONRespond() {
            @Override
            public void onSuccess(List<String> response) {
                progressDialog.dismiss();
                SocialShareManager.shareImagesByIntent(context, response);

            }

            @Override
            public void onFailure(String erroMsg) {
                progressDialog.setMessage("下载图片失败，请稍后再试");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 2000);
            }

        });


    }


}
