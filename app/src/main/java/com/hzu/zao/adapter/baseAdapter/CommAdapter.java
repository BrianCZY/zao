package com.hzu.zao.adapter.baseAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.hzu.zao.R;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.LogUtils;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * 通用的baseAdapter
 * <p/>
 * Created by yangfujing on 15/10/10.
 */
public abstract class CommAdapter<T> extends BaseAdapter {

    public List<T> dataList;
    public Context ctx;
    public MyUser cuser;
    public ViewHolder holder;

    public CommAdapter(Context context) {
        ctx = context;
        cuser = MyApplication.getInstance().getCUser();

    }

    public void setData(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return dataList;
    }

    @Override
    public int getCount() {

        return dataList != null && dataList.size() > 0 ? dataList.size() : 0;

    }

    @Override
    public T getItem(int position) {

        try {
            return dataList != null && dataList.size() > 0 ? dataList.get(position) : null;
        } catch (ConcurrentModificationException e) {
            LogUtils.e(" ConcurrentModificationException = " + e.toString());
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        holder = ViewHolder.get(ctx, position, getlayoutid(position), convertView, parent);

        convert(holder, getItem(position), position);
        return holder.getConvertView();
    }


    /**
     * 再获取当前用户是否存在
     */
    public void getUser() {

        if (cuser == null) {
            cuser = MyApplication.getInstance().getCUser();
        }
    }

    /**
     * 跳转到详细信息中
     *
     * @param clazz
     * @param bundle
     */
    public void startActivity(Class clazz, Bundle bundle) {
        Intent intent = new Intent(ctx, clazz);
        if (bundle != null) {

            intent.putExtras(bundle);
        }
        ctx.startActivity(intent);
        ((Activity) ctx).overridePendingTransition(R.anim.activity_slid_right_in, R.anim.activity_slid_left_out);
    }

    //adapter获取view实例\绑定数据
    public abstract void convert(ViewHolder holder, T t, int position);

    /**
     * item布局的layoutId
     *
     * @param position
     * @return
     */
    public abstract int getlayoutid(int position);

    public void toast(String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_LONG).show();
    }

    public void toast(int msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_LONG).show();
    }


}
