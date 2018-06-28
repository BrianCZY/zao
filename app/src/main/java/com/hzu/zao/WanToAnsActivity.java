package com.hzu.zao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hzu.zao.adapter.WanToAnsAdapter;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.utils.GetUserQuestion;
import com.hzu.zao.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 我要回答 异步数据下载
 *
 * @author Nearby Yang
 *         <p/>
 *         Create at 2015-4-29 下午11:17:17
 */
public class WanToAnsActivity extends BaseAppCompatActivity implements XListView.IXListViewListener {

    private XListView listView;
    private WanToAnsAdapter myAdapter;
    private JSONArray dataJsonArray = null;
    private GetUserQuestion getuserQuestion = null;
    private LinearLayout bg_layout;
    private int size = 0;
    private JSONArray myArray = new JSONArray();
    private int animationModel = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_wantoans;
    }

    // =====================初始化控件==============
    @Override
    public void bindData() {

        int number = 0;
        // 实例化Adapter
        myAdapter = new WanToAnsAdapter(WanToAnsActivity.this, listView);

        // 传数据
        // 分页显示，一页只显示10条数据
        if (null != dataJsonArray) {

            size = dataJsonArray.length();

            // Log.i("json isEmpty", ""+(dataJsonArray.length()));

            if (size > 10) {
                // number=10;
                number = size - 10;
                for (int yy = 0; yy < 10; yy++) {

                    try {
                        myArray.put(dataJsonArray
                                .getJSONObject(number + yy));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                myArray = dataJsonArray;
            }

        }

        // myAdapter.setNumber(number);

        myAdapter.setJsonArray(myArray);

        listView.setAdapter(myAdapter);

        // myAdapter.notifyDataSetChanged();
    }

    @Override
    public void findView() {

        // 实例化自定义listView
        listView = (XListView) findViewById(R.id.WanToAns_listView);

        // 设置下拉刷新
        listView.setPullLoadEnable(true);
        listView.setXListViewListener(this);

    }

    // ======================初始数据=================================

    /**
     * 1、下载问题表中数据，包括启动异步下载用户头像。 2、下载的数据分别缓存到本地
     * <p/>
     * 3、缓存部分说明： 1)问题表：WANTOANS_KEY 2)用户头像：url就是头像的名字
     */
    @Override
    public void initData() {
        initializeToolbar();
        setTitle(R.string.wanToAns);
        getuserQuestion = new GetUserQuestion(this);

        // 判断是否有缓存，如果没有缓存就向Bmob请求，有数据就直接读取
        Intent intent = getIntent();
        intent.getClass();

//        Log.i("xy", "wantoAnsActivity    intent.getClass()=" + intent.getClass());
        dataJsonArray = 	MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.WANTOANS_KEY);
        bg_layout = (LinearLayout) findViewById(R.id.wanto_layout);

        if (dataJsonArray == null) {

            getuserQuestion.callback(new Callback());
            getuserQuestion.getUserQuestion();

        } else {
            bg_layout.setBackgroundColor(Color.WHITE);
        }
    }


    @Override
    protected void handerMessage(Message msg) {

    }

    // =====================刷新完成========================
    private void onLoad() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

        String str = formatter.format(curDate);

        listView.stopRefresh();
        listView.stopLoadMore();
        listView.setRefreshTime(str);
    }

    // =====================下拉刷新============================
    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 刷新的数据
                // Log.i("reflash", "onRefresh ");

                getuserQuestion.callback(new Callback());
                getuserQuestion.getUserQuestion();

                onLoad();
            }
        }, 2000);
    }

    // =====================上拉加载更多===========================
    @Override
    public void onLoadMore() {

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                dataJsonArray = 	MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.WANTOANS_KEY);

                myAdapter.setJsonArray(dataJsonArray);


                myAdapter.notifyDataSetChanged();


                onLoad();
            }
        }, 2000);
    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_want_ans);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.finish();

            }
        });

    }

    // ========================回调函数================================
    // 回调函数。等待缓存完成
    private class Callback implements MyCallback {
        int _number = 0;
        int _size = 0;
//		JSONArray jArray = new JSONArray();

        @Override
        public void getSuccess(boolean isSuccess) {

            // Log.i("callback--result", isSuccess + "");

            if (isSuccess) {

                dataJsonArray = 	MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.WANTOANS_KEY);

                if (null != dataJsonArray) {// 返回数据不为空

                    _size = dataJsonArray.length();
                    // 返回的数据大于10条
                    if (size > 10) {
                        // number=10;
                        _number = _size - 11;

                        for (int yy = 0; yy < 10; yy++) {

                            try {
                                myArray.put(yy, dataJsonArray.getJSONObject(_number
                                        + yy));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        myAdapter.setJsonArray(myArray);

                        myAdapter.notifyDataSetChanged();

                    } else {// 不大于10条

                        myAdapter.setJsonArray(dataJsonArray);

                        myAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(WanToAnsActivity.this, R.string.dataIsEmpty,
                            Toast.LENGTH_SHORT).show();
                }


                //背景处理
                if (dataJsonArray.length() == 0) {//没信息的时候

                    bg_layout.setBackgroundResource(R.drawable.recisempty);
                } else {//有信息的时候
                    bg_layout.setBackgroundColor(Color.WHITE);
                }


            } else {
                // Log.i("回调函数", "没有新的数据");

            }
        }

    }

    @Override
    protected void onPause() {
        if (animationModel == 1) {
            overridePendingTransition(R.anim.enter_from_left_animation,
                    R.anim.exit_to_right_animaion);
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            animationModel = 1;

        }
        return super.onKeyDown(keyCode, event);
    }

}
