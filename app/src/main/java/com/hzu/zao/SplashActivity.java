package com.hzu.zao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.DateUtils;
import com.hzu.zao.utils.Encrypter;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.view.Dialog4Tips;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.SaveListener;

public class SplashActivity extends Activity {
    private Dialog4Tips dialog4Tips;
    private Date currentDate;
    private String times;
    private Intent intent = new Intent();
    private MyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        dialog4Tips = new Dialog4Tips(this);

//		Log.i("xy", "0.05.11.2");

        Bmob.initialize(this, Contants.APPID); // 初始化bmob
        BmobInstallation.getCurrentInstallation(this).save();
        BmobPush.startWork(this, Contants.APPID);

        Bmob.getServerTime(this, new GetServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                currentDate = new Date(time * 1000L);
                times = formatter.format(currentDate);
                LogUtils.i("bmob", "当前服务器时间为:" + times);
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.i("bmob", "获取服务器时间失败:" + msg);
            }
        });

        starLayout();
        // Intent intent=new Intent(getApplicationContext(),
        // LoginActivity.class);
        // startActivity(intent);
        // this.finish();
    }


    /**
     * 判断用户是否有登录
     */
    private void userCloudLogin(String username) {
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {

                if (list.size() > 0) {
                    MyUser myUser = list.get(0);
                    if (myUser.getLimitDate() != null) {
                        int hour = DateUtils.hourBetween(times, myUser.getLimitDate().getDate());
                        LogUtils.e("hour = " + hour);
                        if (hour > 0) {

                            dialog4Tips.setContent("你已经被管理员禁止登录\n" + String.format("还需要%d小时候才能登录", hour));
                            dialog4Tips.getBtnCancel().setVisibility(View.GONE);
                            dialog4Tips.setDialogListener(new Dialog4Tips.Listener() {
                                @Override
                                public void btnOkListenter() {
                                    enterHome();
                                    user = null;
                                }

                                @Override
                                public void btnCancelListener() {

                                }
                            });

                            dialog4Tips.show();

                            BmobUser.logOut(SplashActivity.this);   //清除缓存用户对象
                            MyApplication.getInstance().getCacheInstance().remove(Contants.CURRENTUSER_KEY);

                        } else {
                            //可以登录
                            login();
                        }
                    } else {
                        //可以登录
                        login();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.e("查找失败 code = " + i + "message = " + s);
            }
        });
//        login();


    }

    // 欢迎界面
    private void starLayout() {


        new Handler().postDelayed(new Runnable() {

            public void run() {


                // 判断是否有Bmob账户信息缓存
                // final BmobUser user =
                // BmobUser.getCurrentUser(SplashActivity.this);
                user = MyApplication.getInstance().getCUser();
                String pwd = MyApplication.getInstance().getCacheInstance().getAsString(Contants.CURRENTUSERPWD_KEY);

                if (user != null && pwd != null) {

                    String password = Encrypter.decrypt(pwd);
                    user.setPassword(password);
                    // 登录
                    userCloudLogin(user.getUsername());

                } else {
                    enterHome();
                }
            }

        }, 2000);
    }


    /**
     * 进入主界面
     */
    private void enterHome() {
        intent.setClass(SplashActivity.this, MainActivity.class);// 跳转到登录界面
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
        // 平滑过渡特效
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    /**
     * 登录
     */
    private void login() {

        user.login(SplashActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
                MyUser myUser = BmobUser.getCurrentUser(
                        SplashActivity.this, MyUser.class);// 获取本地用户
                MyApplication.getInstance().getCacheInstance().put(Contants.CURRENTUSER_KEY, myUser);
                String s = getString(R.string.auto_login);

                Toast.makeText(getApplicationContext(),
                        s + user.getUsername(), Toast.LENGTH_SHORT)
                        .show();

                intent.setClass(SplashActivity.this,
                        MainActivity.class);// 跳转到主界面

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
                // 平滑过渡特效
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }

            @Override
            public void onFailure(int code, String msg) {
//							Log.i("zao", "错误代码：" + code + "," + msg);
                if (code == 305) {
                    Toast.makeText(getApplicationContext(),
                            R.string.login_tx1, Toast.LENGTH_SHORT)
                            .show();
                } else if (code == 101) {
                    Toast.makeText(getApplicationContext(),
                            R.string.login_tx3, Toast.LENGTH_SHORT)
                            .show();
                } else if (code == 9016) {
                    Toast.makeText(getApplicationContext(),
                            R.string.net_error, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error(" + code + ")：" + msg,
                            Toast.LENGTH_SHORT).show();
                }
                intent.setClass(SplashActivity.this,
                        LoginActivity.class);// 跳转到登录界面
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
                // 平滑过渡特效
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }
        });
    }

}
