package com.hzu.zao.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hzu.zao.R;
import com.hzu.zao.adapter.MessageAdapter;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.GetMessage;

import org.json.JSONArray;

import cn.bmob.v3.BmobUser;


public class MessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View mitView;
    private ListView lv = null;
    private LinearLayout bg_layout;
    private MessageAdapter myAdapter = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MyUser userInfo = null;
    private Context ctx = null;
    private GetMessage getM = null;
    private JSONArray jsonArray = null;


    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mitView = inflater.inflate(R.layout.activity_message, container, false);
//		ViewGroup vg = (ViewGroup) mitView.getParent();
//		if (vg != null) {
//			vg.removeAllViewsInLayout();
//		}
        return mitView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ctx = getActivity();

        initWiget();

        initData();

        lv.setAdapter(myAdapter);

    }

    private void initData() {

        getM = new GetMessage(ctx);
        userInfo = BmobUser.getCurrentUser(ctx, MyUser.class);

        jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MESSAGE_KEY);
//
        if (null != jsonArray) {

//			getM.callback(new cback());
//			getM.getMessage(userInfo.getObjectId());
//
//		} else {
            bg_layout.setBackgroundColor(Color.WHITE);
            myAdapter.SetJsonArray(jsonArray);
            myAdapter.notifyDataSetChanged();
        }

        getM.callback(new cback());
        getM.getMessage(userInfo.getObjectId());

    }

    private void initWiget() {

        lv = (ListView) mitView.findViewById(R.id.card_listView);
        bg_layout = (LinearLayout) mitView.findViewById(R.id.msg_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) mitView.findViewById(R.id.swipe_refresh);

        myAdapter = new MessageAdapter(getActivity(), lv, new cback());
        swipeRefreshLayout.setOnRefreshListener(this);
//        new ListViewRefreshListener(lv, swipeRefreshLayout, new ListViewRefreshListener.RefreshListener() {
//            @Override
//            public void pushToRefresh() {
//
//            }
//
//            @Override
//            public void pullToRefresh() {//下拉
//                getM.callback(new cback());
//                getM.getMessage(userInfo.getObjectId());
//            }
//        });
        // lv.setOnItemClickListener(new MsgItemOnClickListener());
    }


    @Override
    public void onStart() {
//	 Log.i("xy", "messggeActivity   onStart");

        jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MESSAGE_KEY);

        if (null == jsonArray) {

            getM.callback(new cback());
            getM.getMessage(userInfo.getObjectId());

        } else {
//		if(null!=jsonArray){
            myAdapter.SetJsonArray(jsonArray);
            myAdapter.notifyDataSetChanged();
        }

        super.onStart();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getM.callback(new cback());
        getM.getMessage(userInfo.getObjectId());
    }


    public class cback implements MyCallback {

        @Override
        public void getSuccess(boolean isSuccess) {
            swipeRefreshLayout.setRefreshing(false);

            jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MESSAGE_KEY);
            myAdapter.SetJsonArray(jsonArray);
            myAdapter.notifyDataSetChanged();

            if (jsonArray.length() == 0) {//没有数据
                bg_layout.setBackgroundResource(R.drawable.msgisempty);
            } else {//有数据
                bg_layout.setBackgroundColor(Color.WHITE);

            }

        }

    }

}
