package com.hzu.zao.utils;

import android.content.Context;
import android.widget.Toast;

import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;

public class GetMessage {

	private Context ctx;
	private MyCallback mcb;
	private JSONObject param = null;
	private JSONArray jsonArray = null;
	private JsonUtility jsonUtils = null;
	private AsyncCustomEndpoints asyncCode = null;

	public GetMessage(Context ctx) {
		this.ctx = ctx;

		jsonUtils = new JsonUtility(ctx);
		asyncCode = new AsyncCustomEndpoints();
	}

	public void getMessage(String userId) {

		param = new JSONObject();
		jsonArray =  MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MESSAGE_KEY);

//		Log.i("本地缓存呢",""+jsonArray);

		if (null != jsonArray) {
			try {
				param.put("createdAt",
						jsonArray.getJSONObject(0).getString("c_createdAt"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			param.put("toWho", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
//Log.i("当前用户  userId",userId+"");
		asyncCode.callEndpoint(ctx, Contants.GETMSG_COULDCODE, param,
				new CloudCodeListener() {

					@Override
					public void onSuccess(Object arg0) {

						LogUtils.d("返回数据", arg0.toString() + "");

						if ((!("").equals(arg0.toString()))&&(!("-1").equals(arg0.toString()))) {

							jsonUtils.GetMessage(arg0.toString());
							mcb.getSuccess(true);
						}
					}

					@Override
					public void onFailure(int arg0, String arg1) {

						Toast.makeText(ctx, "服务器正忙，稍后再试", Toast.LENGTH_SHORT)
								.show();
						mcb.getSuccess(false);
//						Log.i("云端方法失败", "code " + arg0 + " msg " + arg1);
					}
				});

	}

	// 刷新消息列表
	public void ReflashMsg(String userId) {

		param = new JSONObject();
		jsonArray =  MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MESSAGE_KEY);

		if (null != jsonArray) {
			try {
				param.put("createdAt",
						jsonArray.getJSONObject(0).getString("c_createdAt"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			param.put("toWho", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Log.i("userId", userId);
		asyncCode.callEndpoint(ctx, Contants.GETMSG_COULDCODE, param,
				new CloudCodeListener() {

					@Override
					public void onSuccess(Object arg0) {

						// Log.i("返回数据", arg0 + "");

						if (!("-1").equals(arg0.toString())) {
//Log.i("刷新msg", "刷新了");
							jsonUtils.GetMessage(arg0.toString());

						}
					}

					@Override
					public void onFailure(int arg0, String arg1) {

//						Log.i("云端方法失败", "code " + arg0 + " msg " + arg1);
					}
				});

	}




	// =======注册回调函数====

	// 注册回调函数
	public void callback(MyCallback mc) {
		this.mcb = mc;
	}
}
