package com.hzu.zao.utils;

import android.content.Context;
import android.widget.Toast;

import com.hzu.zao.WanToAnsDetailActivity.listCallback;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.model.Comment4list;
import com.hzu.zao.model.MyUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;

/**
 * 1、获取用户提出的问题，前30条数据 并将获取到的数据缓存到本地 2、获取用户问题对应的评论回复
 *
 * @author Nearby Yang
 *
 *         Create at 2015 下午8:57:20
 */
public class GetUserQuestion {

	private Context context;
	private JsonUtility jsonUtil = null;
	private JSONObject param = null;// 需要上传的参数
	private MyCallback mcb;
	private listCallback myLcb;
	private MyUser userInfo;
	// 使用云端代码
	private AsyncCustomEndpoints asyncCode = null;

	// 构造函数
	public GetUserQuestion(Context context) {

		this.context = context;

		asyncCode = new AsyncCustomEndpoints();
		jsonUtil = new JsonUtility(context);
		userInfo = BmobUser.getCurrentUser(context, MyUser.class);

	}

	// 注册回调函数
	public void callback(MyCallback mc) {
		this.mcb = mc;
	}

	// 注册回调函数
	public void listCb(listCallback lcb) {
		this.myLcb = lcb;
	}

	/**
	 * 使用云端代码获取问题表的数据
	 *
	 *
	 */
	public void getUserQuestion() {


		param = new JSONObject();

		JSONArray paramArray = null;
		JSONObject lastJson = null;

		// 获取本地缓存的最新的那条数据
		paramArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.WANTOANS_KEY);

		try {
			param.put("type", "q");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		// 过滤掉当前用户的提问
		if (null != userInfo) {
			try {
				param.put("userObjectId", userInfo.getObjectId());

			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		// Log.i("本地缓存--paramArray--",(paramArray==null)+"");

		// 本地缓存的数据不为空
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

						if (!("").equals(arg0.toString())
								&& (!("-1").equals(arg0.toString()))) {

							// Log.i("获取云端数据", " "+arg0.toString());

							jsonUtil.getQuestion(arg0.toString());

							mcb.getSuccess(true);

						} else {

							mcb.getSuccess(false);

							Toast.makeText(context, "暂无数据，请稍后再刷新",
									Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void onFailure(int arg0, String arg1) {
						Toast.makeText(context,
								"错误代码：" + arg0 + "\n错误信息 " + arg1,
								Toast.LENGTH_SHORT).show();

					}
				});

	}

	/**
	 * 查询对应问题的评论、回复
	 *
	 * @param question_id
	 */
	public void getComm(String question_id) {

		param = new JSONObject();

		try {
			param.put("question_id", question_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		asyncCode.callEndpoint(context, Contants.GETCOMM_COULDCODE, param,
				new CloudCodeListener() {

					@Override
					public void onSuccess(Object arg0) {

						// Log.i("详细信息   返回信息", arg0.toString());

						if (!("-1").equals(arg0.toString())
								&& (!("").equals(arg0.toString()))) {// 有评论，可能没有回复

							List<Comment4list> commList = jsonUtil
									.GetComment(arg0.toString());

							myLcb.getQuestComm(commList);

						} else {// 没有评论回复
							myLcb.getQuestComm(null);
						}

					}

					@Override
					public void onFailure(int arg0, String arg1) {

						Toast.makeText(context,
								"服务器正忙  \n 错误代码：" + arg0 + " 错误信息：" + arg1,
								Toast.LENGTH_SHORT).show();

						myLcb.getQuestComm(null);
					}
				});

	}

}
