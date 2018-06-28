package com.hzu.zao.adapter.baseAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * pageradapter  的基类
 * <p/>
 * Created by Administrator on 2016/1/29.
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {

    public Context context;
    public List<T> dataList;
    private View mCurrentView;

    public BasePagerAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<T> getDataList(){
        return dataList;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View) object;
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }

    @Override
    public int getCount() {
        return dataList != null && dataList.size() > 0 ? dataList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 移除在2个以外的item
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
         View v = LayoutInflater.from(context).inflate(getLayout(), container, false);

        instanceItem(v, dataList.get(position), position);

        container.addView(v);
        return v;
    }


    protected abstract int getLayout();

    protected abstract void instanceItem(View v, T t, int position);
}
