package com.hzu.zao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.fragment.HomeFragment;
import com.hzu.zao.fragment.MessageFragment;
import com.hzu.zao.fragment.PersonalFragment;
import com.hzu.zao.fragment.QuesFragment;
import com.hzu.zao.interfaces.JsonCallback;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.GetMessage;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.utils.RealTimeData;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;


public class MainActivity extends BaseAppCompatActivity implements OnClickListener {

    private HomeFragment homeFragment = new HomeFragment();
    private Fragment messageFragment = new MessageFragment();
    private Fragment quesFrament = new QuesFragment();
    private PersonalFragment personalFrament = new PersonalFragment();
    private Fragment from;


    private ImageView homeBt, quesBt, mesBt, personBt;
    private ImageView msgTipsIm;
    private Dialog myDialog;
    private GetMessage getM = null;
    private boolean loginOut;

    private static final String FRAGMENT_HOME = "fragment_home";
    private static final String FRAGMENT_MESSAGE = "fragment_message";
    private static final String FRAGMENT_QUESTION = "fragment_question";
    private static final String FRAGMENT_PERSONAL = "fragment_personal";



    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        getM = new GetMessage(this);
    }

    @Override
    protected void findView() {
        // 点击有效区域
//		LinearLayout quesL = (LinearLayout) findViewById(R.id.quesL);
//		LinearLayout mesL = (LinearLayout) findViewById(R.id.mesL);
//		LinearLayout personL = (LinearLayout) findViewById(R.id.personL);
//		LinearLayout homeL = (LinearLayout) findViewById(R.id.homeL);

        // 信息提示
        msgTipsIm = (ImageView) findViewById(R.id.msgtip_iv);

        // 显示的效果
        homeBt = (ImageView) findViewById(R.id.homeBt);
        quesBt = (ImageView) findViewById(R.id.quesBt);
        mesBt = (ImageView) findViewById(R.id.mesBt);
        personBt = (ImageView) findViewById(R.id.personBt);
        from = homeFragment;
    }

    @Override
    protected void bindData() {
        //切换到主界面
        initFragmant();
//获取最新数据
        updateData();
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    /**
     * 判断用户是否登陆
     *
     * @return 1, 用户已登陆, return true ; 2,用户未登陆,return false
     */
    public boolean judgeLogin() {
        // Log.i("xy", "MainActivity   judgeLogin1");

        MyUser user = MyApplication.getInstance().getCUser();
//        LogUtils.d("本地用户 = " + user);
        // user = BmobUser.getCurrentUser(MainActivity.this, MyUser.class);
        if (user != null) {
            return true;

        } else {

            loginDialog();
            return false;
        }


    }

    // 登陆确认
    public void loginDialog() {

        TextView cancel;
        TextView config;

        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.login_dialog, null);

        cancel = (TextView) myView.findViewById(R.id.login_cancel_tv);
        config = (TextView) myView.findViewById(R.id.login_sure_tv);

        myDialog = new AlertDialog.Builder(this).setView(myView).create();

        cancel.setOnClickListener(this);

        config.setOnClickListener(this);

        myDialog.show();

    }

    /**
     * 实时更新数据
     */
    private void updateData() {

        // ====评论=======
        RealTimeData real = new RealTimeData(this);
        real.getNewJsonArray(new isUpdate());
        // 评论列表
        real.realTimeData(Contants.MYANSWER_TABLE);

        // =====回复=========
        RealTimeData real1 = new RealTimeData(this);
        real1.getNewJsonArray(new isUpdate());
        // 回复列表
        real1.realTimeData(Contants.REPLY_TABLE);

        //注册广播接收者
        registLoginBroadcastReceiver();


        // 如需要在任意网络环境下都进行更新自动提醒，则请在update调用之前添加以下代码
        BmobUpdateAgent.setUpdateOnlyWifi(false);
//        BmobUpdateAgent.update(this);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                //根据updateStatus来判断更新是否成功

                LogUtils.i("updateStatus = " + updateStatus + " updateInfo = " + updateInfo.toString());
            }
        });
    }

    /**
     * 注册登录的广播接收者
     */
    private void registLoginBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Contants.BROADCASTRECEIVER_LOGIN_SUCCESS);
        registerReceiver(loginBroadReceiver, intentFilter);
    }

    /**
     * 登陆的广播接收者
     */
    private BroadcastReceiver loginBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LogUtils.e("BroadcastReceiver intent = " + intent.getAction());
//            toast(intent.getAction());

            if (Contants.BROADCASTRECEIVER_LOGIN_SUCCESS.equals(intent.getAction())) {

                boolean loginSuccess = intent.getBooleanExtra(Contants.INTENT_LOGIN_RESULT, false);
                loginOut = intent.getBooleanExtra(Contants.INTENT_LOGOUT_SUCCESS, false);

                LogUtils.e("onActivityResult loginSuccess = " + loginSuccess + " loginOut = " + loginOut);

                if (loginSuccess) {
                    homeFragment.refreshData();
                    personalFrament.setRefresh();
                }


            }


        }
    };

    /**
     * 切换到主界面
     */
    private void switchHome() {
        homeBt.setImageResource(R.drawable.home_true);
        quesBt.setImageResource(R.drawable.ques_false);
        mesBt.setImageResource(R.drawable.mes_false);
        personBt.setImageResource(R.drawable.user_false);
        //刷新一下数据
        homeFragment.refreshData();

        switchFrament(homeFragment, FRAGMENT_HOME);
        from = homeFragment;

    }


    /**
     * 初始化，第一个是home
     */
    private void initFragmant() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.add(R.id.mainFragment, homeFragment).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeL:
                homeBt.setImageResource(R.drawable.home_true);
                quesBt.setImageResource(R.drawable.ques_false);
                mesBt.setImageResource(R.drawable.mes_false);
                personBt.setImageResource(R.drawable.user_false);
                switchFrament(homeFragment, FRAGMENT_HOME);
                from = homeFragment;
                break;

            case R.id.quesL:

                if (judgeLogin()) {// 如果用户没登陆则跳转到登陆界面

                    homeBt.setImageResource(R.drawable.home_false);
                    quesBt.setImageResource(R.drawable.ques_true);
                    mesBt.setImageResource(R.drawable.mes_false);
                    personBt.setImageResource(R.drawable.user_false);
                    switchFrament(quesFrament, FRAGMENT_QUESTION);
                    from = quesFrament;
                }

                break;
            case R.id.mesL:
                if (judgeLogin()) {// 如果用户没登陆则跳转到登陆界面
                    homeBt.setImageResource(R.drawable.home_false);
                    quesBt.setImageResource(R.drawable.ques_false);
                    mesBt.setImageResource(R.drawable.mes_true);
                    personBt.setImageResource(R.drawable.user_false);
                    msgTipsIm.setVisibility(View.INVISIBLE);
                    switchFrament(messageFragment, FRAGMENT_MESSAGE);
                    from = messageFragment;
                }
                break;
            case R.id.personL:
                if (judgeLogin()) {// 如果用户没登陆则跳转到登陆界面
                    homeBt.setImageResource(R.drawable.home_false);
                    quesBt.setImageResource(R.drawable.ques_false);
                    mesBt.setImageResource(R.drawable.mes_false);
                    personBt.setImageResource(R.drawable.user_true);
                    switchFrament(personalFrament, FRAGMENT_PERSONAL);
                    from = personalFrament;
                }

                break;
            //不登陆
            case R.id.login_cancel_tv:
                myDialog.dismiss();
                break;

            case R.id.login_sure_tv:

                myDialog.dismiss();


                startActivityForResult(new Intent(getApplicationContext(),
                        LoginActivity.class), Contants.REQUEST_CODE_LOGIN);

                overridePendingTransition(R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);

                this.finish();


                break;

            default:
                break;
        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        LogUtils.e("onActivityResult resultCode = " + resultCode);
//        if (requestCode == Contants.RESULT_CODE_LOGIN) {
//            boolean loginSuccess = data.getBooleanExtra(Contants.INTENT_LOGIN_RESULT, false);
//            LogUtils.e("onActivityResult loginSuccess = " + loginSuccess);
//            if (loginSuccess) {
//                homeFragment.onActivityResult(requestCode, Contants.INTENT_LOGIN_SUCCESS, data);
//            }
//        }
//
//
//    }

    public void switchFrament(Fragment to, String tag) {


        if (from != to) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            if (!to.isAdded()) {    // 先判断是否被add过
                fragmentTransaction.hide(from).add(R.id.mainFragment, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                fragmentTransaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }

    }

    // 退出程序
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(
                    MainActivity.this);
            alertbBuilder
                    .setTitle("真的要退出？")
                    .setMessage("你确定要离开吗?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    int nPid = android.os.Process.myPid();
                                    android.os.Process.killProcess(nPid);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }
                            }).create();
            alertbBuilder.show();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    /**
     * 单例模式
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public Context getContext() {
        return MainActivity.this;
    }

    // 回调
    private class isUpdate implements JsonCallback {

        private String createdAt;
        private String user_id;
        private long diff;

        @Override
        public void jsonCallb(JSONObject jsonObj) {

            // Log.i("更新数据", "" + jsonObj.toString());

            if (null != jsonObj) {

                createdAt = jsonObj.optString("createdAt");

                user_id = jsonObj.optString("toWho");

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    Date d1 = df.parse(createdAt);

                    Date curDate = new Date(System.currentTimeMillis() - 3000);// 获取当前时间


                    diff = curDate.getTime() - d1.getTime();

                    // Log.i("对比时间", " 当前 - 新的 " + diff);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//				Log.i("null", "diff "+diff+" userInfo "+userInfo);
                MyUser userInfo = MyApplication.getInstance().getCUser();
                // 时间差
                if ((diff < 0) && (user_id.equals(userInfo.getObjectId()))) {

                    getM.ReflashMsg(userInfo.getObjectId());

                    msgTipsIm.setVisibility(View.VISIBLE);

                }

            }

        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (loginOut) {
            switchHome();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除登陆的广播接收者
        unregisterReceiver(loginBroadReceiver);
    }
}
