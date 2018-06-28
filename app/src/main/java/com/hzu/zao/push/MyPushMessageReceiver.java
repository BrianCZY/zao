package com.hzu.zao.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hzu.zao.utils.LogUtils;

import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {

    String msgString = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        msgString = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
        LogUtils.i("收到推送消息 message= " + msgString);
        Toast.makeText(context, "通知内容 " + msgString, Toast.LENGTH_SHORT).show();
    }

}
