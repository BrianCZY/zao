package com.hzu.zao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hzu.zao.R;
import com.hzu.zao.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyExAdapter extends BaseAdapter {

	private Context context;
	// private CacheUtil cacheUtil;
	private JSONArray jsonArray = null;
	private JSONObject jsonObject = null;
	int length = 0;

	public MyExAdapter(Context context) {

		this.context = context;
		// cacheUtil = new CacheUtil(context);
	}

	@Override
	public int getCount() {

		if (null != jsonArray) {
			length = jsonArray.length();
		}
		return length;
	}

	@Override
	public Object getItem(int position) {
		JSONObject tempObject = null;
		try {
			tempObject = jsonArray.getJSONObject(position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tempObject;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder holder = null;
		if (null == convertView) {
			holder = new viewHolder();

			convertView = LayoutInflater.from(context).inflate(
					R.layout.my_item, null);

			holder.contentTx = (TextView) convertView
					.findViewById(R.id.a_content);

			holder.dateTx = (TextView) convertView.findViewById(R.id.a_date);

			convertView.setTag(holder);

		} else {

			holder = (viewHolder) convertView.getTag();
		}

		int order = length - position - 1;

		if (order >= 0) {

			try {
				jsonObject = jsonArray.getJSONObject(order);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}


		// ʱ��
		String createdAt = "";

		// ��ȡ����
		try {
			holder.contentTx.setText(StringUtils.getEmotionContent(context,jsonObject.getString("shareEx")));
		} catch (JSONException e) {
			e.printStackTrace();
		}


		try {
			createdAt = jsonObject.getString("createdAt");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		holder.dateTx.setText(createdAt);

		return convertView;
	}

	private class viewHolder {
		TextView contentTx;
		TextView dateTx;

	}

	public void setData(JSONArray jsonArray2) {
		jsonArray = jsonArray2;
	}
}
