package com.hzu.zao.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.zao.CommitQuestionActivity;
import com.hzu.zao.ExperienceActivity;
import com.hzu.zao.LoginActivity;
import com.hzu.zao.R;
import com.hzu.zao.SearchAcitivity;
import com.hzu.zao.WanToAnsActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.model.UserQuestion;
import com.hzu.zao.utils.GetUserRecord;
import com.hzu.zao.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

public class HomeFragment extends Fragment {

    private View mitView;
    private Button wToAnsBtn, myExperiBtn;
    private LinearLayout search_Ly, questionging_LL, userMsgLl;
    private TextView homeLoginLl;
    private Intent i = new Intent();
    private TextView canceltv, send_tv, coinsNum_tv, coinsPropor_tv;
    private EditText quest_content, searchContent;
    private Dialog dialog;
    private ImageView im;
    private String q_content;
    private MyUser myUser;
    private Dialog myDialog;
    private BmobUser currentUser;
    private GetUserRecord getRex = null;
    private Context context;

    // private CacheUtil cache;//使用异步任务
    // AsyncTaskUtil task;//使用异步任务
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mitView = inflater.inflate(R.layout.activity_home, container, false);
//		ViewGroup vg = (ViewGroup) mitView.getParent();
//		if (vg != null) {
//			vg.removeAllViewsInLayout();
//		}
        return mitView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(getActivity());
        getRex = new GetUserRecord(context, Contants.MYQUESTION_TABLE);
        // 获取当前UserId
        myUser = MyApplication.getInstance().getCUser();

        intiView();
        intidata();
    }

    //
    private void intiView() {
        // task.execute("http://file.bmob.cn/M00/EC/F1/oYYBAFUuAmWAL4WBAAYJ-3aHJ-M922.jpg");

        im = (ImageView) mitView.findViewById(R.id.user_home_iv);
        wToAnsBtn = (Button) mitView.findViewById(R.id.wToAns);
        myExperiBtn = (Button) mitView.findViewById(R.id.myEx);

        questionging_LL = (LinearLayout) mitView.findViewById(
                R.id.myQues_ll);
        userMsgLl = (LinearLayout) mitView.findViewById(R.id.user_msg_ll);
        homeLoginLl = (TextView) mitView.findViewById(R.id.home_login_ll);

        search_Ly = (LinearLayout) mitView.findViewById(R.id.search_ll);

        coinsNum_tv = (TextView) mitView.findViewById(R.id.coins_num);
        coinsPropor_tv = (TextView) mitView.findViewById(
                R.id.coins_propor);

        searchContent = (EditText) mitView.findViewById(R.id.search_ed);

        wToAnsBtn.setOnClickListener(new BtnOnClickListener());
        myExperiBtn.setOnClickListener(new BtnOnClickListener());

        search_Ly.setOnClickListener(new BtnOnClickListener());
        questionging_LL.setOnClickListener(new BtnOnClickListener());
        homeLoginLl.setOnClickListener(new BtnOnClickListener());

    }

    private void intidata() {
        // 更新本地用户缓存
        updataUserCache();

        if (judgeLogin()) { /// 如果用户登录了
            // Log.i("xy", "homeActivity   judgeLogin()=true");
            homeLoginLl.setVisibility(View.GONE);
            userMsgLl.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(myUser.getIcon().getFileUrl(context), im);

            // 金币
            coinsNum_tv.setText(myUser.getWealth() + "");

            calculate(myUser.getWealth());

        } else {
            userMsgLl.setVisibility(View.GONE);
            homeLoginLl.setVisibility(View.VISIBLE);
        }

    }

    private void calculate(int wealth) {
        try {
            // 执行云端的搜索用户的所有房间代码
            String cloudCodeName = "CalculateCoins"; // 云端方法名

            // 创建云端代码对象
            AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
            // 异步调用云端代码
            cloudCode.callEndpoint(getActivity(), cloudCodeName,
                    new CloudCodeListener() {

                        // 执行成功时调用，返回result对象
                        @Override
                        public void onSuccess(Object result) {

                            // Log.i("xy", "homeActivity   result =" +
                            // result.toString());
                            // int amountCoins =
                            // Integer.parseInt(result.toString());

                            float amountCoins = Float.parseFloat(result
                                    .toString());
                            NumberFormat numFormat = NumberFormat
                                    .getPercentInstance();
                            numFormat.setMaximumFractionDigits(1);

                            // Log.i("xy",
                            // "homeActivity   myUser.getWealth()/100.0="+myUser.getWealth()/100.0);

                            String propor = numFormat.format(myUser.getWealth()
                                    / amountCoins);// 转换为百分比形式

                            coinsPropor_tv.setText(propor);
                        }

                        // 执行失败时调用
                        @Override
                        public void onFailure(int arg0, String arg1) {
                            LogUtils.e("bmob BmobException = " + arg1);
                        }
                    });
        } catch (Exception e) {
            // e.printStackTrace();
        }

    }

    // 更新用户本地缓存
    private void updataUserCache() {
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        // query.setCachePolicy(CachePolicy.NETWORK_ONLY);
        if (null != myUser) {

            query.getObject(context, myUser.getObjectId(),
                    new GetListener<MyUser>() {

                        @Override
                        public void onFailure(int arg0, String arg1) {

                        }

                        @Override
                        public void onSuccess(MyUser arg0) {

                            // myUser = BmobUser.getCurrentUser(context,
                            // MyUser.class);
                            coinsNum_tv.setText(arg0.getWealth() + "");
                        }

                    });
        }

    }



    /**
     * 刷新数据
     */
    public void refreshData(){
        myUser = MyApplication.getInstance().getCUser();

        //刷新数据
        intidata();
    }

    /**
     * 判断用户是否登陆
     *
     * @return 1, 用户已登陆, return true ; 2,用户未登陆,return false
     */
    public boolean judgeLogin() {
        // Log.i("xy", "MainActivity   judgeLogin1");
        // user = BmobUser.getCurrentUser(MainActivity.this, MyUser.class);
        return MyApplication.getInstance().getCUser() != null;

    }

    // 登陆确认
    public void loginDialog() {

        TextView cancel;
        TextView config;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.login_dialog, null);

        cancel = (TextView) myView.findViewById(R.id.login_cancel_tv);
        config = (TextView) myView.findViewById(R.id.login_sure_tv);

        myDialog = new AlertDialog.Builder(getActivity()).setView(myView)
                .create();

        cancel.setOnClickListener(new BtnOnClickListener());

        config.setOnClickListener(new BtnOnClickListener());

        myDialog.show();

    }

    //
    private class BtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                // 我来回答
                case R.id.wToAns:
                    // 使用异步任务
                    // im.setImageBitmap(cache.ReadBitmap("http://file.bmob.cn/M00/EC/F1/oYYBAFUuAmWAL4WBAAYJ-3aHJ-M922.jpg"));

                    // if (null != currentUser) {// 用户已登录

                    i.setClass(context, WanToAnsActivity.class);
                    startActivity(i);

                    ((Activity) context).overridePendingTransition(
                            R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);


                    // } else {// 未登陆
                    //
                    // loginDialog();
                    // }

                    break;

                // 搜索
                case R.id.search_ll:

                    i.setClass(context, SearchAcitivity.class);

                    if (!"".equals(searchContent.getText().toString())) {
                        i.putExtra("searchContent", searchContent.getText()
                                .toString());
                        startActivity(i);
                        ((Activity) context).overridePendingTransition(
                                R.anim.enter_from_right_animation,
                                R.anim.exit_to_left_animation);
                    } else {
                        Toast.makeText(context, R.string.searchIsEmpty,
                                Toast.LENGTH_SHORT).show();
                    }

                    break;
                // 我找经验
                case R.id.myEx:
                    // if (null != currentUser) {// 用户已登录
                    i.setClass(getActivity(), ExperienceActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(
                            R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);
                    // } else {// 未登录
                    // loginDialog();
                    // }
                    break;

                // 弹窗，我来提问
                case R.id.myQues_ll:
                    if (null != currentUser) {// 用户已登录
                        questionView();
                    } else {// 未登录
                        loginDialog();
                    }
                    break;
                // 登陆按钮
                case R.id.home_login_ll:

                    i.setClass(getActivity(), LoginActivity.class);

                    startActivity(i);

                    getActivity().overridePendingTransition(
                            R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);

//                    getActivity().finish();

                    break;
                // 用户不要登陆
                case R.id.login_cancel_tv:
                    myDialog.dismiss();
                    break;
                // 用户要登录
                case R.id.login_sure_tv:

                    myDialog.dismiss();

                    startActivity(new Intent(context, LoginActivity.class));
                    ((Activity) context).overridePendingTransition(
                            R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);

                case R.id.quest_cancel:
                    dialog.dismiss();
                    break;
                case R.id.quest_confir:
                    getQuestion();
                    if (("").equals(q_content)) {

                        Toast.makeText(context, "问题内容不能为空",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        UserQuestion interrogate = new UserQuestion();
                        interrogate.setUser_id(myUser);
                        interrogate.setContent(q_content);
                        interrogate.save(context, new SaveListener() {

                            @Override
                            public void onSuccess() {

                                getRex.reflash();

                                Toast.makeText(context, "提问成功",
                                        Toast.LENGTH_SHORT).show();

                                dialog.dismiss();

                                // Log.i(Tag, "success!!");

                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {
                                Toast.makeText(context, arg1,
                                        Toast.LENGTH_SHORT).show();
                                // Log.i(Tag, "erro  arg0 " + arg0 + " arg1 " +
                                // arg1);

                            }
                        });

                    }

                    break;
                default:
                    break;
            }

        }

    }

    // 提出问题，弹窗
    private void questionView() {

        Intent intent = new Intent(context, CommitQuestionActivity.class);

        startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.enter_from_right_animation,
                R.anim.exit_to_left_animation);

//		LayoutInflater mlayout = LayoutInflater.from(getActivity());
//		View myView = mlayout.inflate(R.layout.activity_questioning, null);
//
//		quest_content = (EditText) myView.findViewById(R.id.questioning_et);
//		canceltv = (TextView) myView.findViewById(R.id.quest_cancel);
//		send_tv = (TextView) myView.findViewById(R.id.quest_confir);
//
//		dialog = new AlertDialog.Builder(getActivity()).setView(myView)
//				.create();
//
//		canceltv.setOnClickListener(new BtnOnClickListener());
//		send_tv.setOnClickListener(new BtnOnClickListener());
//
//		dialog.show();

    }

    // 回去提出问题的数据
    private void getQuestion() {
        q_content = quest_content.getText().toString().trim();
    }

}
