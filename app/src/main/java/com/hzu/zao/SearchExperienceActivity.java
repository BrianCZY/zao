package com.hzu.zao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hzu.zao.adapter.SearchExperienceAdapter;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.Experience;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class SearchExperienceActivity extends BaseAppCompatActivity implements
        XListView.IXListViewListener {

    private XListView cardlist;
    private LinearLayout experiSearch;
    private LinearLayout progll;
    private EditText  searchExperCont_et;
    SearchExperienceAdapter exAdapter = null;
    SearchExperienceActivity experienceActivity = null;
    private String searchExperContent;
    List<Experience> listData;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_experience;
    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
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

    // =========================初始化控件============================
    private void intiListView() {


    }

    // ========================初始化数据========================================
    @Override
    public void initData() {
        initializeToolbar();
        setTitle(R.string.share_ex);

        progll = (LinearLayout) findViewById(R.id.searchExpprogll1);
        experienceActivity = SearchExperienceActivity.this;
        searchExperContent = getIntent().getStringExtra("searchExperContent");
        // callback = new Callback();
        queryExperience();
    }

    @Override
    protected void findView() {
        ImageView add_iv = (ImageView) findViewById(R.id.fab);
        add_iv.setVisibility(View.GONE);
        experiSearch = (LinearLayout) findViewById(R.id.Experi_search_ll);
        searchExperCont_et = (EditText) findViewById(R.id.Experi_search_ed);
        cardlist = (XListView) findViewById(R.id.experi_listView);
        exAdapter = new SearchExperienceAdapter(SearchExperienceActivity.this,
                cardlist);

        // add_iv.setOnClickListener(new onclick());

    }

    @Override
    protected void bindData() {
        // 设置下拉刷新


        searchExperCont_et.setText(searchExperContent);
//		Log.i("xy", "SearchExpActi    searchExperContent=" + searchExperContent);
        cardlist.setPullLoadEnable(true);
        cardlist.setXListViewListener(this);
        cardlist.setAdapter(exAdapter);
        cardlist.setOnItemClickListener(new ItemOnClickListener());

        experiSearch.setOnClickListener(new onclick());
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    /**
     * 根据输入的内容模糊搜索先对应的问题。
     *
     * @author xiaoyang
     */
    private void queryExperience() {

//		Log.i("xy", "searchExperActi   queryQusetion()");
        progress("start"); //开始转菊花
        listData = (List<Experience>) MyApplication.getInstance().getCacheInstance().getAsListObject(Contants.SEARCHEXPERIENCE_KEY);
        try {

            BmobQuery<Experience> query = new BmobQuery<Experience>();
            query.order("-createdAt");

            // query.addWhereContainedIn("content", Arrays.asList(new
            // String[]{"今天","晚上","吃","什么"}));
            query.addWhereContains("shareEx", searchExperContent);
            query.include("user_id");
            query.setLimit(20);
            if (listData != null) {
//				Log.i("xy",
//						"searchExperActi listData.size()=" + listData.size());

                query.setSkip(listData.size());
            }

            query.findObjects(this, new FindListener<Experience>() {

                @Override
                public void onSuccess(List<Experience> value) {

//					Log.i("xy", "searchExperActi 查找Experience表成功");
                    // 将查到的数据缓存起来
                    progress("end"); //结束转菊花
//					Log.i("xy",
//							"searchExperActi  queryQusetion()  value.size()="
//									+ value.size());

                    if (listData != null && listData.size() != 0) {
                        ArrayList<Experience> listData2 = (ArrayList<Experience>) listData;

                        ArrayList<Experience> value2 = (ArrayList<Experience>) value;
                        ArrayList<Experience> list = (ArrayList<Experience>) listData2
                                .clone();
                        list.addAll(value2);

                        MyApplication.getInstance().getCacheInstance().put(
                                Contants.SEARCHEXPERIENCE_KEY, (List<?>) list);
                        cardlist.setAdapter(new SearchExperienceAdapter(
                                experienceActivity, cardlist));
                        cardlist.setSelection(listData.size());

                    } else {
                        MyApplication.getInstance().getCacheInstance().put(
                                Contants.SEARCHEXPERIENCE_KEY, value);
                        // cardlist.setAdapter(exAdapter);
                        cardlist.setAdapter(new SearchExperienceAdapter(
                                experienceActivity, cardlist));

                    }

                }

                @Override
                public void onError(int arg0, String arg1) {
//					Log.i("zao", "searchExperActi 查找Experience表失败");
                    progress("end"); //结束转菊花
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ==============================点击监听事件====================================

    private class onclick implements View.OnClickListener {
        String Content = "";

        Experience experience = new Experience();

        MyUser userInfo = BmobUser.getCurrentUser(
                SearchExperienceActivity.this, MyUser.class);

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Experi_search_ll:
                    MyApplication.getInstance().getCacheInstance().remove(Contants.SEARCHEXPERIENCE_KEY);
                    searchExperContent = searchExperCont_et.getText().toString();
                    queryExperience();
                    break;
                case R.id.fab:
                    // showInputView();
                    break;

            }
        }
    }

    // =====================刷新完成========================
    private void onLoad() {
        cardlist.stopRefresh();
        cardlist.stopLoadMore();
        cardlist.setRefreshTime("刚刚");
    }

    // ========================下拉刷新=====================================

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
//				Log.i("zao", "searchExperActi  onRefresh");
                MyApplication.getInstance().getCacheInstance().remove(Contants.SEARCHEXPERIENCE_KEY);
                queryExperience();
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
                queryExperience();
                // task.execute("start");
                // 获取更多的数据
//				Log.i("loadMore", "loadMore ");
                onLoad();
            }
        }, 2000);
    }


    @Override
    public void finish() {
//		Log.i("zao", "searchExperActi finish Remove");
        MyApplication.getInstance().getCacheInstance().remove(Contants.SEARCHEXPERIENCE_KEY);
        super.finish();

    }

    public class ItemOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            List<Experience> listExper;
            listExper = (List<Experience>) MyApplication.getInstance().getCacheInstance().getAsListObject(Contants.SEARCHEXPERIENCE_KEY);
            JSONObject experienceObj = new JSONObject();
            if (listExper != null) {
                Experience experience = listExper.get(position);
                if (experience != null) {

                    try {
                        experienceObj.put("updatedAt", experience.getUpdatedAt());
                        experienceObj.put("icon", experience.getUser_id().getIcon());
                        experienceObj.put("username", experience.getUser_id().getUsername());
                        experienceObj.put("sex", experience.getUser_id().getSex());
                        experienceObj.put("shareEx", experience.getShareEx());
                        experienceObj.put("nickName", experience.getUser_id().getNickName());
                        experienceObj.put("objectId", experience.getObjectId());
                        experienceObj.put("createdAt", experience.getCreatedAt());
                        experienceObj.put("user_id", experience.getUser_id());
                        experienceObj.put("good", experience.getGood());
                        experienceObj.put("bad", experience.getBad());

                        experienceObj.put("images", experience.getImages() != null ?
                                new JSONArray(experience.getImages()) : new JSONArray());
//                        experienceObj.put("images", experience.getImages());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                //JSONObject jo = JSONObject.fromObject();

                //Log.i("xy", "searchActivity  questionObj =" + questionObj);
                Intent intent = new Intent(SearchExperienceActivity.this,
                        ExDetailActivity.class);
                intent.putExtra("experienceObj", experienceObj.toString());
                startActivity(intent);
                overridePendingTransition(
                        R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);
            }

        }
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
