package com.hzu.zao.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hzu.zao.utils.LogUtils;


/**
 * 原生viewpager存在问题：当数目比较多的时候会出现指针越界的异常，崩溃，是属于系统级bug，
 * 为了防止捕获这个bug使用try catch
 * 定义viewpager
 * <p/>
 * 来自大神的提醒
 * <p/>
 * Created by Administrator on 2016/1/29.
 */
public class CustomViewPager extends ViewPager {

    //设置viewpager外缓存的item数目
    private static final int ITEM_COUNT = 3 ;
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOffscreenPageLimit(ITEM_COUNT);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            LogUtils.e("viewpager IllegalArgumentException =" + e);
        } catch (ArrayIndexOutOfBoundsException e) {
            LogUtils.e("viewpager ArrayIndexOutOfBoundsException =" + e);
        }

        return false;
    }


}
