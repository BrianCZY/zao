package com.hzu.zao.mainPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hzu.zao.R;
import com.hzu.zao.WanToAnsDetailActivity;
import com.hzu.zao.adapter.MyAnswerAdapter;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.utils.GetUserRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyAnswer implements SwipeRefreshLayout.OnRefreshListener {

    private Context ctx;
    private View v;
    private ListView listView_myAns;
    private MyAnswerAdapter myAdapter;
    private GetUserRecord getRecord;
    private JSONArray jsonArray;
    private RelativeLayout bg_layout;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MyAnswer(Context ctx, View view) {
        this.ctx = ctx;
        this.v = view;

        getRecord = new GetUserRecord(ctx, Contants.MYANSWER_TABLE);
    }

    // 初始化控件
    public void initView() {
        listView_myAns = (ListView) v.findViewById(R.id.myAnswer_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_my_ans);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView_myAns.setAdapter(myAdapter);
        listView_myAns.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                JSONObject tempObj = null;

                int order = jsonArray.length() - position - 1;

                try {
                    tempObj = jsonArray.getJSONObject(order);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intents = new Intent();
                intents.setClass(ctx, WanToAnsDetailActivity.class);
                intents.putExtra("myAns", tempObj.toString());
                ctx.startActivity(intents);
                ((Activity) ctx).overridePendingTransition(
                        R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);

            }
        });

    }

    // 初始化数据
    public void initData() {
        myAdapter = new MyAnswerAdapter(ctx);
        bg_layout = (RelativeLayout) v.findViewById(R.id.myAns_layout);


        jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MYANSWER_KEY);
        if (null != jsonArray) {

            bg_layout.setBackgroundColor(Color.WHITE);
            myAdapter.setData(jsonArray);
        }


        getRecord.myCallback(new callback());
        getRecord.AsyncCode();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getRecord.myCallback(new callback());
        getRecord.AsyncCode();
    }

    // 回调函数
    private class callback implements MyCallback {

        @Override
        public void getSuccess(boolean isSuccess) {
            swipeRefreshLayout.setRefreshing(false);
            if (isSuccess) {

                jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MYANSWER_KEY);

                myAdapter.setData(jsonArray);
                myAdapter.notifyDataSetChanged();
                if (jsonArray.length() == 0) {
                    bg_layout.setBackgroundResource(R.drawable.recisempty);
                } else {
                    bg_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                }

            } else {
                Toast.makeText(ctx, R.string.isNewAns, Toast.LENGTH_SHORT)
                        .show();
                // Log.i("callback","没有数据");
            }
        }

    }

}
