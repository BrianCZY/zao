package com.hzu.zao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.Encrypter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 用户注册： 1、使用云端代码。1）验证邀请码，2）验证成功，写入用户信息，3）将用户id插入到对应邀请码的被邀请者列，4）返回操作码
 * 2、1）本地判断整个过程是否操作成功
 * ，2）成功用户直接登陆，3）将默认头像存到：/data/data/com.hzu.zao/files/filename，
 * 4）读取文件，上传到bmob云，作用户默认头像
 *
 * @author Nearby Yang
 *         <p/>
 *         Create at 2015 上午12:14:52
 */
public class RegistActivity extends BaseAppCompatActivity {
    private EditText regist_user_et, regist_pw_et, regist_pwcheck_et,
            regist_incode_et, regist_ques_et, regist_answer_et;
    private Button regist_bt;
    AsyncCustomEndpoints cloudCode;
    String installationId = null;
    //	private BmobUserManager userManager;
    String user;
    String pw;
    JSONObject dataJson;
    String pwcheck;
    String incode;
    String ques;
    String answer;

    // AssetManager assetManager = getAssets();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intents = new Intent();
            intents.setClass(RegistActivity.this, LoginActivity.class);
            startActivity(intents);
            overridePendingTransition(
                    R.anim.enter_from_left_animation,
                    R.anim.exit_to_right_animaion);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        setTitle(R.string.regist_bt);

        cloudCode = new AsyncCustomEndpoints();
        installationId = BmobInstallation.getInstallationId(this);
    }


    @Override
    protected void bindData() {
        regist_bt.setOnClickListener(new RegistListener());
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    // 对象初始化
    @Override
    public void findView() {

        regist_user_et = (EditText) findViewById(R.id.regist_user_et);
        regist_pw_et = (EditText) findViewById(R.id.regist_pw_et);
        regist_pwcheck_et = (EditText) findViewById(R.id.regist_pwcheck_et);
        regist_incode_et = (EditText) findViewById(R.id.regist_incode_et);
        regist_ques_et = (EditText) findViewById(R.id.regist_ques_et);
        regist_answer_et = (EditText) findViewById(R.id.regist_answer_et);

        regist_bt = (Button) findViewById(R.id.regist_bt);

    }


    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_regist);
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


    private class RegistListener implements OnClickListener {

        /**
         * var inviteCode="";//邀请码 var username="";//用户名 var password=""//密码 var
         * pwdQuestion="";//密保问题 var pwdAnswer="";//密保答案
         * <p/>
         * //字符串返回值： //1、该邀请码不存在---777 //2、该邀请码已经使用---778 //3、用户名已存在---779
         * //4、用户注册成功---780
         */
        // 按钮点击事件
        public void onClick(View v) {
            if (init()) {

                cloudCode.callEndpoint(getApplication(), "Regist", dataJson,
                        new CloudCodeListener() {

                            @Override
                            public void onSuccess(Object arg0) {
                                // Log.i("running",
                                // "OnSuccess  here--" + arg0.toString());

                                switch (arg0.toString()) {
                                    case "777":
                                        Toast.makeText(RegistActivity.this,
                                                "该邀请码不存", Toast.LENGTH_SHORT)
                                                .show();
                                        break;
                                    case "778":
                                        Toast.makeText(RegistActivity.this,
                                                "该邀请码已经使用过", Toast.LENGTH_SHORT)
                                                .show();
                                        break;
                                    case "779":
                                        Toast.makeText(RegistActivity.this,
                                                "用户名已存在", Toast.LENGTH_SHORT)
                                                .show();
                                        break;
                                    case "780":

                                        Toast.makeText(RegistActivity.this, "注册成功",
                                                Toast.LENGTH_SHORT).show();

//									userManager
//											.bindInstallationForRegister(user);

                                        UserLogin();

                                        break;

                                }
                                // Toast.makeText(RegistActivity.this,
                                // "连接成功" + arg0, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {
                                Toast.makeText(RegistActivity.this,
                                        "连接服务器失败" + arg0 + "string " + arg1,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    // 对象初始化
    private boolean init() {
        dataJson = new JSONObject();

        user = regist_user_et.getText().toString().trim();
        pw = regist_pw_et.getText().toString().trim();
        pwcheck = regist_pwcheck_et.getText().toString().trim();
        incode = regist_incode_et.getText().toString().trim();
        ques = regist_ques_et.getText().toString().trim();

        int Answer = regist_answer_et.getText().toString().trim().hashCode();
        answer = Answer + "";
        if (("").equals(user)) {
            Toast.makeText(RegistActivity.this, "用户名不能为空", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (("").equals(user) && user.length() < 4) {
            Toast.makeText(RegistActivity.this, "用户名长度不小于4个字符串", Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        if (pw.length() < 6) {
            Toast.makeText(RegistActivity.this, "密码不能少于6位", Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        if (("").equals(pw) || ("").equals(pwcheck)) {
            Toast.makeText(RegistActivity.this, "密码不能为空", Toast.LENGTH_LONG)
                    .show();
            return false;

        } else {
            if (!pw.equals(pwcheck)) {

                Toast.makeText(RegistActivity.this, "输入两次密码不一致",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (("").equals(ques)) {
            Toast.makeText(RegistActivity.this, "密保问题不能为空", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (("").equals(answer)) {
            Toast.makeText(RegistActivity.this, "密保问题答案不能为空",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // Log.i("msg---", user + pw + incode + answer);

        try {
            dataJson.put("inviteCode", incode);
            dataJson.put("username", user);
            dataJson.put("password", pw);
            dataJson.put("pwdQuestion", ques);
            dataJson.put("pwdAnswer", answer);
            dataJson.put("installationId", installationId);

            // Log.i("installationId", installationId + "000");
        } catch (JSONException e) {
            // Log.i("JSONException  ", "--" + e);
            e.printStackTrace();
        }
        return true;
    }

    private void UserLogin() {
        final BmobUser bu2 = new BmobUser();

        bu2.setUsername(user);
        bu2.setPassword(pw);
        // 登陆用户
        bu2.login(RegistActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {

                MyUser myUser = BmobUser.getCurrentUser(RegistActivity.this,
                        MyUser.class);// 获取本地用户objectId

                updateIcon(myUser);

                MyApplication.getInstance().getCacheInstance().put(Contants.USERINFO_KEY, myUser);

                // 缓存并加密用户密码
                MyApplication.getInstance().getCacheInstance().put(Contants.CURRENTUSERPWD_KEY,
                        Encrypter.encrypt(pw));

                startActivity(new Intent(RegistActivity.this,
                        MainActivity.class));
                overridePendingTransition(
                        R.anim.enter_from_left_animation,
                        R.anim.exit_to_right_animaion);

                finish();

            }

            @Override
            public void onFailure(int code, String msg) {
                // Log.i("zao", "登陆失败");
                Toast.makeText(getApplication(),
                        "错误代码  " + code + " 错误信息  " + msg, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    // ==============================上传默认头像==================================

    private void updateIcon(final MyUser user) {
        // 将默认头像放到手机内存
        Bitmap bitmap = null;
        this.getCacheDir();
        String path = "/data/data/com.hzu.zao/files/userIcon.png";

        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.usericon_default);
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("userIcon.png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }

        //=============先上传图片 上传到Bmob云===============
        final BmobFile iconFile = new BmobFile(new File(path));

        iconFile.upload(this, new UploadFileListener() {

            @Override
            public void onSuccess() {

                //===========上传头像成功之后 更新到对应的用户===============
                user.setIcon(iconFile);

                user.update(RegistActivity.this, user.getObjectId(),
                        new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                // Log.i("success", "yeach");
                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {

                                // Log.i("onFailure", "int " + arg0 + " String "
                                // + arg1);

                            }
                        });
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub

            }
        });

    }
}
