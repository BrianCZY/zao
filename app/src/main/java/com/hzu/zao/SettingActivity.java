package com.hzu.zao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.v3.BmobUser;


public class SettingActivity extends BaseAppCompatActivity {
    private ImageView cacheIv;
    private Button logOff;
    private AlertDialog dialog;
    private Dialog myDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
        initializeToolbar();
    }

    @Override
    protected void findView() {
        cacheIv = (ImageView) findViewById(R.id.clear_cache);
        logOff = (Button) findViewById(R.id.log_off);
    }

    @Override
    protected void bindData() {

        cacheIv.setOnClickListener(new myOnclickListener());
        logOff.setOnClickListener(new myOnclickListener());
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_about);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, MainActivity.class));
                mActivity.finish();
            }
        });

    }


    public class myOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clear_cache:
                    // Log.i("xy", "setting   clear_cache ");
                    // 显示对话框：取消|确定
                    setAlerdailog();

                    break;
                case R.id.log_off:
                    logoutDialog();

                    break;
                case R.id.dialog_cancel_tv:
                    // 退出对话框
                    dialog.cancel();

                    break;
                case R.id.dialog_sure_tv:
                    removCache();

                    break;

                // 确定注销
                case R.id.logout_sure_tv:
                    myDialog.dismiss();
                    logOff();
                    break;
                // 取消注销
                case R.id.logout_cancel_tv:
                    myDialog.dismiss();
                    break;

                default:
                    break;
            }

        }

    }

    // 用户注销 确认
    public void logoutDialog() {
        TextView cancel;
        TextView config;

        LayoutInflater mlayout = LayoutInflater.from(this);
        View myView = mlayout.inflate(R.layout.logout_dialog, null);

        cancel = (TextView) myView.findViewById(R.id.logout_cancel_tv);
        config = (TextView) myView.findViewById(R.id.logout_sure_tv);

        myDialog = new AlertDialog.Builder(this).setView(myView).create();

        cancel.setOnClickListener(new myOnclickListener());

        config.setOnClickListener(new myOnclickListener());

        myDialog.show();
    }


    // 注销登陆
    public void logOff() {
        // Log.i("xy", "setting   logOff()");
        // 清掉当前用户的信息
        BmobUser.logOut(this);

        MyApplication.getInstance().getCacheInstance().clear();


        Intent ins = new Intent(Contants.BROADCASTRECEIVER_LOGIN_SUCCESS);
        ins.putExtra(Contants.INTENT_LOGOUT_SUCCESS,true);
        mActivity.sendBroadcast(ins);

        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
//        Intent intent = new Intent(Contants.BROADCASTRECEIVER_LOGOUT_SUCCESS);
//        intent.putExtra(Contants.INTENT_LOGIN_RESULT, true);

        overridePendingTransition(R.anim.enter_from_right_animation,
                R.anim.exit_to_left_animation);
        this.finish();

//		EnterActivity.enterActivityContext.finish(); // 通过静态变量来销毁上一级Activity
        // int nPid = android.os.Process.myPid();
        // android.os.Process.killProcess(nPid);
//        this.finish();
//        System.exit(0);
    }


    public void removCache() {
        MyApplication.getInstance().getCacheInstance().remove(Contants.EXPERIENCE_KEY);
        MyApplication.getInstance().getCacheInstance().remove(Contants.MESSAGE_KEY);
        MyApplication.getInstance().getCacheInstance().remove(Contants.MYANSWER_KEY);
        MyApplication.getInstance().getCacheInstance().remove(Contants.MYEXPERIENCE_KEY);
        MyApplication.getInstance().getCacheInstance().remove(Contants.MYQUESTION_KEY);
        MyApplication.getInstance().getCacheInstance().remove(Contants.RECOMMENDED_KEY);
        // util.Remove(Contants.USEHEADSCULPTURE_KEY);
        MyApplication.getInstance().getCacheInstance().remove(Contants.USERICONURL_KEY);
        MyApplication.getInstance().getCacheInstance().remove(Contants.USERINFO_KEY);
        MyApplication.getInstance().getCacheInstance().remove(Contants.WANTOANS_KEY);
        //清除缓存
        ImageLoader.getInstance().clearDiskCache();

        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.clear_success), Toast.LENGTH_LONG).show();
        dialog.cancel();
    }

    public void setAlerdailog() {
        // Log.i("xy", "setting   setAlerdailog()  ");
        dialog = new AlertDialog.Builder(SettingActivity.this).create();
        dialog.show();
        Window win = dialog.getWindow();
        win.setContentView(R.layout.clear_cache_dialog);

        TextView cancel = (TextView) win.findViewById(R.id.dialog_cancel_tv);
        TextView sure = (TextView) win.findViewById(R.id.dialog_sure_tv);

        cancel.setOnClickListener(new myOnclickListener());
        sure.setOnClickListener(new myOnclickListener());

    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.enter_from_bottom_animtion,
                R.anim.exit_to_top_animation);
        super.onPause();
    }
}
