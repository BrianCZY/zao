package com.hzu.zao;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.zao.adapter.CommAdapter;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.model.Comment;
import com.hzu.zao.model.Comment4list;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.EmotionUtils;
import com.hzu.zao.utils.GetMessage;
import com.hzu.zao.utils.GetUserQuestion;
import com.hzu.zao.utils.GetUserRecord;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.view.CusProcessDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;


/**
 * 我来回答 问题的详细页面
 *
 * @author Nearby Yang
 *         <p/>
 *         Create at 2015-5-1 下午3:27:00
 */
public class WanToAnsDetailActivity extends BaseAppCompatActivity implements OnClickListener {

    private TextView name_tx;
    private ListView listView;
    private ImageView addEmoationIm;
    private ViewPager emotionViewPager;
    private LinearLayout emotionPanel_bg;
    private EditText content_tx;
    private ProgressDialog progressDialog;
//    private TwoBallRotationProgressBar progressLayout;

    private CommAdapter commAdapter;
    private WanToAnsDetailActivity context;
    private ImageView sendComm;
    private MyUser userInfo;

    private JSONObject jsonObject = null;// 问题对应的jsonObject
    private String question_id = "";
    private GetUserQuestion getInfo = null;
    private Comment4list questionObj = null;
    private getCallbackList getcall = null;
    private GetUserRecord getRex = null;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_wan_detail;
    }

    /**
     * 初始化数据。 调用云端方法，返回list
     */
    @Override
    public void initData() {
        initializeToolbar();
        setTitle(R.string.txt_main_body);
        context = WanToAnsDetailActivity.this;
        getInfo = new GetUserQuestion(context);
        questionObj = new Comment4list();
        userInfo = BmobUser.getCurrentUser(context, MyUser.class);
        getRex = new GetUserRecord(this, Contants.MYANSWER_TABLE);

        JSONArray jsonArray = null;

        String position = null;
        String searchContent;
        String message = null;
        String myQuest = null;
        String myAns = null;

        getcall = new getCallbackList();
        Intent intents = getIntent();
        // 获取数据

        position = intents.getStringExtra("num");
        // Log.i("点击item传入的序数", "" + position);

        searchContent = intents.getStringExtra("searchContent");

        // 消息的详细内容
        message = intents.getStringExtra("msg");

        // 用户记录的详细内容
        myQuest = intents.getStringExtra("myQuest");

        // 用户记录 我的回答--详细内容
        myAns = intents.getStringExtra("myAns");


//		Log.i("信息", "position "+position
//				+" searchContent "+searchContent
//				+" message "+message
//				+" myQuest "+myQuest
//				+" myAns "+myAns);

        if (position != null) {

            try {
                jsonObject = new JSONObject(position);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (searchContent != null) {

            try {
                jsonObject = new JSONObject(searchContent);


            } catch (JSONException e3) {
                e3.printStackTrace();
            }
        } else if (message != null) {// 信息详情


            try {

                jsonObject = new JSONObject(message);
//				Log.i("详细信息", ""+jsonObject.toString());
            } catch (JSONException e3) {
                LogUtils.e("详细信息 " + e3.toString());

            }

//			Log.i("msg", ""+jsonObject);

            GetMessage getMsg = new GetMessage(this);
            getMsg.ReflashMsg(userInfo.getObjectId());


        } else if (null != myQuest) {// 用户记录，提问的详细页面

            try {
                jsonObject = new JSONObject(myQuest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (null != myAns) {// 用户记录，回答的详细页面
            try {
                jsonObject = new JSONObject(myAns);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void findView() {
        List<Comment4list> templist = new ArrayList<Comment4list>();

        sendComm = (ImageView) findViewById(R.id.wdt_send);
        content_tx = (EditText) findViewById(R.id.ans_content);
        name_tx = (TextView) findViewById(R.id.wand_username);
        listView = (ListView) findViewById(R.id.wand_listV);
        addEmoationIm = $(R.id.im_content_dialog_share_emotion);
        emotionViewPager = $(R.id.vp_popupwindow_emotion_dashboard);
        emotionPanel_bg = $(R.id.ll_popip_window_emotion_panel);
//        progressLayout = $(R.id.pb_two_progress_bar);
        progressDialog = CusProcessDialog.commenProgressDialog(this, "正在加载");
        // 发送评论
        sendComm.setOnClickListener(new myOnClick());
        content_tx.setOnClickListener(this);

        commAdapter = new CommAdapter(context, listView, getcall);

        templist.add(questionObj);

        commAdapter.setList(templist);

        listView.setAdapter(commAdapter);

    }

    @Override
    protected void bindData() {
//        progressLayout.setVisibility(View.VISIBLE);
        progressDialog.show();
        // 初始化问题数据
        initQuest();
        //表情
        new EmotionUtils(mActivity, emotionViewPager, content_tx);
        addEmoationIm.setOnClickListener(this);

        getInfo.listCb(new getCallbackList());
        // 调用云端方法进行查询
        getInfo.getComm(question_id);

    }

    @Override
    protected void handerMessage(Message msg) {

    }


    // 初始化问题数据
    private void initQuest() {

        // 对应问题的id，根据问题id查询对应的评论、回答
        try {
            question_id = jsonObject.getString("objectId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 问题的id
        questionObj.setQuest_id(question_id);

        // 问题内容

        try {
            questionObj.setComContent(jsonObject.getString("content"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        // 提出问题的时间
        try {
            questionObj.setCreatAt(jsonObject.getString("createdAt"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 设置提问者的id
        try {
            questionObj.setUser_id(jsonObject.getString("user_id"));
        } catch (JSONException e2) {
            e2.printStackTrace();
        }

        // 提问用户用户名 nickName
        try {
            questionObj.setUserName4(jsonObject.getString("username"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 用户头像
        try {
            questionObj.setUserIcon(jsonObject.getString("icon"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        try {
            JSONArray imageArray = jsonObject.getJSONArray("images");
            if (imageArray != null) {
                List<String> imageList = new ArrayList<>();
                for (int i = 0; i < imageArray.length(); i++) {
                    imageList.add(imageArray.getString(i));
                }
                questionObj.setImages(imageList);

                LogUtils.e("图片存在 " + imageArray.toString());
            }


        } catch (JSONException e) {
            LogUtils.e("图片不存在 " + e.toString());
        }

        // 提问用户昵称

        try {
            questionObj.setNickName(jsonObject.getString("nickName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 提问用户性别

        try {
            questionObj.setUserSex(jsonObject.getString("sex"));
        } catch (JSONException e) {
            e.printStackTrace();

        }
        // 类型 type=0--->第一项
        questionObj.setType(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.im_content_dialog_share_emotion:
                emotionPanel_bg.setVisibility(emotionPanel_bg.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.ans_content:
                emotionPanel_bg.setVisibility(View.GONE);
                break;
        }


    }

    /*
        * 初始化toolbar
        */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_want_ans_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.finish();

            }
        });

    }
    // 点击事件

    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            String con = content_tx.getText().toString();
            emotionPanel_bg.setVisibility(View.GONE);

            if (("").equals(con)) {
                Toast.makeText(context, "评论信息不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            MyUser uu = new MyUser();

            try {

                uu.setObjectId(jsonObject.getString("user_id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Comment myComm = new Comment();

            myComm.setUser_id(userInfo);

            myComm.setToWho(uu);

            myComm.setComContent(con);
            myComm.setQuest_id(question_id);

            myComm.save(context, new SaveListener() {

                @Override
                public void onSuccess() {
                    // 这里需要触发刷新，刷新新的数据到列表
                    // 可以通过刚才提出的问题，直接放到对应的list，再显示出来
                    getInfo.listCb(new getCallbackList());
                    // 调用云端方法进行查询
                    getInfo.getComm(question_id);

                    getRex.reflash();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(content_tx.getWindowToken(), 0);

                    content_tx.setText("");

                    Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    Toast.makeText(context,
                            "发生错误 \n 错误代码：" + arg0 + " 错误信息 " + arg1,
                            Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    // 回调函数注册
    public class getCallbackList implements listCallback {

        @Override
        public void getQuestComm(List<Comment4list> mylist) {
//            progressLayout.setVisibility(View.GONE);
            progressDialog.dismiss();
            // 需要先插入用户信息，问题、用户信息
            if (null == mylist) {

                Toast.makeText(context, "没有新的评论或回复", Toast.LENGTH_SHORT).show();

            } else {

                questionObj.setWealth(mylist.get(0).getWealth());

                mylist.remove(0);
                mylist.add(0, questionObj);
                commAdapter.setList(mylist);
                commAdapter.notifyDataSetChanged();

            }

        }

    }

//    @Override
//    protected void onPause() {
//        overridePendingTransition(R.anim.enter_from_left_animation,
//                R.anim.exit_to_right_animaion);
//        super.onPause();
//    }

    // 定义一个接口，用作在异步任务回调，回传参数
    public interface listCallback {

        public void getQuestComm(List<Comment4list> mylist);
    }
}
