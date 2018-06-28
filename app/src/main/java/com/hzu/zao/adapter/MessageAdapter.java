package com.hzu.zao.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hzu.zao.R;
import com.hzu.zao.WanToAnsDetailActivity;
import com.hzu.zao.fragment.MessageFragment;
import com.hzu.zao.model.Comment;
import com.hzu.zao.model.Reply;
import com.hzu.zao.utils.GetMessage;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.utils.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.listener.UpdateListener;

public class MessageAdapter extends BaseAdapter {

    private ListView lv;
    private JSONArray jArray = null;
    private JSONObject jsonOject = null;
    private Activity context;
    private boolean isRead = false;// 消息是否已读
    private String msgId;// 问题或者评论的id
    private GetMessage getM = null;

    public MessageAdapter(Activity context, ListView lv, MessageFragment.cback mycb) {
        this.context = context;
        this.lv = lv;


        getM = new GetMessage(context);

    }

    // 数据源
    public void SetJsonArray(JSONArray jArray) {
        this.jArray = jArray;

    }

    @Override
    public int getCount() {
        int size = 0;
        if (null != jArray) {
            size = jArray.length();
        }
        return size;
    }

    @Override
    public Object getItem(int position) {

        JSONObject temp = null;

        try {
            temp = jArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListItemView listItemView = null;

        if (null == convertView) {

            listItemView = new ListItemView();

            convertView = LayoutInflater.from(context).inflate(
                    R.layout.message_list_card, null);
            // 头像
            listItemView.image = (ImageView) convertView
                    .findViewById(R.id.card_head_image);
            // 用户名
            listItemView.userNameTv = (TextView) convertView
                    .findViewById(R.id.card_user_name_tv);
            // 消息类型
            listItemView.typeTv = (TextView) convertView
                    .findViewById(R.id.card_type_tv);
            // 消息类型
            listItemView.contentTV = (TextView) convertView
                    .findViewById(R.id.card_content_tv);

            convertView.setTag(listItemView);

        } else {

            listItemView = (ListItemView) convertView.getTag();

        }

        String url = "";
        String type = "";
        String text = "";

        int number = 0;

        // 数据源
        number = jArray.length() - 1;

        try {
            jsonOject = jArray.getJSONObject(number - position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            type = jsonOject.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            url = jsonOject.getString("_Icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listItemView.image.setTag(url);

        try {
            listItemView.userNameTv.setText(jsonOject.getString("_nickName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 问题或者评论的id

        try {
            msgId = jsonOject.getString("ms_objectId");
        } catch (JSONException e2) {
            e2.printStackTrace();
        }

        // -------------请求是否已读-----------------
        if (("comment").equals(type)) {// 查评论表


            text = "评论了你";

        } else {// 查回复表


            text = "回复了你";

        }

        try {
            isRead = jsonOject.getBoolean("isRead");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
//		Log.i("是否已读", "id " + msgId + " isread " + isRead);

        if (isRead) {// 已读

            listItemView.contentTV.setTextColor(Color.BLACK);
        } else {// 未读
            listItemView.contentTV.setTextColor(Color.parseColor("#45CE88"));

        }
        try {
            if (!TextUtils.isEmpty(jsonOject.getString("_comContent"))) {
                listItemView.contentTV.setText(StringUtils.getEmotionContent(context, jsonOject.getString("_comContent")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        listItemView.typeTv.setText(text);
        lv.setOnItemClickListener(new OnItemClickListener() {

            JSONObject myobj = null;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    myobj = jArray.getJSONObject(jArray.length() - position - 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(context, WanToAnsDetailActivity.class);
                intent.putExtra("msg", myobj.toString());

                context.startActivity(intent);

                context.overridePendingTransition(
                        R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);

                updateIsRead(myobj);

            }
        });

        // Log.i("url-", url);
        ImageLoader.getInstance().displayImage(url, listItemView.image);
        return convertView;

    }


    //动态更新消息是否已读

    private void updateIsRead(JSONObject JsonObj) {

        String objectId = "";
        String type = "";

        boolean _isRead = false;

        // 更新云端数据
        try {
            objectId = JsonObj.getString("ms_objectId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            type = JsonObj.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            _isRead = JsonObj.getBoolean("isRead");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        try {
//            JsonObj.put("isRead", !JsonObj.getBoolean("isRead"));
//            notifyDataSetChanged();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        if (("comment").equals(type)) {// 如果 消息 类型是评论
            if (!_isRead) {

                Comment comment = new Comment();
                comment.setIsRead(true);

                comment.update(context, objectId, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        LogUtils.d("评论---isread", "更新成功");

                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        LogUtils.e("回复 失败", "code " + arg0 + "  msg  " + arg1);
                    }
                });
            }

        } else {// 信息类型是 回复

            if (!_isRead) {
                Reply reply = new Reply();
                reply.setIsRead(true);

                reply.update(context, objectId, new UpdateListener() {

                    @Override
                    public void onSuccess() {

                        LogUtils.d("回复---isread 更新成功");

                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {

                        LogUtils.e("回复 失败 code " + arg0 + "  msg  " + arg1);
                    }
                });

            }
        }


        try {
            JsonObj.put("isRead", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 刷新界面数据
        reflash();


    }


    // 条目的点击事件
    public class myOnclick implements OnItemClickListener {

        //		JSONArray jsonArray;
        JSONObject _jsonObject;
        String objectId;
        String type = "";

        boolean _isRead = false;
        int nn = 0;

        public myOnclick(JSONObject _jsonObject) {

            this._jsonObject = _jsonObject;
//			nn = jsonOject.length() - 1;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {


            Intent intent = new Intent(context, WanToAnsDetailActivity.class);

            intent.putExtra("msg", _jsonObject.toString());

            context.startActivity(intent);

            context.overridePendingTransition(
                    R.anim.enter_from_right_animation,
                    R.anim.exit_to_left_animation);

//			Log.i("点击",  " 数据源 " + _jsonObject.toString());

            // notifyDataSetChanged();

            // 更新云端数据
            try {
                objectId = _jsonObject.getString("ms_objectId");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                type = _jsonObject.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                _isRead = _jsonObject.getBoolean("isRead");
            } catch (JSONException e) {
                e.printStackTrace();
            }

//			Log.i("type", type + "  " + ("comment").equals(type));
            // ("comment").equals(type)

            if (("comment").equals(type)) {// 如果 消息 类型是评论

                if (!_isRead) {

                    Comment comment = new Comment();
                    comment.setIsRead(true);

                    comment.update(context, objectId, new UpdateListener() {

                        @Override
                        public void onSuccess() {

//							Log.i("评论---isread", "更新成功");

                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
//							Log.i("回复 失败", "code " + arg0 + "  msg  " + arg1);
                        }
                    });
                }


            } else {// 信息类型是 回复


                if (!_isRead) {

                    // }

                    Reply reply = new Reply();

                    reply.setIsRead(true);

                    reply.update(context, objectId, new UpdateListener() {

                        @Override
                        public void onSuccess() {

//							Log.i("回复---isread", "更新成功");

                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {

//							Log.i("回复 失败", "code " + arg0 + "  msg  " + arg1);
                        }
                    });

                }
            }


            try {
                _jsonObject.put("isRead", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 刷新界面数据
            reflash();

        }

    }


    public void reflash() {
        notifyDataSetChanged();
    }

    // 数据结构
    public class ListItemView {
        public ImageView image;
        public TextView userNameTv;
        public TextView typeTv;

        public TextView contentTV;

    }

}