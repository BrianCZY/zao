package com.hzu.zao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hzu.zao.adapter.SearchAdapter;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.UserQuestion;
import com.hzu.zao.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SearchAcitivity extends BaseAppCompatActivity implements XListView.IXListViewListener {

    private XListView cardListView;
    private EditText searchEt;
    private ImageView searchIm;
    private ArrayList<HashMap<String, Object>> a_date;
    private Intent intent = null;
    String searchContent = null;
    SearchAdapter searchAdapter;
    List<UserQuestion> listData;
    private List<UserQuestion> listQues;
    private LinearLayout progll;
    private int animationModel = 0;
    private LinearLayout bg_layout;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        intent = getIntent();
        searchContent = intent.getStringExtra("searchContent");
    }

    @Override
    protected void findView() {
        searchEt = (EditText) findViewById(R.id.search_result);
        searchIm = (ImageView) findViewById(R.id.search_im);
        cardListView = (XListView) findViewById(R.id.searchResult_lv);
        progll = (LinearLayout) findViewById(R.id.progll1);
        bg_layout = (LinearLayout) findViewById(R.id.search_layout);

        searchEt.setText(searchContent);
        searchIm.setOnClickListener(new SearchOnClickListner());
        // 设置下拉刷新
        cardListView.setPullLoadEnable(true);
        cardListView.setXListViewListener(SearchAcitivity.this);
        cardListView.setOnItemClickListener(new ItemOnClickListener());
    }

    @Override
    protected void bindData() {

        searchAdapter = new SearchAdapter(SearchAcitivity.this, cardListView);

        cardListView.setAdapter(searchAdapter);

        // callback = new Callback();

//        queryQusetion();

        queryQusetion();// 查找内容
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_search_result);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.finish();
//                overridePendingTransition(R.anim.enter_from_left_animation,
//                        R.anim.exit_to_right_animaion);
//

            }
        });

        setTitle(R.string.search_result);

    }

    /**
     * 根据输入的内容模糊搜索先对应的问题。
     *
     * @author xiaoyang
     */
    private void queryQusetion() {
        progress("start"); // 开始转菊花
        try {

            listData = (List<UserQuestion>) MyApplication.getInstance().getCacheInstance()
                    .getAsListObject(Contants.SEARCHQUESTION_KEY);

            BmobQuery<UserQuestion> query = new BmobQuery<UserQuestion>();

            query.order("-createdAt");

            // query.addWhereContainedIn("content", Arrays.asList(new
            // String[]{"今天","晚上","吃","什么"}));
            query.addWhereContains("content", searchContent);
            query.include("user_id");
            query.setLimit(20);

            if (listData != null) {
                // Log.i("xy",
                // "searchExperActi listData.size()=" + listData.size());

                query.setSkip(listData.size());

            }

            query.findObjects(this, new FindListener<UserQuestion>() {

                @Override
                public void onSuccess(List<UserQuestion> value) {

//                    LogUtils.i("searchActivity 查找UserQuestion表成功");
                    progress("end"); // 结束转菊花
                    // 将查到的数据缓存起来
                    if (value.size() != 0) {
                        bg_layout.setBackgroundResource(R.color.white);
//                        LogUtils.i("searchActivity 查找UserQuestion表成功" + value.size());
                    } else {
                        bg_layout.setBackgroundResource(R.drawable.notfound);
                    }

                    if (listData != null && listData.size() != 0) {

                        ArrayList<UserQuestion> listData2 = (ArrayList<UserQuestion>) listData;

                        ArrayList<UserQuestion> value2 = (ArrayList<UserQuestion>) value;

                        ArrayList<UserQuestion> list = (ArrayList<UserQuestion>) listData2.clone();

                        list.addAll(value2);

                        MyApplication.getInstance().getCacheInstance().put(Contants.SEARCHQUESTION_KEY,
                                (List<?>) list);

                        cardListView.setAdapter(new SearchAdapter(
                                SearchAcitivity.this, cardListView));

                        cardListView.setSelection(listData.size());

                    } else {
                        MyApplication.getInstance().getCacheInstance().put(Contants.SEARCHQUESTION_KEY,
                                value);
                        cardListView.setAdapter(new SearchAdapter(
                                SearchAcitivity.this, cardListView));
                    }

                }

                @Override
                public void onError(int arg0, String arg1) {

                    // Log.i("zao", "Recomm 查找UserQuestion表失败");
                    progress("end"); // 结束转菊花
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class SearchOnClickListner implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // 给searchContent重新赋值
            MyApplication.getInstance().getCacheInstance().remove(Contants.SEARCHQUESTION_KEY);
            searchContent = searchEt.getText().toString();
            queryQusetion();// 查找内容
        }

    }

    public class ItemOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            listQues = (List<UserQuestion>) MyApplication.getInstance()
                    .getCacheInstance().getAsListObject(Contants.SEARCHQUESTION_KEY);

            JSONObject questionObj = new JSONObject();

            if (listQues != null) {
                UserQuestion question = listQues.get((int) id);

                if (question != null) {

                    try {
                        questionObj.put("updatedAt", question.getUpdatedAt());
                        questionObj.put("content", question.getContent());
                        questionObj
                                .put("icon", question.getUser_id().getIcon().getFileUrl(mActivity));
                        questionObj.put("sex", question.getUser_id().getSex());
                        questionObj.put("username", question.getUser_id()
                                .getUsername());
                        questionObj.put("nickName", question.getUser_id()
                                .getNickName());
                        questionObj.put("createdAt", question.getCreatedAt());
                        questionObj.put("objectId", question.getObjectId());
                        questionObj.put("user_id", question.getUser_id());
//                        JSONArray imagesArr= new JSONArray(question.getImages());
                        questionObj.put("images", new JSONArray(question.getImages()));
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }

                // JSONObject jo = JSONObject.fromObject();

                // Log.i("xy", "searchActivity  questionObj =" + questionObj);
                Intent intent = new Intent(SearchAcitivity.this,
                        WanToAnsDetailActivity.class);
                intent.putExtra("searchContent", questionObj.toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);
            }

        }
    }

    // =====================刷新完成========================
    private void onLoad() {
        cardListView.stopRefresh();
        cardListView.stopLoadMore();
        cardListView.setRefreshTime("刚刚");
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // Log.i("xy", "searchActivity   onRefresh()");
                MyApplication.getInstance().getCacheInstance().remove(Contants.SEARCHQUESTION_KEY);
                queryQusetion();
                onLoad();
            }
        }, 2000);

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

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // Log.i("xy", "searchActivity   onLoadMore()");
                queryQusetion();
                onLoad();
            }
        }, 2000);

    }

    @Override
    public void finish() {
        // Log.i("zao", "searchExperActi finish Remove");
        MyApplication.getInstance().getCacheInstance().remove(Contants.SEARCHQUESTION_KEY);
        super.finish();

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
