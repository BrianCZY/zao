package com.hzu.zao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzu.zao.adapter.ExperienceAdapter;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.model.Experience;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.GetExperience;
import com.hzu.zao.utils.GetUserRecord;
import com.hzu.zao.view.XListView;

import org.json.JSONArray;

import cn.bmob.v3.BmobUser;


/**
 * 经验页面
 * Created by Nearby Yang on 2015-04-01.
 */
public class ExperienceActivity extends BaseAppCompatActivity implements XListView.IXListViewListener {

    private XListView cardlist;

    private ImageView add_iv;
    private TextView confirm_tx, cancel_tx;
    private Dialog dialog;
    private EditText shareContent, searchExperCont_et;
    // private boolean isLoad = false;
    private LinearLayout experiSearch;
    GetExperience get = null;
    ExperienceAdapter exAdapter = null;
    ExperienceActivity experienceActivity = null;
    JSONArray dataJsonArray = null;
    private RelativeLayout bg_layout;
    private Dialog myDialog;
    private BmobUser currentUser;
    private int animationModel = 0;
    private GetUserRecord getRex = null;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_experience;
    }

    /**
     * 加号出现动画
     */
    private void showPlusButton() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_slid_bottom_in);
        animation.setFillEnabled(true);
        animation.setFillBefore(true);
        animation.setFillAfter(true);
        add_iv.setAnimation(animation);
    }


    // ========================初始化数据========================================
    @Override
    public void initData() {

        setActionbar();
        setTitle(R.string.share_ex);

        currentUser = BmobUser.getCurrentUser(this);
        getRex = new GetUserRecord(this, Contants.EXPERIENCE_TABLE);

        experienceActivity = ExperienceActivity.this;
        get = new GetExperience(this);
        // 判断是否有缓存，如果没有缓存就向Bmob请求，有数据就直接读取

        dataJsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.EXPERIENCE_KEY);


    }

    @Override
    protected void findView() {
        add_iv = (ImageView) findViewById(R.id.fab);
        experiSearch = (LinearLayout) findViewById(R.id.Experi_search_ll);
        bg_layout = (RelativeLayout) findViewById(R.id.ex_layout);

        searchExperCont_et = (EditText) findViewById(R.id.Experi_search_ed);

        cardlist = (XListView) findViewById(R.id.experi_listView);

        exAdapter = new ExperienceAdapter(experienceActivity, cardlist);

        cardlist.setAdapter(exAdapter);

        // 设置下拉刷新
        cardlist.setPullLoadEnable(true);
        cardlist.setXListViewListener(this);

        add_iv.setOnClickListener(new onclick());
        experiSearch.setOnClickListener(new onclick());
    }

    @Override
    protected void bindData() {
        //加号
        showPlusButton();

        if (dataJsonArray != null) {

            bg_layout.setBackgroundColor(Color.WHITE);
            exAdapter.setJsonArray(dataJsonArray);
            exAdapter.notifyDataSetChanged();

        }
        get.back(new Callback());
        get.getExperience();


    }

    @Override
    protected void handerMessage(Message msg) {

    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Contants.RESULT_CODE) {
            get.back(new Callback());
            get.getExperience();
            get.updataWealth();

            getRex.reflash();

        }
    }

    // =======================分享经验=======================================

    private void showInputView() {

//		LayoutInflater mlayout = LayoutInflater.from(ExperienceActivity.this);
//		View mview = mlayout.inflate(R.layout.diolog_share_ex, null);
//
//		shareContent = (EditText) mview.findViewById(R.id.shareCont_et);
//
//		confirm_tx = (TextView) mview.findViewById(R.id.confirm_share);
//		cancel_tx = (TextView) mview.findViewById(R.id.cancel_share);
//
//		dialog = new AlertDialog.Builder(ExperienceActivity.this)
//				.setView(mview).create();
//
//		confirm_tx.setOnClickListener(new onclick());
//		cancel_tx.setOnClickListener(new onclick());
//
//		dialog.show();
        Intent intent = new Intent(this, ShareExpertenceActivity.class);

        startActivityForResult(intent, Contants.REQUEST_CODE);
        overridePendingTransition(R.anim.activity_slid_right_in, R.anim.activity_slid_left_out);

    }

    // ========================回调函数================================

    // 回调函数。等待缓存完成
    class Callback implements MyCallback {

        @Override
        public void getSuccess(boolean isSuccess) {

            // Log.i("callback--result", isSuccess + "");

            if (isSuccess) {

                dataJsonArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.EXPERIENCE_KEY);

                if (dataJsonArray.length() == 0) {
                    bg_layout.setBackgroundResource(R.drawable.recisempty);

                } else {
                    exAdapter.setJsonArray(dataJsonArray);

                    exAdapter.notifyDataSetChanged();
                    bg_layout.setBackgroundColor(Color.WHITE);
                }

            }
        }

    }

    // ==============================点击监听事件====================================

    private class onclick implements View.OnClickListener {
        String Content = "";

        Experience experience = new Experience();

        MyUser userInfo = BmobUser.getCurrentUser(ExperienceActivity.this,
                MyUser.class);

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    if (null != currentUser) {
                        showInputView();
                    } else {
                        loginDialog();
                    }
                    break;
                case R.id.Experi_search_ll:

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),
                            SearchExperienceActivity.class);
                    if (!"".equals(searchExperCont_et.getText().toString())) {
                        intent.putExtra("searchExperContent", searchExperCont_et
                                .getText().toString());
                        startActivity(intent);
                        overridePendingTransition(
                                R.anim.enter_from_right_animation,
                                R.anim.exit_to_left_animation);
                    }
                    break;

                case R.id.cancel_share:
                    dialog.dismiss();
                    break;
//
//				case R.id.confirm_share:// 分享按钮
//
//					Content = shareContent.getText().toString().trim();
//
//					if (!("").equals(Content)) {
//
//						experience.setShareEx(Content);
//						experience.setUser_id(userInfo);
//
//						experience.save(ExperienceActivity.this,
//								new SaveListener() {
//
//									@Override
//									public void onSuccess() {
//
//										get.back(new Callback());
//										get.getExperience();
//										get.updataWealth();
//
//										getRex.reflash();
//
//										Toast.makeText(ExperienceActivity.this,
//												"分享成功", Toast.LENGTH_SHORT).show();
//
//										dialog.dismiss();
//									}
//
//									@Override
//									public void onFailure(int arg0, String arg1) {
//										// Log.i("onFailure",
//										// "onFailure int "+arg0+" Msg "+arg1);
//										Toast.makeText(ExperienceActivity.this,
//												"error \n errorMsg " + arg1,
//												Toast.LENGTH_SHORT).show();
//
//									}
//								});
//
//					} else {
//						Toast.makeText(ExperienceActivity.this, "分享的内容不能为空",
//								Toast.LENGTH_SHORT).show();
//						return;
//					}
//
//					break;

                // 用户不要登陆
                case R.id.login_cancel_tv:
                    myDialog.dismiss();
                    break;

                // 用户要登录
                case R.id.login_sure_tv:

                    myDialog.dismiss();

                    startActivity(new Intent(ExperienceActivity.this,
                            LoginActivity.class));
                    ExperienceActivity.this.overridePendingTransition(
                            R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);
            }
        }
    }

    // =====================刷新完成========================
    private void onLoad() {
        cardlist.stopRefresh();
        cardlist.stopLoadMore();
        cardlist.setRefreshTime("刚刚");
    }

    // ========================下拉加载更多=====================================

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 刷新的数据
                // task = new myAsyncTask();
                // task.execute("start");

                get.back(new Callback());
                get.getExperience();

                onLoad();
            }
        }, 2000);
    }

    // =====================上拉加载更多========================================

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 获取更多的数据
                Log.i("loadMore", "loadMore ");
                onLoad();
            }
        }, 2000);
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

        cancel.setOnClickListener(new onclick());

        config.setOnClickListener(new onclick());

        myDialog.show();

    }

    // ======================自定义Actionbar=============
    public void setActionbar() {


        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_ex_acty);
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

    @Override
    protected void onPause() {
        if (animationModel == 1) {
            overridePendingTransition(R.anim.enter_from_left_animation,
                    R.anim.exit_to_right_animaion);
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            animationModel = 1;

        }
        return super.onKeyDown(keyCode, event);
    }
}
