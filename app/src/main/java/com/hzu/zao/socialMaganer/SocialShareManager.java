package com.hzu.zao.socialMaganer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.hzu.zao.R;
import com.hzu.zao.config.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 社会化分享
 * Created by Nearby Yang on 2016-04-08.
 */
public class SocialShareManager {

    /**
     * 纯文字的分享
     * @param context
     * @param content 文字
     */
    public static void shareText(Context context, String content){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
//      intent.setPackage("com.sina.weibo");
//        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.app_name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "请选择"));
    }

    /**
     * 分享纯图片 单张图片
     * @param context
     * @param imageUrl
     */
    public static void shareImage(Context context,  String imageUrl){


        List<String> list = new ArrayList<>();
        list.add(imageUrl);

        shareImagesByIntent(context, list);
    }




    /**
     * 分享多图
     *
     * @param context
     * @param filePathList 文件的地址
     */
    public static void shareImagesByIntent(Context context, List<String> filePathList) {
        ArrayList<Uri> picturesUriArrayList = new ArrayList<>();

//        File pictureFile1=new File
//                (Environment.getExternalStorageDirectory()+File.separator+"test1.png");
//        File pictureFile2=new File
//                (Environment.getExternalStorageDirectory()+File.separator+"test2.png");
//
//        Uri pictureUri1=Uri.fromFile(pictureFile1);
//        Uri pictureUri2=Uri.fromFile(pictureFile2);
//
//        picturesUriArrayList.add(pictureUri1);
//        picturesUriArrayList.add(pictureUri2);

        for (String filePath : filePathList) {
            Uri pictureUri = Uri.fromFile(new File(filePath));
            picturesUriArrayList.add(pictureUri);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, picturesUriArrayList);
        intent.setType("image/jpeg");
        Intent.createChooser(intent, "分享多张图片");
        context.startActivity(intent);
    }


    /**
     * toast
     *
     * @param msg
     */
    private static void toast(String msg) {
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_LONG).show();

    }
}
