package com.hzu.zao.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hzu.zao.model.InviteCode;
import com.hzu.zao.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommAdapter extends BaseAdapter {

	Context context; // ������

	ListItemView listItemView;
	List<InviteCode> list = new ArrayList<>();

	public class ListItemView { // �Զ���ؼ�����
		public ImageView image;
		public TextView userNameTv;
		public TextView dateTv;

	}

	/**
	 * RecommAdapter �Ĺ��캯��
	 * @author xiaoyang
	 * @param context
	 * @param list  InviteCode �ļ��ϡ�
	 *            
	 */
	public RecommAdapter(Context context, List<InviteCode> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// Log.i("canta", "InfoAdapter listMsg.size()="+listMsg.size());
		// return this.types.length;

		return list.size();
	}

	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.i("canta",
				"list.get(position).getInvitees().getUsername()="
						+ list.get(position).getInvitees().getUsername());
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
//			Log.i("xy", "RecommAdapter  0");
			listItemView = new ListItemView();

			convertView = LayoutInflater.from(this.context).inflate(
					R.layout.recomm_list_item, null);

			listItemView.image = (ImageView) convertView
					.findViewById(R.id.image_v);
			listItemView.userNameTv = (TextView) convertView
					.findViewById(R.id.name);
			listItemView.dateTv = (TextView) convertView
					.findViewById(R.id.date);
			
			
			convertView.setTag(listItemView);
		} else { //���convertView ��Ϊ�գ�����Ҫ���´���view,����ʹ��֮ǰ��view
			listItemView = (ListItemView) convertView.getTag();
			

		}
		listItemView.image.setVisibility(View.VISIBLE);
		//listItemView.image.setImageResource(R.drawable.headp1);
		//listItemView.image.setImageBitmap((Bitmap) list.get(position);
		if(list.get(position).getInvitees()!=null){
			listItemView.userNameTv.setText((String) list.get(position).getInvitees().getUsername());
		}
		listItemView.dateTv.setText((String) list.get(position).getCreatedAt());
		
		return convertView;
	}

}
