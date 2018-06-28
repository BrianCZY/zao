package com.hzu.zao.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.zao.EditUserActivity;
import com.hzu.zao.R;
import com.hzu.zao.RecommActivity;
import com.hzu.zao.SettingActivity;
import com.hzu.zao.UserManagerActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.InviteCode;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PersonalFragment extends Fragment {
    private Context context;

    private View mitView;
    private TextView recommTv, verif_Tv, edit_Tv, pwd_Tv, userName_Tv,
            wealth_Tv, proportion_Tv, qq_Tv, address_Tv;
    private TextView managerTxt;
    private ImageView setting_iv, userIcon_iv;
    private Intent intent = new Intent();
    private Dialog alertDialog;
    private MyUser currentUser;
    private View dialogV;
    private AlertDialog dialog;
    private LinearLayout managerLayout;

    private boolean isRefresh = false;//是否刷新
    private final int PIC_FROM_CAMERA = 1;
    private final int PIC_FROM＿LOCALPHOTO = 0;
    private Uri photoUri;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mitView = inflater.inflate(R.layout.activity_personal, container, false);
//
//		ViewGroup vg = (ViewGroup) mitView.getParent();
//		if (vg != null) {
//			vg.removeAllViewsInLayout();
//		}
        return mitView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 从缓存中读取用户
        currentUser = MyApplication.getInstance().getCUser();
        // testSeeUser();
        initView();
        setContent();

    }

//	/**
//	 * 用于查看当前用户的信息
//	 */
//	public void testSeeUser() {
//		Log.i("canta",
//				"currentUser " + "\n" + "id 		 = " + currentUser.getObjectId()
//						+ "\n" + "Icon		 = " + currentUser.getIcon() + "\n"
//						+ "Wealth	 = " + currentUser.getWealth() + "\n"
//						+ "Proportion = " + currentUser.getProportion() + "\n"
//						+ "Qq		 = " + currentUser.getQq() + "\n"
//						+ "Address	 = " + currentUser.getAddress() + "\n"
//						+ "Sex		 = " + currentUser.getSex() + "\n" + "Age		 = "
//						+ currentUser.getAge() + "\n" + "Password	 = "
//						+ currentUser.getP() + "\n");
//	}

    // 初始化控件

    private void initView() {
        userIcon_iv = (ImageView) mitView.findViewById(R.id.user_icon_iv);
        userName_Tv = (TextView) mitView.findViewById(R.id.userName_tv);
        wealth_Tv = (TextView) mitView.findViewById(R.id.wealth_tv);
        proportion_Tv = (TextView) mitView.findViewById(
                R.id.proportion_tv);
        qq_Tv = (TextView) mitView.findViewById(R.id.persional_qq);
        address_Tv = (TextView) mitView.findViewById(R.id.address);

        recommTv = (TextView) mitView.findViewById(R.id.recomm_tv);
        verif_Tv = (TextView) mitView.findViewById(R.id.verif_tv);
        edit_Tv = (TextView) mitView.findViewById(R.id.edit_tv);
        pwd_Tv = (TextView) mitView.findViewById(R.id.pwd_tv);
        setting_iv = (ImageView) mitView.findViewById(R.id.setting);
        managerTxt = (TextView) mitView.findViewById(R.id.txt_manager);
        managerLayout = (LinearLayout) mitView.findViewById(R.id.ll_root_manager);

        managerLayout.setVisibility(View.GONE);
        userIcon_iv.setOnClickListener(new onClickListener());

        setting_iv.setOnClickListener(new onClickListener());

        recommTv.setOnClickListener(new onClickListener());

        verif_Tv.setOnClickListener(new onClickListener());

        edit_Tv.setOnClickListener(new onClickListener());

        pwd_Tv.setOnClickListener(new onClickListener());
        managerTxt.setOnClickListener(new onClickListener());
    }

    /**
     * 将用户的基本信息显示在ui上 The basic information of the user in the UI display .
     */
    private void setContent() {
        // Log.i("zao", "Person   setContent");

//		CacheUtil cacheUtil = new CacheUtil(getActivity());

        if (currentUser != null) {
//		Bitmap userBitmap = cacheUtil.ReadBitmap(Contants.CURRENTUSERICON_KEY); // 得到图片bitmap
            ImageLoader.getInstance().displayImage(currentUser.getIcon() != null ? currentUser.getIcon().getFileUrl(context) :
                    Contants.DEFAULT_AVATAR, userIcon_iv);
//		userIcon_iv.setImageBitmap(userBitmap); // set user head sculpture

            if (currentUser.getNickName() != null) {
                userName_Tv.setText(currentUser.getNickName());
            }
            wealth_Tv.setText(String.valueOf(currentUser.getWealth()));
            calculate(currentUser.getWealth());
            // proportion_Tv.setText(currentUser.getProportion());
            qq_Tv.setText(currentUser.getQq());
            address_Tv.setText(currentUser.getAddress());

            if (currentUser.isRoot() && Contants.ROOT.equals(currentUser.getType())) {
                managerLayout.setVisibility(View.VISIBLE);
            }
        } else {
            userName_Tv.setText("当前用户还未登录");

        }


    }

    /**
     * 刷新界面
     */
    public void setRefresh() {
        isRefresh = true;
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("onResume = " + isRefresh);
        if (isRefresh) {
            currentUser = MyApplication.getInstance().getCUser();
            setContent();
            isRefresh = false;
        }
    }

    /**
     * 计算并显示百分比
     *
     * @param wealth 财富值。
     */
    private void calculate(final int wealth) {
        try {
            // 执行云端的搜索用户的所有房间代码
            String cloudCodeName = "CalculateCoins"; // 云端方法名

            // 创建云端代码对象
            AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
            // 异步调用云端代码
            cloudCode.callEndpoint(context, cloudCodeName,
                    new CloudCodeListener() {

                        // 执行成功时调用，返回result对象
                        @Override
                        public void onSuccess(Object result) {
                            // Log.i("xy", "homeActivity   result =" +
                            // result.toString());

//
//                            double amountCoins = (jsonObject.optDouble("results",0));

                            double amountCoins = Integer.parseInt(result.toString());
                            NumberFormat numFormat = NumberFormat
                                    .getPercentInstance();
                            numFormat.setMaximumFractionDigits(1);
                            // Log.i("xy",
                            // "homeActivity   myUser.getWealth()/100.0="+myUser.getWealth()/100.0);

                            String propor = numFormat.format((double) wealth / amountCoins);// 转换为百分比形式

                            proportion_Tv.setText(propor);
                        }

                        // 执行失败时调用
                        @Override
                        public void onFailure(int arg0, String arg1) {
                            // Log.i("bmob", "BmobException = " + arg1);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 显示对话框
    public void verification() {

        EditText verif_et;

        LayoutInflater mlayoutInflater = LayoutInflater.from(getActivity());
        View myMainView = mlayoutInflater.inflate(
                R.layout.activity_verification, null);
        verif_et = (EditText) myMainView.findViewById(R.id.verif_et);
        Button send_bt = (Button) myMainView.findViewById(R.id.sent_verif);

        send_bt.setOnClickListener(new onClickListener());

        Dialog alertDialog = new AlertDialog.Builder(getActivity()).setView(
                myMainView).create();
        alertDialog.show();
    }

    /**
     * 产生邀请码算法 author xiaoyang
     *
     * @param length
     * @return
     */
    public String haveAnInvitaCode(int length) {
        String val = "";

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

            if ("char".equalsIgnoreCase(charOrNum)) // 字符串
            {
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) // 数字
            {
                val += String.valueOf(random.nextInt(10));
            }
        }

        return val;
    }

    /**
     * 在InviteCode表中查找最新一条创建的数据
     *
     * @author xiaoyang
     */
    public void querydata() {
        try {

            // Log.i("zao", "Person strcreator2= " +
            // "{'__type':'Pointer','className':'_User','objectId':'ff22c3b8a3'}");
            BmobQuery<InviteCode> query = new BmobQuery<InviteCode>();
            // query.addWhereNotEqualTo("creator",
            // "{'__type':'Pointer','className':'_User','objectId':'ff22c3b8a3'}");
            query.addWhereEqualTo("creator", currentUser);
            query.setLimit(1);
            query.order("-createdAt");// 最新的一条
            query.findObjects(context, new FindListener<InviteCode>() {

                @Override
                public void onSuccess(List<InviteCode> list) {

                    try {
                        if (list.size() != 0) { // 如果有记录则往下 if there are record,
                            // continued!

                            // Log.i("zao", "Person 查找Invite表成功");
                            // Log.i("zao", "Person list =" +list);
                            // Log.i("zao", "Person list.size() ="
                            // +list.size());
                            // Log.i("zao", "Person  object.getInCode())="
                            // + list.get(0).getInCode());

                            if (list.get(0).getInvitees() == null) {
                                // 显示未用的邀请码
                                // 对话框
                                LayoutInflater mlayoutInflater = LayoutInflater
                                        .from(getActivity());
                                View myMainView = mlayoutInflater.inflate(
                                        R.layout.activity_verification, null);
                                EditText verif_et = (EditText) myMainView
                                        .findViewById(R.id.verif_et);
                                Button send_bt = (Button) myMainView
                                        .findViewById(R.id.sent_verif);
                                // 设置button响应事件
                                send_bt.setOnClickListener(new onClickListener(
                                        list.get(0).getInCode().trim()));

                                Dialog alertDialog = new AlertDialog.Builder(
                                        getActivity()).setView(myMainView)
                                        .create();
                                verif_et.setText(list.get(0).getInCode());
                                alertDialog.show(); // 显示对话框
                                // alertDialog.show();
                            } else {// 没有未被使用的邀请码
                                // 生成邀请码
                                // Log.i("zao", "Person 生成邀请码");
                                final String invitaCodeStr = haveAnInvitaCode(4);
                                // insert inCode into InvitaCode table
                                InviteCode inviteCode = new InviteCode();
                                inviteCode.setCreator(currentUser);
                                inviteCode.setInCode(invitaCodeStr);

                                inviteCode.save(context,
                                        new SaveListener() {

                                            @Override
                                            public void onSuccess() {
                                                try {
                                                    // Log.i("zao",
                                                    // "Person 插入inCode成功");
                                                    // 对话框
                                                    LayoutInflater mlayoutInflater = LayoutInflater
                                                            .from(getActivity());
                                                    View myMainView = mlayoutInflater
                                                            .inflate(
                                                                    R.layout.activity_verification,
                                                                    null);
                                                    EditText verif_et = (EditText) myMainView
                                                            .findViewById(R.id.verif_et);
                                                    Button send_bt = (Button) myMainView
                                                            .findViewById(R.id.sent_verif);
                                                    // 设置button响应事件
                                                    send_bt.setOnClickListener(new onClickListener(
                                                            invitaCodeStr));

                                                    Dialog alertDialog = new AlertDialog.Builder(
                                                            getActivity())
                                                            .setView(myMainView)
                                                            .create();
                                                    verif_et.setText(invitaCodeStr);
                                                    alertDialog.show(); // 显示对话框
                                                } catch (Exception e) {
                                                    // block
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(int code,
                                                                  String arg0) {
                                                // Log.i("zao",
                                                // "Person 插入inCode失败");
                                            }
                                        });

                            }

                        } else {
                            // 没有未被使用的邀请码
                            // 生成邀请码
                            // Log.i("zao", "Person 生成邀请码");

                            final String invitaCodeStr = haveAnInvitaCode(4);
                            // insert inCode into InvitaCode table
                            InviteCode inviteCode = new InviteCode();
                            inviteCode.setCreator(currentUser);
                            inviteCode.setInCode(invitaCodeStr);

                            inviteCode.save(context, new SaveListener() {

                                @Override
                                public void onSuccess() {
                                    try {
                                        // Log.i("zao", "Person 插入inCode成功");

                                        // 对话框
                                        LayoutInflater mlayoutInflater = LayoutInflater
                                                .from(getActivity());

                                        View myMainView = mlayoutInflater
                                                .inflate(
                                                        R.layout.activity_verification,
                                                        null);

                                        EditText verif_et = (EditText) myMainView
                                                .findViewById(R.id.verif_et);

                                        Button send_bt = (Button) myMainView
                                                .findViewById(R.id.sent_verif);
                                        // 设置button响应事件
                                        send_bt.setOnClickListener(new onClickListener(
                                                invitaCodeStr));

                                        Dialog alertDialog = new AlertDialog.Builder(
                                                getActivity()).setView(
                                                myMainView).create();

                                        verif_et.setText(invitaCodeStr);

                                        alertDialog.show(); // 显示对话框

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int code, String arg0) {
                                    // Log.i("zao", "Person 插入inCode失败");
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(int arg0, String arg1) {
                    // Log.i("zao", "Person 查找Invite表失败");
                    Toast.makeText(context,
                            getResources().getString(R.string.unkown_failure),
                            Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送验证码button 监听事件
     *
     * @author xiaoyang
     */
    public class sendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }

    }

    private void editPwd() {
        EditText oldPwd, newPwd, confPwd;
        TextView confirm, cancel;

        LayoutInflater li = LayoutInflater.from(getActivity());
        dialogV = li.inflate(R.layout.activity_editpwd, null);

        oldPwd = (EditText) dialogV.findViewById(R.id.old_pwd);
        newPwd = (EditText) dialogV.findViewById(R.id.new_pwd);
        confPwd = (EditText) dialogV.findViewById(R.id.confirm_pwd_et);
        confirm = (TextView) dialogV.findViewById(R.id.confirm_pwd_bt);
        cancel = (TextView) dialogV.findViewById(R.id.cancel_pwd);

        confirm.setOnClickListener(new onClickListener());

        cancel.setOnClickListener(new onClickListener());

        alertDialog = new AlertDialog.Builder(getActivity()).setView(dialogV)
                .create();
        alertDialog.show();
    }

    /**
     * 发送短信
     *
     * @param smsBody
     */

    private void sendSMS(String smsBody)

    {

        Uri smsToUri = Uri.parse("smsto:");

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        intent.putExtra("sms_body", smsBody);

        startActivity(intent);
        ((Activity) context).overridePendingTransition(
                R.anim.enter_from_top_animation,
                R.anim.exit_to_bottom_animaion);

    }

    private class onClickListener implements OnClickListener {
        private String str;

        public onClickListener() {
            super();
        }

        public onClickListener(String str) {
            super();
            this.str = str;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.user_icon_iv:
//				Log.i("zao", "EditUserActivity   R.id.edit_user_iv");
                    // 修改用户头像。弹窗显示调用相机还是使用本地图片
                    // Modify user Avater。show popups：Call the camera or the use of
                    // local image
                    dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.show();
                    Window win = dialog.getWindow();
                    win.setContentView(R.layout.edit_user_icon_dialog);
                    TextView cameraTv = (TextView) win
                            .findViewById(R.id.dialog_camera_tv);
                    TextView localPictureTv = (TextView) win
                            .findViewById(R.id.dialog_local_tv);
                    cameraTv.setOnClickListener(new DiaOnclickListener()); //调用相机
                    localPictureTv.setOnClickListener(new DiaOnclickListener());//使用本地图片
                    break;
                case R.id.sent_verif:


                    // Log.i("zao", "str = " + str);
                    sendSMS("我在用早知道，很好用喔！送你一个邀请码：" + str + ",赶紧来注册吧！");
                    // Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_SHORT)
                    // .show();
                    break;

                case R.id.setting:
                    intent.setClass(context, SettingActivity.class);
                    startActivity(intent);
                    ((Activity) context).overridePendingTransition(
                            R.anim.enter_from_top_animation,
                            R.anim.exit_to_bottom_animaion);
                    break;
                case R.id.recomm_tv:
                    intent.setClass(context, RecommActivity.class);
                    startActivity(intent);
                    ((Activity) context).overridePendingTransition(
                            R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);
                    break;
                case R.id.verif_tv:
                    querydata(); // 在InviteCode表中查找最新一条创建的数据
                    // verification();
                    break;
                case R.id.edit_tv:
                    intent.setClass(context, EditUserActivity.class);
                    startActivity(intent);
                    ((Activity) context).overridePendingTransition(
                            R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);
                    break;
                case R.id.pwd_tv:
                    editPwd();
                    break;
                case R.id.confirm_pwd_bt:
                    // Toast.makeText(getActivity(), "confirm", Toast.LENGTH_SHORT)
                    // .show();
                    doSavePwd();
                    break;
                case R.id.cancel_pwd:
                    alertDialog.dismiss();
                    break;
                case R.id.txt_manager:
                    intent = new Intent(context, UserManagerActivity.class);
                    startActivity(intent);
                    break;

            }

        }
    }

    /**
     * 保存修改的密码到云端
     */
    public void doSavePwd() {
//        Log.i("xy", "Person doSavePwd()");
        EditText oldPwd;
        final EditText newPwd;
        final EditText confPwd;
        oldPwd = (EditText) dialogV.findViewById(R.id.old_pwd);
        newPwd = (EditText) dialogV.findViewById(R.id.new_pwd);
        confPwd = (EditText) dialogV.findViewById(R.id.confirm_pwd_et);
        final BmobUser user = new BmobUser();
        user.setUsername(currentUser.getUsername());
        user.setPassword(oldPwd.getText().toString());
        if (newPwd.length() < 6) {
            Toast.makeText(context, "密码不能少于6位", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (!"".equals(oldPwd.getText().toString())
                && !"".equals(newPwd.getText().toString())
                && !"".equals(confPwd.getText().toString())) {
            user.login(context, new SaveListener() {

                @SuppressWarnings("static-access")
                @Override
                public void onSuccess() {

                    // Log.i("xy", "Person 密码正确");

                    String newStr = newPwd.getText().toString();
                    String conStr = confPwd.getText().toString();
                    if (!"".equals(newStr) && newStr.equals(conStr)) {
                        // 当两次输入的密码不为空且相等时，更改用户密码
                        try {
                            String cloudCodeName = "ResetPwd"; // 云端方法名
                            JSONObject params = new JSONObject();
                            params.put("username", currentUser.getUsername());// 测试用
                            params.put("password", newStr);// 测试用
                            // 创建云端代码对象
                            AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
                            // 异步调用云端代码
                            cloudCode.callEndpoint(context,
                                    cloudCodeName, params,
                                    new CloudCodeListener() {

                                        @Override
                                        public void onSuccess(Object arg0) {
                                            // update password success
                                            Toast.makeText(
                                                    ((Activity) context),
                                                    getResources()
                                                            .getString(
                                                                    R.string.update_password_success),
                                                    Toast.LENGTH_LONG).show();
                                            alertDialog.cancel();// 退出编辑对话框
                                            Log.i("xy",
                                                    "Person    update password success");
                                        }

                                        @Override
                                        public void onFailure(int arg0,
                                                              String arg1) {
                                            // Log.i("xy", "BmobException = "
                                            // + arg1);
                                        }
                                    });

                            //
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 两次输入的密码不匹配
                        Toast.makeText(context,
                                getResources().getString(
                                        R.string.two_pwd_not_match), Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    // 原密码错误
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.wrong_original_pwd), Toast.LENGTH_LONG).show();

                }
            });

        } else {
            // all password can not be null
            Toast.makeText(context,
                    getResources().getString(R.string.pwd_can_not_be_null), Toast.LENGTH_LONG)
                    .show();
        }

    }

    public class DiaOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_camera_tv:
                    doHandlerPhoto(PIC_FROM_CAMERA);
                    break;
                case R.id.dialog_local_tv:
                    doHandlerPhoto(PIC_FROM＿LOCALPHOTO);
                    break;

                default:
                    break;
            }

        }

    }

    /**
     * 根据不同方式选择图片设置ImageView
     *
     * @param type 0-本地相册选择，非0为拍照
     */
    private void doHandlerPhoto(int type) {
        try {
            // 保存裁剪后的图片文件
            File pictureFileDir;
            if (Environment.getExternalStorageState()
                    .equals(android.os.Environment.MEDIA_MOUNTED)) {
                pictureFileDir = new File(
                        Environment.getExternalStorageDirectory(), "/zao");

            } else {
                pictureFileDir = new File(context.getFilesDir().toString(), "/zao");
            }
            File picFile = new File(pictureFileDir, "userIcon.png");
            if (!picFile.exists()) {
                picFile.createNewFile();
            }
            photoUri = Uri.fromFile(picFile);

            if (type == PIC_FROM＿LOCALPHOTO) {
                Intent intent = getCropImageIntent();
                startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
                dialog.cancel();// 退出dialog
            } else {
                Intent cameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
                dialog.cancel();// 退出dialog
            }

        } catch (Exception e) {
//			Log.i("HandlerPicError", "处理图片出现错误");
        }
    }

    /**
     * 调用图片剪辑程序
     */
    public Intent getCropImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        setIntentParams(intent);
        return intent;
    }

    /**
     * 启动裁剪
     */
    private void cropImageUriByTakePhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        setIntentParams(intent);
        startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
    }

    /**
     * 设置公用参数
     */
    private void setIntentParams(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PIC_FROM_CAMERA: // 拍照
                try {
                    cropImageUriByTakePhoto();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PIC_FROM＿LOCALPHOTO:
                try {
                    if (photoUri != null) {
                        Bitmap bitmap = decodeUriAsBitmap(photoUri);
                        userIcon_iv.setImageBitmap(bitmap);
                        //upLoadBloack();
                        upLoadBmobFile();
                        dialog.cancel();// 退出dialog
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public void upLoadBloack() {
        String picPath = "/mnt/sdcard/zao/userIcon.png";
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(context, new UploadFileListener() {

            @Override
            public void onSuccess() {
                //bmobFile.getUrl()---返回的上传文件的地址（不带域名）
                //bmobFile.getFileUrl(context)--返回的上传文件的完整地址（带域名）
//				Log.i("xy",
//						"EditUserActivity   bmobFile.getFileUrl()="
//								+ bmobFile.getUrl());
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    /**
     * 上传图片到服务器
     */
    private void upLoadBmobFile() {
        File pictureFileDir;
        if (Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED)) {
            pictureFileDir = new File(
                    Environment.getExternalStorageDirectory(), "/zao");

        } else {
            pictureFileDir = new File(context.getFilesDir().toString(), "/zao");
        }


        final File picFile = new File(pictureFileDir, "userIcon.png");
//		Log.i("xy", "persion  picFile:"+picFile.getPath());
        final BmobFile bmobFile = new BmobFile(picFile);
//		Log.i("xy", "persion  bmobFile:"+bmobFile);
//		Toast.makeText(getActivity(),
//				getResources().getString(R.string.please_wait_upload), 1)
//				.show();// 请等待上传

        bmobFile.uploadblock(context, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // bmobFile.getUrl()---返回的上传文件的地址（不带域名）
                // bmobFile.getFileUrl(context)--返回的上传文件的完整地址（带域名）
//				Toast.makeText(getActivity(),
//						getResources().getString(R.string.success_upload), 1)
//						.show();// 上传文件成功

                currentUser.setIcon(bmobFile);// 设置头像的链接
                updateUser();//在服务器上更新用户信息

//                try { //将用户头像保存在缓存中
//                    FileInputStream is;
//                    is = new FileInputStream(picFile);
//                    Bitmap userBitmap = BitmapFactory.decodeStream(is);
//                    utility.SaveBitmap(Contants.CURRENTUSERICON_KEY,
//                            userBitmap);
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }


                // bmobFile.getFileUrl()
                // http://file.bmob.cn/M00/34/DD/oYYBAFU0bW2AQjPGAA-x20z2DVE50.jpeg;
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）

            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(context,
                        getResources().getString(R.string.failed_upload), Toast.LENGTH_LONG)
                        .show();// 上传文件失败
            }
        });

    }

    /**
     * 在服务器上更新用户信息
     */
    public void updateUser() {
        try {

            currentUser.update(context,
                    currentUser.getObjectId(), new UpdateListener() {

                        @Override
                        public void onSuccess() {
//							Log.i("zao", "EditUserActivity    更新_User表成功");
                            MyApplication.getInstance().getCacheInstance().put(Contants.CURRENTUSER_KEY,
                                    currentUser); // 缓存本地用户

//							Toast.makeText(
//									getActivity(),
//									getResources().getString(
//											R.string.success_update), 1).show();

                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
//							Log.i("zao", "EditUserActivity    更新_User表失败");

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    @Override
//    public void onResume() {
////        currentUser = (MyUser) 	MyApplication.getInstance().getCacheInstance().getAsObject(Contants.CURRENTUSER_KEY);
////        setContent();
//        super.onResume();
//    }

}
