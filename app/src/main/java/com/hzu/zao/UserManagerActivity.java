package com.hzu.zao;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.hzu.zao.adapter.UserManagerAdapter;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.interfaces.ListViewRefreshListener;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.view.CusProcessDialog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 管理员 用户的管理
 * Created by Nearby Yang on 2016-04-25.
 */
public class UserManagerActivity extends BaseAppCompatActivity implements ListViewRefreshListener.RefreshListener {

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<MyUser> dataList;
    private ProgressDialog progressDialog;

    private UserManagerAdapter userManagerAdapter;
    private BmobQuery<MyUser> userBmobQuery;
    private boolean getMore = false;//上拉刷新
    private int limit = 40;
    private int pageSize = 20;
    private int endIndex = 20;
    private int startIndex = 0;
    private int PUSH_TIMES = 0;//下拉次数

    private static final int FIRST_GET_DATA = 0x1001;
    private static final int GET_LOACTIOPN_DATA = 0x1002;
    private static final int HANDLER_GET_DATA = 0x1003;
    private static final int MESSAGE_NO_MORE_DATA = 0x1004;
    private static final int MESSAGE_LOAD_DATA_FAILURE = 0x1005;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_manager;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        setTitle(R.string.txt_root_user_manager);
        userBmobQuery = new BmobQuery<>();
        userBmobQuery.setLimit(limit);
        userBmobQuery.addWhereNotEqualTo("type", "root");
        userBmobQuery.findObjects(this, new FindUserListener());
    }

    @Override
    protected void findView() {
        listView = $(R.id.ls_user_manager);
        swipeRefreshLayout = $(R.id.swipe_refresh_manager);

        userManagerAdapter = new UserManagerAdapter(this);
        progressDialog = CusProcessDialog.commenProgressDialog(this);
    }

    @Override
    protected void bindData() {
        new ListViewRefreshListener(listView, swipeRefreshLayout, this);

        listView.setAdapter(userManagerAdapter);
        progressDialog.show();
    }

    @Override
    protected void handerMessage(Message msg) {
        switch (msg.what) {

            case FIRST_GET_DATA:
                //刷新列表数据
                refreshUI();
                progressDialog.dismiss();
                break;
        }
    }

    /**
     * 刷新列表，最新数据
     */
    private void refreshUI() {

        if (getMore) {
            int tempEnd = endIndex;
            endIndex = dataList.size() < (PUSH_TIMES + 1) * pageSize ?
                    dataList.size() : (PUSH_TIMES + 1) * pageSize;

            userManagerAdapter.getData().addAll(dataList.subList(tempEnd, endIndex));
            userManagerAdapter.notifyDataSetChanged();

        } else {
            endIndex = dataList.size() < pageSize ? dataList.size() : endIndex;

            userManagerAdapter.setData(dataList.subList(startIndex, endIndex));

        }

        //停止刷新动画
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void pushToRefresh() {//上拉
        if (dataList.size() > pageSize * PUSH_TIMES) {

            endIndex = dataList.size() < pageSize +
                    pageSize * PUSH_TIMES ? dataList.size() :
                    pageSize + pageSize * PUSH_TIMES;

            userManagerAdapter.setData(dataList.subList(startIndex, endIndex));

            PUSH_TIMES++;
            swipeRefreshLayout.setRefreshing(false);
        } else {
            getMore = true;
//        Toast.makeText(ctx, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
            startIndex = dataList.size();

            if (dataList.size() >= limit) {
                userBmobQuery.addWhereNotEqualTo("username", "rootApp");
                userBmobQuery.setSkip(startIndex);
                userBmobQuery.setLimit(limit);
                userBmobQuery.findObjects(this, new FindUserListener());
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }


    }

    @Override
    public void pullToRefresh() {//下拉
        getMore = false;
        PUSH_TIMES = 1;
        startIndex = 0;

        initData();
    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_user_manager);
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

    /**
     * 查找用户的回调
     */
    public class FindUserListener extends FindListener<MyUser> {

        @Override
        public void onSuccess(List<MyUser> list) {
            LogUtils.d(" list size = " + list.size());
            swipeRefreshLayout.setRefreshing(false);
            if (getMore) {
                if (dataList == null) {
                    dataList = new ArrayList<>();
                }
                dataList.addAll(list);
            } else {
                dataList = list;
            }

            mHandler.sendEmptyMessage(FIRST_GET_DATA);
        }

        @Override
        public void onError(int i, String s) {
            swipeRefreshLayout.setRefreshing(false);
            LogUtils.e("查找全部用户失败 code = " + i + " message = " + s);
        }
    }
}
