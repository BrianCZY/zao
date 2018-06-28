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

public class MyAnswerAdapter extends BaseAdapter {

	private Context ctx;
	private JSONArray jsonArray;
	private JSONObject jsonObject;
	private int length = 0;
	
	
	public MyAnswerAdapter(Context ctx) {

		this.ctx = ctx;
	}
	
	public void setData(JSONArray jsonArray){
		this.jsonArray=jsonArray;
	}

	@Override
	public int getCount() {
		
		
		if(null!=jsonArray){
			length=jsonArray.length();
		}
		
		return length;
	}

	@Override
	public Object getItem(int position) {
		JSONObject tempObject=null;
		
		try {
			tempObject=jsonArray.getJSONObject(position);
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
		
		viewHolder holder=null;
		
		if (null == convertView) {
			holder = new viewHolder();
			
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.my_item, null);
			
			
			holder.contentView = (TextView) convertView
					.findViewById(R.id.a_content);
			
			holder.dateView=(TextView) convertView
			.findViewById(R.id.a_date);
			
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
		

		//ʱ��
		String createdAt="";
		
		//��ȡ����
		try {
			holder.contentView.setText(StringUtils.getEmotionContent(ctx,jsonObject.getString("comContent")));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			createdAt=jsonObject.getString("comm_createdAt");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		holder.dateView.setText(createdAt);
		
		
		
		return convertView;
	}
	
	
	
	private class viewHolder {
		TextView contentView;
		TextView dateView;

	}
}
