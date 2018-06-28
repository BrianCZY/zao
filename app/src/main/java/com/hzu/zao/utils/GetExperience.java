package com.hzu.zao.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.model.MyUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;

/**
 * 请求下载Experience表的数据
 *
 * @author Nearby Yang
 *
 *         Create at 2015 下午5:35:33
 */
public class GetExperience {
	private Context context;
	JsonUtility jsonUtil = null;
	JSONObject param = null;// 需要上传的参数
	public MyCallback mcb;
	private MyUser userInfo=null;
	private AsyncCustomEndpoints asyncCode=null;

	public GetExperience(Context context) {
		this.context = context;

		jsonUtil = new JsonUtility(context);
		param = new JSONObject();
		userInfo=BmobUser.getCurrentUser(context, MyUser.class);

	}

	public void back(MyCallback mc) {
		this.mcb = mc;
	}

	// ==========================调用云端代码====================

	public void getExperience() {
		JSONArray paramArray = null;
		JSONObject lastJson = null;

		// 使用云端代码
		asyncCode = new AsyncCustomEndpoints();

		// 获取本地缓存的最新的那条数据
		paramArray =  MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.EXPERIENCE_KEY);

		try {
			param.put("type", "e");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}



		if (paramArray != null) {

			int lenght = paramArray.length() - 1;
			try {
				// 最后一条数据为最新的一条数据
				lastJson = paramArray.getJSONObject(lenght);

				// String createdAt=lastJson.get("createdAt").toString();

				param.put("createdAt", lastJson.get("createdAt"));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		asyncCode.callEndpoint(context, Contants.COULDCODE, param,
				new CloudCodeListener() {

					@Override
					public void onSuccess(Object arg0) {

						if (!("").equals(arg0.toString())&&(!("-1").equals(arg0.toString()))) {

							LogUtils.d("toString "+arg0.toString());

							jsonUtil.getExperience(arg0.toString());


							mcb.getSuccess(true);



						} else {

							mcb.getSuccess(false);

							Toast.makeText(context,
									"暂无数据，请稍后再刷新",
									Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void onFailure(int arg0, String arg1) {
						Toast.makeText(context,
								"错误代码：" + arg0 + " 错误信息 " + arg1,
								Toast.LENGTH_SHORT).show();

					}
				});


	}


	//===================分享获得经验==============================
	public void updataWealth(){
		JSONObject userObj=new JSONObject();
		asyncCode = new AsyncCustomEndpoints();

		//上传的参数，userId
		try {
			userObj.put("userId", userInfo.getObjectId());
		} catch (JSONException e) {
//			e.printStackTrace();
		}

		asyncCode.callEndpoint(context, Contants.UPDATAEXEALTH_COULDCODE, userObj, new CloudCodeListener() {

			@Override
			public void onSuccess(Object arg0) {
				Log.i("onSuccess", arg0.toString()+"");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Log.i("onFailure", "code" +arg0+" msg  "+arg1.toString()+"");

			}
		});

	}

}
