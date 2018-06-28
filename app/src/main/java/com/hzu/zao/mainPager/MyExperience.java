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

import com.hzu.zao.ExDetailActivity;
import com.hzu.zao.R;
import com.hzu.zao.adapter.MyExAdapter;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.utils.GetUserRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Nearby Yang
 *         <p>
 *         Create at 2015-4-29 下午4:26:09
 */

public class MyExperience implements SwipeRefreshLayout.OnRefreshListener {

    private Context ctx;
    private View v;
    private ListView listView_myEx;
    private MyExAdapter myAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private GetUserRecord getRecord;
    private JSONArray jsonArray = null;
    private RelativeLayout bg_RelativeLayout;

    public MyExperience(Context context, View view) {

        this.v = view;
        this.ctx = context;

        getRecord = new GetUserRecord(context, Contants.EXPERIENCE_TABLE);

    }

    // 初始化控件
    public void initWiget() {
        listView_myEx = (ListView) v.findViewById(R.id.myExper_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_my_ex);
        swipeRefreshLayout.setOnRefreshListener(this);
        listView_myEx.setAdapter(myAdapter);
        listView_myEx.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                JSONObject temp = null;

                int order = jsonArray.length() - position - 1;
                try {
                    temp = jsonArray.getJSONObject(order);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intents = new Intent();
                intents.setClass(ctx, ExDetailActivity.class);

                intents.putExtra("myEx", temp.toString());

                ctx.startActivity(intents);
                ((Activity) ctx).overridePendingTransition(
                        R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);

            }

        });

    }

    // 初始化数据
    public void initData() {
        myAdapter = new MyExAdapter(ctx);
        bg_RelativeLayout = (RelativeLayout) v.findViewById(R.id.myEx_layout);

        jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MYEXPERIENCE_KEY);

        if (null != jsonArray) {

            // Log.i("对象",bg_RelativeLayout+"");
            bg_RelativeLayout.setBackgroundColor(Color.WHITE);

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

    private class callback implements MyCallback {

        @Override
        public void getSuccess(boolean isSuccess) {
            swipeRefreshLayout.setRefreshing(false);
            if (isSuccess) {

                jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MYEXPERIENCE_KEY);
                myAdapter.setData(jsonArray);
                myAdapter.notifyDataSetChanged();

//				Log.i("回调", jsonArray+"   "+ (null != jsonArray));

                if (jsonArray.length() == 0) {

                    bg_RelativeLayout
                            .setBackgroundResource(R.drawable.recisempty);
                } else {
                    bg_RelativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                }

            }
        }

    }
}
