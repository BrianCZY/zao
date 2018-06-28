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

import com.hzu.zao.R;
import com.hzu.zao.WanToAnsDetailActivity;
import com.hzu.zao.adapter.MyQuestionAdapter;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.utils.GetUserRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 用户记录---我的提问
 *
 * @author Nearby Yang
 *         <p>
 *         Create at 2015 下午3:02:26
 */
public class MyQuestion implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private View myView = null;
    private ListView listView_myQues;
    private SwipeRefreshLayout swipeRefreshLayout;

    private GetUserRecord getRecord;
    private JSONArray jsonArray;
    private MyQuestionAdapter myAdapter;
    private RelativeLayout bg_layout;

    // 构造
    public MyQuestion(Context context, View v) {
        this.context = context;
        this.myView = v;

        getRecord = new GetUserRecord(context, Contants.MYQUESTION_TABLE);
    }

    // 初始化控件
    public void initView() {
        listView_myQues = (ListView) myView.findViewById(R.id.myQues_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_refresh_my_ques);
        swipeRefreshLayout.setOnRefreshListener(this);
        listView_myQues.setAdapter(myAdapter);
        listView_myQues.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int order = jsonArray.length() - position - 1;

                JSONObject tempObj = null;

                try {
                    tempObj = jsonArray.getJSONObject(order);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intents = new Intent();
                intents.setClass(context, WanToAnsDetailActivity.class);
                intents.putExtra("myQuest", tempObj.toString());
                context.startActivity(intents);
                ((Activity) context).overridePendingTransition(
                        R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);

            }
        });

    }

    // 初始化数据
    public void initData() {
        myAdapter = new MyQuestionAdapter(context);
        bg_layout = (RelativeLayout) myView.findViewById(R.id.myQues_layout);

        jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MYQUESTION_KEY);

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

    // 回调函数，等待数据更新
    private class callback implements MyCallback {

        @Override
        public void getSuccess(boolean isSuccess) {
            swipeRefreshLayout.setRefreshing(false);
            if (isSuccess) {

                jsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.MYQUESTION_KEY);
                myAdapter.setData(jsonArray);

                myAdapter.notifyDataSetChanged();
                if (jsonArray.length() == 0) {
                    bg_layout.setBackgroundResource(R.drawable.recisempty);
                } else {
                    bg_layout.setBackgroundColor(Color.WHITE);

                }

            }
//            else {
//                Toast.makeText(context, R.string.isNewQuest, Toast.LENGTH_SHORT)
//                        .show();
//            }
        }

    }

}
