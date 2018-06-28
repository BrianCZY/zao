package com.hzu.zao.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzu.zao.BigPicActivity;
import com.hzu.zao.LoginActivity;
import com.hzu.zao.R;
import com.hzu.zao.WanToAnsActivity;
import com.hzu.zao.WanToAnsDetailActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.utils.DataFormateUtils;
import com.hzu.zao.utils.DisplayUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.utils.StringUtils;
import com.hzu.zao.view.MultiImageView.MultiImageView;
import com.hzu.zao.view.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * 我要回答适配器
 *
 * @author Nearby Yang
 *         <p/>
 *         Create at 2015-4-29 下午11:16:54
 */
public class WanToAnsAdapter extends BaseAdapter {

    private WanToAnsActivity context;
    private JSONArray jsonArray = null;
    private JSONObject jsonObject = null;
    private XListView listView;
    private int size = 0;
    private int number = 0;
    private Dialog myDialog;
    private BmobUser currentUser;

    public WanToAnsAdapter(WanToAnsActivity context, XListView listView) {

        this.context = context;
        this.listView = listView;

        currentUser = BmobUser.getCurrentUser(context);

    }

    // 数据源
    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    // //上拉刷新设置
    // public void setNumber(int number){
    //
    // this.number=number;
    //
    // Log.i("number", number+"");
    //
    // }

    @Override
    public int getCount() {

        if (null != jsonArray) {
            size = jsonArray.length();

            // if(size>10){
            // size=10;
            // }
            //
        }
        // Log.i("size", size + "");

        return size;
    }

    @Override
    public Object getItem(int position) {
        JSONObject tempObject = null;
        try {
            tempObject = jsonArray.getJSONObject(jsonArray.length() - position
                    - 1);

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return tempObject;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Log.i("position ", position+"");

        ViewHolder hodler = null;
        // ***********************position=0开始************************************

        if (convertView == null) {
            hodler = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(
                    R.layout.wantoans_list_item_card, null);

            hodler.im_icon = (ImageView) convertView
                    .findViewById(R.id.user_icon_wanToAns);
            hodler.userName = (TextView) convertView
                    .findViewById(R.id.name_wanToAns);
            hodler.im_sex = (ImageView) convertView
                    .findViewById(R.id.sex_wanToAns);
            hodler.userQuestion = (TextView) convertView
                    .findViewById(R.id.userQuestion_wanToAns);
            hodler.creat_date = (TextView) convertView
                    .findViewById(R.id.creat_date_wanToAns);
            hodler.multiImageView = (MultiImageView) convertView.findViewById(R.id.miv_want_ans);

            convertView.setTag(hodler);

        } else {
            hodler = (ViewHolder) convertView.getTag();

        }

        // 数据源
        try {
            jsonObject = jsonArray.getJSONObject(jsonArray.length() - position
                    - 1);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        // Log.i("item 数据源", jsonArray.length() + " ------- " + position + "");
        // 用户昵称

        try {
            // 获取数据

            hodler.userName.setText(jsonObject.getString("nickName"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 创建时间
        try {
            hodler.creat_date.setText(jsonObject.getString("createdAt"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 问题内容
        try {
            String content = jsonObject.getString("content");
            hodler.userQuestion.setText(!TextUtils.isEmpty(content) ? StringUtils.getEmotionContent(context, content) : "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 性别
        try {
            if (("F").equals(jsonObject.getString("sex"))) {

                hodler.im_sex.setBackgroundResource(R.drawable.woman_icon);
            } else {

                hodler.im_sex.setBackgroundResource(R.drawable.man_icon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // *********************异步加载图片icon********************

        String iconUrl = "";

        try {
            iconUrl = jsonObject.getString("icon");
            // iconUrl = jsonObject.getString("icon");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log.i("url ", iconUrl);

//        hodler.im_icon.setTag(iconUrl);
        ImageLoader.getInstance().displayImage(iconUrl, hodler.im_icon);

        JSONArray imageArray = new JSONArray();
        try {
            imageArray = jsonObject.getJSONArray("images");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (imageArray != null) {
            hodler.multiImageView.setVisibility(View.VISIBLE);
            final List<String> list = new ArrayList<>();

            for (int i = 0; i < imageArray.length(); i++) {

                try {
                    list.add(imageArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ViewGroup.LayoutParams layoutParams = hodler.multiImageView.getLayoutParams();
            layoutParams.width = DisplayUtils.getScreenWidthPixels(context) / 3 * 2;

            hodler.multiImageView.setList(list);
            hodler.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(context, list);

                    EvaluateUtil.setupCoords(context, (ImageView) view, pictureInfoList, position);
                    Intent intent = new Intent(context, BigPicActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putSerializable(Contants.INTENT_IMAGE_INFO_LIST, (Serializable) pictureInfoList);
                    intent.putExtras(bundle);
                    intent.putExtra(Contants.INTENT_CURRENT_ITEM, position);

                    context.startActivity(intent);
                    context.overridePendingTransition(0, 0);
                }
            });
        } else {
            hodler.multiImageView.setVisibility(View.GONE);
        }


        // 对应item的点击事件
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (null != currentUser) {// 用户已经登录

                    Intent intents = new Intent();

                    intents.setClass(context, WanToAnsDetailActivity.class);
                    String strJSON = "";
                    // ListView具有header，item从1开始计算
                    try {
                        strJSON = jsonArray.getJSONObject(
                                jsonArray.length() - position).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Log.i("点击事件", strJSON);

                    intents.putExtra("num", strJSON);

                    context.startActivity(intents);

                } else {// 用户还没登录

                    loginDialog();
                }

            }
        });
        return convertView;

    }

    // 登陆确认
    public void loginDialog() {

        TextView cancel;
        TextView config;

        LayoutInflater inflater = LayoutInflater.from(context);
        View myView = inflater.inflate(R.layout.login_dialog, null);

        cancel = (TextView) myView.findViewById(R.id.login_cancel_tv);
        config = (TextView) myView.findViewById(R.id.login_sure_tv);

        myDialog = new AlertDialog.Builder(context).setView(myView).create();

        cancel.setOnClickListener(new BtnOnClickListener());

        config.setOnClickListener(new BtnOnClickListener());

        myDialog.show();

    }

    // 弹窗的点击事件
    private class BtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {


            switch (v.getId()) {

                case R.id.login_cancel_tv:
                    myDialog.dismiss();
                    break;

                case R.id.login_sure_tv:
                    myDialog.dismiss();

                    context.startActivity(new Intent(context, LoginActivity.class));

                    context.overridePendingTransition(
                            R.anim.enter_from_right_animation,
                            R.anim.exit_to_left_animation);

                    break;

            }

        }

    }

    private class ViewHolder {
        ImageView im_icon;// 用户头像
        ImageView im_sex;// 用户性别
        TextView userName;// 用户昵称
        TextView creat_date;// 创建时间
        TextView userQuestion;// 用户提出的问题
        MultiImageView multiImageView;

    }

}
