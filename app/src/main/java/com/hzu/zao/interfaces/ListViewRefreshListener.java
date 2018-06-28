package com.hzu.zao.interfaces;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * 上拉加载的滑动监听器
 * <p/>
 * Created by Nearby Yang on 2015-11-15.
 */
public class ListViewRefreshListener implements AbsListView.OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener {

    private boolean isLastItem = false;
    private RefreshListener refreshlistener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    public ListViewRefreshListener(ListView listView, SwipeRefreshLayout swipeRefreshLayout,
                                   RefreshListener refreshlistener) {
        this.listView = listView;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.refreshlistener = refreshlistener;

        listView.setOnScrollListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (isLastItem) {
                swipeRefreshLayout.setRefreshing(true);
                if (refreshlistener != null) {
                    refreshlistener.pushToRefresh();
                }

            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isLastItem = (totalItemCount - 1) == listView.getLastVisiblePosition();
//        LogUtils.logD("totalitemCount = " + totalItemCount + " lastItemcount = " + listView.getLastVisiblePosition());
    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);
        if (refreshlistener != null) {
            refreshlistener.pullToRefresh();
        }

    }

    /**
     * 刷新回调
     */
    public interface RefreshListener {
        //上拉刷新
        void pushToRefresh();

        //下拉刷新
        void pullToRefresh();

    }
}

