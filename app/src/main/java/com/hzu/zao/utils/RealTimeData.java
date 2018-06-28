package com.hzu.zao.utils;

import android.content.Context;

import com.hzu.zao.interfaces.JsonCallback;

import org.json.JSONObject;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;


/**
 * 数据实时同步 需要传入参数：context、监听的表名 返回数据：data 使用回调函数
 *
 * @author Nearby Yang
 *
 *         Create at 2015-5-14 上午9:28:32
 */

public class RealTimeData {

	private JsonCallback cb;
	private Context ctx;
	private BmobRealTimeData realTimeData = null;
	private JSONObject data=null;//返回的更新数据

	public RealTimeData(Context ctx) {

		this.ctx = ctx;

		realTimeData = new BmobRealTimeData();
	}

	// 注册回调函数
	public void getNewJsonArray(JsonCallback cb) {
		this.cb = cb;
	}

	// 数据实时同步
	public void realTimeData(final String table) {

		realTimeData.start(ctx, new ValueEventListener() {

			@Override
			public void onDataChange(JSONObject arg0) {
				if (BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0
						.optString("action"))) {

					data = arg0.optJSONObject("data");

					cb.jsonCallb(data);

//					Log.i("bmob", ""+data.toString());
				}

//				Log.i("myMsg", ""+arg0.toString());
			}

			@Override
			public void onConnectCompleted() {
				if (realTimeData.isConnected()) {

					realTimeData.subTableUpdate(table);
//					Log.i("connected", "success");
				}
			}
		});

	}

}
