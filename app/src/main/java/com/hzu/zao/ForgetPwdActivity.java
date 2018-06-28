package com.hzu.zao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
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
import com.hzu.zao.utils.Encrypter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class ForgetPwdActivity extends BaseAppCompatActivity {

    LinearLayout forget_layout1, forget_layout2, forget_layout3;
    EditText forget_user_et, forget_answer_et, forget_pw_et, forget_pwcheck_et;
    Button forget_user_bt, forget_answer_bt, forget_finish_bt;
    TextView forget_user1_tx, forget_user2_tx, forget_ques_tx;
    MyUser user;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        setTitle(R.string.forget_findpw);
    }

    @Override
    protected void findView() {
        forget_layout1 = (LinearLayout) findViewById(R.id.forget_layout1);
        forget_layout2 = (LinearLayout) findViewById(R.id.forget_layout2);
        forget_layout3 = (LinearLayout) findViewById(R.id.forget_layout3);

        forget_user_et = (EditText) findViewById(R.id.forget_user_et);
        forget_answer_et = (EditText) findViewById(R.id.forget_answer_et);
        forget_pw_et = (EditText) findViewById(R.id.forget_pw_et);
        forget_pwcheck_et = (EditText) findViewById(R.id.forget_pwcheck_et);

        forget_user_bt = (Button) findViewById(R.id.forget_user_bt);
        forget_answer_bt = (Button) findViewById(R.id.forget_answer_bt);
        forget_finish_bt = (Button) findViewById(R.id.forget_finish_bt);

        forget_user1_tx = (TextView) findViewById(R.id.forget_user1_tx);
        forget_user2_tx = (TextView) findViewById(R.id.forget_user2_tx);
        forget_ques_tx = (TextView) findViewById(R.id.forget_ques_tx);
    }

    @Override
    protected void bindData() {
        forget_user_bt.setOnClickListener(new onclicklistener());
        forget_answer_bt.setOnClickListener(new onclicklistener());
        forget_finish_bt.setOnClickListener(new onclicklistener());
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_forget);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

    }


    //查询用户界面
    private void checkUser() {
        String username = forget_user_et.getText().toString();
        BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
        bq.addWhereEqualTo("username", username);
        bq.findObjects(ForgetPwdActivity.this, new FindListener<MyUser>() {

            @Override
            public void onError(int arg0, String arg1) {
                Toast.makeText(ForgetPwdActivity.this, arg1, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<MyUser> arg0) {
                if (arg0.size() != 0) {
                    user = arg0.get(0);
                    forget_layout1.setVisibility(View.GONE);
                    forget_user1_tx.setText(user.getUsername());
                    forget_ques_tx.setText(user.getPwdQuestion());
                    forget_layout2.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ForgetPwdActivity.this, R.string.forget_tx4, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //回答密保问题界面
    private void answerQues() {
        if (user.getPwdAnswer().equals(forget_answer_et.getText().toString().trim().hashCode() + "")) {
            forget_layout2.setVisibility(View.GONE);
            forget_user2_tx.setText(user.getUsername());
            forget_layout3.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(ForgetPwdActivity.this, R.string.forget_tx3, Toast.LENGTH_SHORT).show();
        }
    }

    //重设密码界面
    private void reSetPwd() {
        //检测是否不小于6位数并且两次输入相同
        final String pw1 = forget_pw_et.getText().toString();
        String pw2 = forget_pwcheck_et.getText().toString();
        if (pw1.length() >= 6 && pw1.equals(pw2)) {
            JSONObject json = new JSONObject();
            try {
                json.put("password", pw1);
                json.put("username", user.getUsername());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
//			Log.i("Re:","云端开始");
            ace.callEndpoint(ForgetPwdActivity.this, "ResetPwd", json, new CloudCodeListener() {

                @SuppressLint("NewApi")
                public void onSuccess(Object arg0) {
//					Log.i("Re:",arg0.toString());
                    MyApplication.getInstance().getCacheInstance().put(Contants.CURRENTUSERPWD_KEY, Encrypter.encrypt(pw1));
                    user.setPassword(pw1);
                    user.login(ForgetPwdActivity.this, new SaveListener() {

                        @Override
                        public void onFailure(int arg0, String arg1) {
                            // TODO Auto-generated method stub
                            Toast.makeText(ForgetPwdActivity.this, arg1, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            String s = getString(R.string.auto_login);
                            Toast.makeText(getApplicationContext(), s + user.getUsername(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(ForgetPwdActivity.this, MainActivity.class);// 跳转到主界面
                            ForgetPwdActivity.this.startActivity(intent);
                            overridePendingTransition(
                                    R.anim.enter_from_right_animation,
                                    R.anim.exit_to_left_animation);
                            ForgetPwdActivity.this.finish();
                        }

                    });
                    ForgetPwdActivity.this.finish();
                }

                public void onFailure(int arg0, String arg1) {
                    Toast.makeText(ForgetPwdActivity.this, arg1, Toast.LENGTH_LONG).show();
                }
            });
        } else if (pw1.length() < 6)
            Toast.makeText(ForgetPwdActivity.this, R.string.forget_tx1, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(ForgetPwdActivity.this, R.string.forget_tx2, Toast.LENGTH_SHORT).show();
    }

    //按钮监听器
    class onclicklistener implements OnClickListener {

        //点击事件
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.forget_user_bt:
                    checkUser();
                    break;
                case R.id.forget_answer_bt:
                    answerQues();
                    break;
                case R.id.forget_finish_bt:
                    reSetPwd();
                    break;
            }

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intents = new Intent();
            intents.setClass(ForgetPwdActivity.this, LoginActivity.class);
            startActivity(intents);
            overridePendingTransition(
                    R.anim.enter_from_left_animation,
                    R.anim.exit_to_right_animaion);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
