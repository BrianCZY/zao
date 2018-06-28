package com.hzu.zao.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hzu.zao.R;
import com.hzu.zao.annotation.Injector;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.MyUser;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * 基类 v7 兼容activity
 * 有actionBar，并且有点击事件
 * 使用注释
 * <p/>
 * Created by Nearby Yang on 2015-10-08.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {


    public Activity mActivity;
    public Intent intents;
    public MyApplication app;
    public MyUser cuser;

    private final static int TYPE_BACK = 0x1;
    private final static int TYPE_DEFUAL = 0x0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());
        Injector.inject(this);
        mActivity = this;
        app = MyApplication.getInstance();
        cuser = app.getCUser();
        //沉浸式
        setTranslucentStatus();

        initData();
        findView();
        bindData();
    }


    /**
     * 沉浸式
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus() {
        boolean on = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            on = true;
        }

        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }

        win.setAttributes(winParams);


        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
//设置沉浸的颜色
        tintManager.setStatusBarTintResource(R.color.green);


    }

    /**
     * 获取根目录的布局
     */
    public View getRootView() {
        return LayoutInflater.from(this).inflate(getLayoutId(), null);
    }


    /**
     * 跳转界面，不带参数的
     *
     * @param clazz 要跳转的类，也就是要传递参数的类
     */
    public void mStartActivity(Class clazz) {
        mStartActivity(clazz, null, TYPE_DEFUAL, null);
    }

    /**
     * 跳转界面，带参数的
     *
     * @param clazz
     * @param bundle
     */
    public void mStartActivity(Class clazz, Bundle bundle) {


        mStartActivity(clazz, bundle, TYPE_DEFUAL, null);
    }

    /**
     * 跳转界面，带参数的,参数标识tag
     *
     * @param clazz
     * @param bundle
     * @param bundleTag
     */
    public void mStartActivity(Class clazz, Bundle bundle, String bundleTag) {

        mStartActivity(clazz, bundle, TYPE_DEFUAL, bundleTag);
    }


    /**
     * 返回上一级界面，不带参数
     *
     * @param clazz
     */
    public void mBackStartActivity(Class clazz) {
        mStartActivity(clazz, null, TYPE_BACK, null);
    }


    /**
     * 跳转界面，带参数的
     *
     * @param clazz  要跳转的类，也就是要传递参数的类
     * @param bundle serializable
     */
    public void mStartActivity(Class clazz, Bundle bundle, int type, String bundleTag) {

        Intent intent = new Intent();
        intent.setClass(this, clazz);

        if (bundle != null) {
            if (TextUtils.isEmpty(bundleTag)) {
                intent.putExtras(bundle);
            } else {
                intent.putExtra(bundleTag, bundle);
            }
        }

        startActivity(intent);
        if (type == TYPE_BACK) {
            overridePendingTransition(R.anim.activity_slid_left_in, R.anim.activity_slid_right_out);
        } else {

            overridePendingTransition(R.anim.activity_slid_right_in, R.anim.activity_slid_left_out);
        }
    }

    /**
     * toast
     *
     * @param strId 文字id
     */
    public void toast(int strId) {
        Toast.makeText(this, strId, Toast.LENGTH_LONG).show();
    }

    /**
     * toast
     *
     * @param str 文字id
     */
    public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    /**
     * 点击事件的回调
     */
    public interface mActionBarOnClickListener {
        void onClick(View v);

    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handerMessage(msg);
        }
    };

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                && event.getAction() != KeyEvent.ACTION_UP) {
//
//        }
//
//
//        return super.dispatchKeyEvent(event);
//    }


    //主界面layout的id
    protected abstract int getLayoutId();

    //初始化数据
    protected abstract void initData();

    //获取控件实例
    protected abstract void findView();

    //绑定数据到空间中
    protected abstract void bindData();


    protected abstract void handerMessage(Message msg);

    /**
     * 简化findviewbyid
     *
     * @param viewID
     * @param <T>
     * @return
     */
    protected <T> T $(int viewID) {
        return (T) findViewById(viewID);
    }


}
