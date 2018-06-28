package com.hzu.zao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.push.BmobPush;
import com.hzu.zao.utils.DateUtils;
import com.hzu.zao.utils.Encrypter;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.view.Dialog4Tips;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends BaseAppCompatActivity {
    /**
     * ID: 登录用户名输入框：login_user_et 登录密码输入框：login_pw_et 忘记密码文本：forget_tx
     * 注册文本：regist_tx 登录按钮：login_bt
     */
    private Button login_bt;
    private EditText login_user_et, login_pw_et;
    private TextView forgetPwd_tx, regist_tx;
    private Intent intents;
    private String times;
    private Date currentDate;
    private Dialog4Tips dialog4Tips;

    //    MyUser myUser;
    private LinearLayout progll;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        setTitle(R.string.login);

        Bmob.getServerTime(mActivity, new GetServerTimeListener() {
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

    }

    @Override
    protected void findView() {
        Bmob.initialize(LoginActivity.this, Contants.APPID);

        login_bt = (Button) findViewById(R.id.login_bt);
        forgetPwd_tx = (TextView) findViewById(R.id.forget_tx);
        regist_tx = (TextView) findViewById(R.id.regist_tx);
        login_user_et = (EditText) findViewById(R.id.login_user_et);
        login_pw_et = (EditText) findViewById(R.id.login_pw_et);
        progll = (LinearLayout) findViewById(R.id.progll1);
        dialog4Tips = new Dialog4Tips(mActivity);
        intents = new Intent();
    }

    @Override
    protected void bindData() {

        login_bt.setOnClickListener(new clickiListener());
        forgetPwd_tx.setOnClickListener(new clickiListener());
        regist_tx.setOnClickListener(new clickiListener());

    }

    @Override
    protected void handerMessage(Message msg) {

    }


    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_login);
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

    // 退出程序
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            startActivity(new Intent(mActivity, MainActivity.class));
            mActivity.finish();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    /**
     * 更新设备id
     */
    private void saveUserInstalltionId(String username) {
        BmobQuery<MyUser> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo(Contants.PARAMS_USER_NAME, username);
        userQuery.findObjects(this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list.size() > 0) {
                    BmobPush.updateinstallationId(LoginActivity.this, list.get(0));

                    MyUser myUser = list.get(0);

                    MyApplication.getInstance().getCacheInstance().put(Contants.CURRENTUSER_KEY, myUser);
                    myUser.setInstallationId(BmobInstallation.getInstallationId(LoginActivity.this));
                    myUser.update(LoginActivity.this, myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            LogUtils.e("更新用户表你 installationId 成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            LogUtils.e("更新用户表 installationId 失败 code = " + i + " message = " + s);
                        }
                    });

                }

            }

            @Override
            public void onError(int i, String s) {
                LogUtils.e("更新installationId 失败 code = " + i + " message = " + s);
            }
        });

    }

    private class clickiListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_bt:

                    if (!TextUtils.isEmpty(login_user_et.getText().toString())) {
                        if (!TextUtils.isEmpty(login_pw_et.getText().toString())) {
                            //判断用户是否能登录
                            userCloudLogin();


                        } else {
                            toast("请输入有效密码");
                        }


                    } else {
                        toast("请输入用户名");
                    }


                    break;
                case R.id.forget_tx:
                    //
                    // GetUserQuestion get = new
                    // GetUserQuestion(LoginActivity.this);
                    // get.getUserQuestion();
                    // //
                    // //
                    // String result =
                    // cacheUtil.ReadJSONArray(Contants.WANTOANS_KEY)
                    // .toString();
                    // Log.i("readJsonArray  ", result + "");

                    intents.setClass(LoginActivity.this, ForgetPwdActivity.class);
                    startActivity(intents);
                    overridePendingTransition(R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);
//                    LoginActivity.this.finish();
                    break;
                case R.id.regist_tx:
                    intents.setClass(LoginActivity.this, RegistActivity.class);
                    startActivity(intents);
                    overridePendingTransition(R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);
//                    LoginActivity.this.finish();
                    break;

            }
        }

    }


    /**
     * 判断用户是否有登录
     */
    private void userCloudLogin() {
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", login_user_et.getText().toString());
        query.findObjects(mActivity, new FindListener<MyUser>() {
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
                                    BmobUser.logOut(mActivity);   //清除缓存用户对象
                                    MyApplication.getInstance().getCacheInstance().remove(Contants.CURRENTUSER_KEY);
                                }

                                @Override
                                public void btnCancelListener() {

                                }
                            });

                            dialog4Tips.show();


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

    /**
     * 登陆模块
     */
    private void login() {
        // Log.i("zao", "login_bt");
        progress("start"); // 开始转菊花
        final String username = login_user_et.getText().toString().trim();
        final String password = login_pw_et.getText().toString().trim();
        final BmobUser bu2 = new BmobUser();
        // Log.i("zao", "2");
        bu2.setUsername(username);
        bu2.setPassword(password);
        // 登陆用户
        bu2.login(LoginActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
//				Log.i("zao", "登陆成功");
                progress("end"); //开始转菊花
                String s = getString(R.string.login_tx2);
                saveUserInstalltionId(username);
                Toast.makeText(getApplicationContext(), s + username,
                        Toast.LENGTH_SHORT).show();

                // 缓存并加密用户密码
                MyApplication.getInstance().getCacheInstance().put(Contants.CURRENTUSERPWD_KEY,
                        Encrypter.encrypt(password));


//                intents =new Intent();
//                setResult(Contants.RESULT_CODE_LOGIN,intents);
                Intent intent = new Intent(Contants.BROADCASTRECEIVER_LOGIN_SUCCESS);
                intent.putExtra(Contants.INTENT_LOGIN_RESULT, true);

//                intent.setAction(Contants.BROADCASTRECEIVER_LOGIN_SUCCESS);
                sendBroadcast(intent);
                LogUtils.e("login success ");

                startActivity(new Intent(LoginActivity.this,
                        MainActivity.class));
                overridePendingTransition(R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);
                LoginActivity.this.finish();
            }

            @Override
            public void onFailure(int code, String msg) {
                // 1);305：用户或密码为空
                // 2);101：用户或密码错误
                // 3);9016：网络不给力
                // Log.i("zao", "错误代码："+code+","+msg);
                progress("end"); //开始转菊花
                if (code == 305) {
                    Toast.makeText(getApplicationContext(), R.string.login_tx1,
                            Toast.LENGTH_SHORT).show();
                } else if (code == 101) {
                    Toast.makeText(getApplicationContext(), R.string.login_tx3,
                            Toast.LENGTH_SHORT).show();
                } else if (code == 9016) {
                    Toast.makeText(getApplicationContext(), R.string.net_error,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error(" + code + ")：" + msg, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    /**
     * 转菊花
     *
     * @param s
     */
    public void progress(String s) {
        if (s.equals("start")) {
            progll.setVisibility(View.VISIBLE);
        } else {
            progll.setVisibility(View.GONE);
        }
    }

}
