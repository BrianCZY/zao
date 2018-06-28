package com.hzu.zao.adapter.baseAdapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * listview 的通用viewholder
 * Created by yangfujing on 15/10/10.
 */
public class ViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public ViewHolder(Context context, int position, int layoutId, ViewGroup parent) {
        mPosition = position;
        mViews = new SparseArray<>();

        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 入口方法
     * @param context
     * @param position
     * @param layoutId
     * @param convertView
     * @param parent
     * @return viewholder
     */
    public static ViewHolder get(Context context, int position, int layoutId, View convertView, ViewGroup parent) {
        if (null == convertView) {
            return new ViewHolder(context, position, layoutId, parent);

        } else {

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
            return viewHolder;

        }


    }

    /**
     * 通过viewid获取view
     *
     * @param viewId view的id
     * @param <T>    泛型 view
     * @return 当前id对应的view实例
     */
    public <T extends View> T getView(int viewId) {
        View views = mViews.get(viewId);

        if (views == null) {
            views = mConvertView.findViewById(viewId);
            mViews.put(viewId, views);
        }

        return (T) views;
    }

    //获取viewholder
    public View getConvertView() {
        return mConvertView;
    }
}
