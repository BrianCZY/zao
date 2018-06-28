package com.hzu.zao.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.zao.BigPicActivity;
import com.hzu.zao.R;
import com.hzu.zao.WanToAnsDetailActivity;
import com.hzu.zao.WanToAnsDetailActivity.getCallbackList;
import com.hzu.zao.config.Contants;
import com.hzu.zao.model.Comment;
import com.hzu.zao.model.Comment4list;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.model.Reply;
import com.hzu.zao.utils.DataFormateUtils;
import com.hzu.zao.utils.DisplayUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.utils.GetUserQuestion;
import com.hzu.zao.utils.StringUtils;
import com.hzu.zao.view.MultiImageView.MultiImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Nearby Yang on 2015-04-08. 使用ListView的Item分类功能：将评论和回复分开，从而实现评论列表
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CommAdapter extends BaseAdapter {

    private WanToAnsDetailActivity context;
    private List<Comment4list> dataList;

    private LayoutInflater myInflater;

    private ListView comm_litView;
    private Dialog myDialog;

    private EditText replyContent;// 回复的弹窗的输入框

    private MyUser userInfo = null;

    private UIWidget fir_Widget = null;// 问题
    private UIWidget par_Widget = null;// 评论
    private UIWidget chi_Widget = null;// 回复

    private boolean isCurrentUser = false;// 用作判断当前用户是否为问题的主人
    private boolean isBested = false;// 用作判断是否已经选过是最佳答案

    private String objectId_old = "";// 原来的最佳选项的objectId

    private GetUserQuestion getInfo = null;

    private getCallbackList getCall;

    private Comment4list commDatalist = null;


    public CommAdapter(WanToAnsDetailActivity context, ListView listView,
                       getCallbackList getcall) {

        this.context = context;
        this.comm_litView = listView;
        this.getCall = getcall;

        myInflater = LayoutInflater.from(context);
        userInfo = BmobUser.getCurrentUser(context, MyUser.class);
        getInfo = new GetUserQuestion(context);
        dataList = new ArrayList<Comment4list>();

    }

    // 用作调用该Adapter的Activity的数据传输、处理
    // ==================Star===========================
    public void setList(List<Comment4list> list) {
        this.dataList = list;

    }

    public List<Comment4list> getDataList() {
        return dataList;

    }

    public void addList(List<Comment4list> list) {
        this.dataList.addAll(list);
    }

    public void removeItem(int position) {
        if (dataList.size() > 0) {
            dataList.remove(position);
        }

    }

    public void clearList() {
        this.dataList.clear();

    }

    // ==============END===============

    // ++++++++++++++++++++++++++++++++++

    // ++++++++++++++++BaseAdapter++++++++++++++++++++

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {

        // Log.i("itemObject ", dataList.get(position).toString() + "");

        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {

        // Log.i("itemId", position + "");

        return position;
    }

    // 这里设置3种类型
    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {

        // Log.i("type", "" + dataList.get(position).getType());

        return dataList.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        commDatalist = dataList.get(position);

        String url = "";
        ImageView userIconIv = null;// 用户头像

        // 实例化对应界面的控件
        switch (getItemViewType(position)) {

            case Contants.ITEM_FIRST:// 第一项 问题详情

                if (convertView == null) {

                    convertView = myInflater.inflate(R.layout.comment_1st, null);

                    fir_Widget = new UIWidget();

                    fir_Widget.item_userName4 = (TextView) convertView
                            .findViewById(R.id.wand_username);// 提问题用户名

                    fir_Widget.item_userSex = (ImageView) convertView
                            .findViewById(R.id.wand_sex);// 用户时间

                    fir_Widget.item_createdAt = (TextView) convertView
                            .findViewById(R.id.wand_date);// 创建时间

                    fir_Widget.item_Content = (TextView) convertView
                            .findViewById(R.id.wand_content);// 问题内容

                    fir_Widget.item_userIcon = (ImageView) convertView
                            .findViewById(R.id.wand_user_icon);// 用户头像

                    fir_Widget.multiImageView = (MultiImageView) convertView.findViewById(R.id.miv_want_ans_detail);
                    ViewGroup.LayoutParams params = fir_Widget.multiImageView.getLayoutParams();
                    params.width = DisplayUtils.getScreenWidthPixels(context) / 3 * 2;

                    convertView.setTag(R.layout.comment_1st, fir_Widget);

                } else {

                    fir_Widget = (UIWidget) convertView
                            .getTag(R.layout.comment_1st);

                }

                // =======设置数据=========

                fir_Widget.item_userName4.setText(commDatalist.getNickName()); // 提问题用户名

                if (("M").equals(commDatalist.getUserSex())) {

                    fir_Widget.item_userSex
                            .setBackgroundResource(R.drawable.man_icon);// 用户性别
                } else {
                    fir_Widget.item_userSex
                            .setBackgroundResource(R.drawable.woman_icon);// 用户性别
                }

                // 用作判断当前用户是否为问题的主人
                if ((userInfo.getObjectId()).equals(commDatalist.getUser_id())) {
                    isCurrentUser = true;
                }

                fir_Widget.item_createdAt.setText(commDatalist.getCreatAt());// 创建时间

                fir_Widget.item_Content.setText(StringUtils.getEmotionContent(context, commDatalist.getComContent()));// 问题内容

                url = commDatalist.getUserIcon();
                //image
                if (commDatalist.getImages() != null) {
                    fir_Widget.multiImageView.setList(commDatalist.getImages());
                    fir_Widget.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(context, commDatalist.getImages());

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

                }
                userIconIv = fir_Widget.item_userIcon;


                break;

            case Contants.ITEM_PARENT:// 父项---评论

                if (convertView == null) {

                    convertView = myInflater.inflate(R.layout.comment_parent, null);

                    par_Widget = new UIWidget();

                    par_Widget.item_userIcon = (ImageView) convertView
                            .findViewById(R.id.user_icon_ques);// 用户头像

                    par_Widget.item_userName4 = (TextView) convertView
                            .findViewById(R.id.cpar_4username);// 发出评论用户的用户名

                    par_Widget.item_Content = (TextView) convertView
                            .findViewById(R.id.par_replycontent);// 评论内容

                    par_Widget.item_createdAt = (TextView) convertView
                            .findViewById(R.id.cper_createdAt);// 创建时间

                    // 点赞-按钮
                    par_Widget.goodLy = (LinearLayout) convertView
                            .findViewById(R.id.par_linear_good);

                    // 点踩-按钮
                    par_Widget.badLy = (LinearLayout) convertView
                            .findViewById(R.id.par_linear_bad);

                    // 显示是否点击过：点赞或者糟糕的
                    par_Widget.goodImv = (ImageView) convertView
                            .findViewById(R.id.par_good_im);

                    par_Widget.badImv = (ImageView) convertView
                            .findViewById(R.id.par_bad_im);
                    // 最赞的
                    par_Widget.bestImv = (ImageView) convertView
                            .findViewById(R.id.cper_best_Im);

                    // good数量
                    par_Widget.goodQuantTx = (TextView) convertView
                            .findViewById(R.id.good_num);
                    // bad数量
                    par_Widget.badQuantTx = (TextView) convertView
                            .findViewById(R.id.bad_num);

                    convertView.setTag(R.layout.comment_parent, par_Widget);

                } else {

                    par_Widget = (UIWidget) convertView
                            .getTag(R.layout.comment_parent);

                }

                // 点赞图标-效果
                if (commDatalist.isGood()) {
                    par_Widget.goodImv.setBackgroundResource(R.drawable.good_press);
                } else {
                    par_Widget.goodImv.setBackgroundResource(R.drawable.good);

                }

                // 踩图标-效果
                if (commDatalist.isBad()) {
                    par_Widget.badImv.setBackgroundResource(R.drawable.bad_press);
                } else {
                    par_Widget.badImv.setBackgroundResource(R.drawable.bad);

                }

                // 点赞数量
                if (commDatalist.getGood() > 0) {
                    par_Widget.goodQuantTx.setText(commDatalist.getGood() + "");
                    par_Widget.goodQuantTx
                            .setTextColor(Color.parseColor("#F84241"));
                } else {
                    par_Widget.goodQuantTx.setText(0 + "");
                    par_Widget.goodQuantTx
                            .setTextColor(Color.parseColor("#000000"));
                }

                // 点踩数量
                if (commDatalist.getBad() > 0) {

                    par_Widget.badQuantTx.setText(commDatalist.getBad() + "");
                    par_Widget.badQuantTx.setTextColor(Color.parseColor("#F84241"));

                } else {

                    par_Widget.badQuantTx.setText(0 + "");
                    par_Widget.badQuantTx.setTextColor(Color.parseColor("#000000"));

                }

                par_Widget.goodLy
                        .setOnClickListener(new clickListener(commDatalist));// 点赞

                par_Widget.badLy
                        .setOnClickListener(new clickListener(commDatalist));// 糟糕的

                par_Widget.item_Content.setOnClickListener(new clickListener(
                        commDatalist));// 点击回复

                if (isCurrentUser) {// 是否为问题主人

                    if (commDatalist.isBest()) {

                        par_Widget.bestImv
                                .setBackgroundResource(R.drawable.recomment_true);

                    } else {

                        par_Widget.bestImv
                                .setBackgroundResource(R.drawable.recomment_false);

                    }

                    par_Widget.bestImv.setVisibility(View.VISIBLE);

                    par_Widget.bestImv.setOnClickListener(new clickListener(
                            commDatalist));

                } else {
                    //
                    if (commDatalist.isBest()) {

                        par_Widget.bestImv
                                .setBackgroundResource(R.drawable.recomment_true);

                        par_Widget.bestImv.setVisibility(View.VISIBLE);
                    }
                }

                // ======设置数据========
                par_Widget.item_userName4.setText(commDatalist.getNickName());

                par_Widget.item_Content.setText(StringUtils.getEmotionContent(context, commDatalist.getComContent()));

                // comm_id = commDatalist.getComm_id();

                par_Widget.item_createdAt.setText(commDatalist.getCreatAt());

                url = commDatalist.getUserIcon();

                // 头像
                par_Widget.item_userIcon.setTag(url);

                // //先判断-----显示是否点击过：点赞或者糟糕的

                userIconIv = par_Widget.item_userIcon;

                break;

            case Contants.ITEM_CHIND:// 子项---回复

                if (null == convertView) {

                    convertView = myInflater.inflate(R.layout.comment_child, null);
                    chi_Widget = new UIWidget();

                    // 发出回复用户头像
                    chi_Widget.item_userIcon = (ImageView) convertView
                            .findViewById(R.id.chil_user_icon_ques);

                    // 发出回复用户
                    chi_Widget.item_userName4 = (TextView) convertView
                            .findViewById(R.id.chile_4username);

                    // 收到回复用户
                    chi_Widget.item_UserName2 = (TextView) convertView
                            .findViewById(R.id.chile_2username);

                    // 回复内容
                    chi_Widget.item_Content = (TextView) convertView
                            .findViewById(R.id.chile_replycontent);

                    // 创建时间
                    chi_Widget.item_createdAt = (TextView) convertView
                            .findViewById(R.id.chile_createdAt);

                    convertView.setTag(R.layout.comment_child, chi_Widget);

                } else {

                    chi_Widget = (UIWidget) convertView
                            .getTag(R.layout.comment_child);

                }

                // 点击回复
                chi_Widget.item_Content.setOnClickListener(new clickListener(
                        commDatalist));

                chi_Widget.item_userName4.setText(commDatalist.getUserName4());// 发出回复者

                chi_Widget.item_UserName2.setText(commDatalist.getToWho());// 收到回复者

                chi_Widget.item_Content.setText(StringUtils.getEmotionContent(context, commDatalist.getReplyContent()));
                chi_Widget.item_createdAt.setText(commDatalist.getCreatAt());

                // 头像
                url = commDatalist.getUserIcon();

                chi_Widget.item_userIcon.setTag(url);

                userIconIv = chi_Widget.item_userIcon;

                break;
        }

        ImageLoader.getInstance().displayImage(url, userIconIv);


        return convertView;

    }

    // ================最佳答案=====================
    @SuppressLint("NewApi")
    private void isbestAns(Comment4list isBestCommlist) {

        // int wealth = 0;// 金币
        // String userObjectId = "";// 用户id
        String objectId_new = "";// 新的最佳选项的objectId

        // Log.i("list", isBestCommlist.getComm_id()+"");

        Comment comment = new Comment();

        // =========== 是否已经为最佳答案==============
        if (isBestCommlist.isBest()) {

            Toast.makeText(context, R.string.bestAnsed, Toast.LENGTH_SHORT)
                    .show();

        } else {

            for (int yy = 0; yy < dataList.size(); yy++) {

                if (dataList.get(yy).getType() == Contants.ITEM_PARENT) {// 是否为评论

                    if (dataList.get(yy).isBest()) {

                        objectId_old = dataList.get(yy).getComm_id();
                        dataList.get(yy).setBest(false);

                        isBested = true;
                        // Log.i("commAdapter best",
                        // dataList.get(yy).isBest()+"");

                    }

                }

            }



            objectId_new = isBestCommlist.getComm_id();

            isBestCommlist.setBest(true);

//			Log.i("objectId", "新的 " + objectId_new + " 原来的 " + objectId_old);

            // ==============推荐回答====================

            comment.setBest(true);
            notifyDataSetChanged();
            comment.update(context, objectId_new, new UpdateListener() {

                @Override
                public void onSuccess() {

                    // Log.i("old",
                    // (null!=objectId_old)+"---"+(""!=objectId_old));

                    if (!Objects.equals("", objectId_old)) {
                        Comment rmBest = new Comment();

                        rmBest.setBest(false);
                        rmBest.update(context, objectId_old,
                                new UpdateListener() {

                                    @Override
                                    public void onSuccess() {

                                        getInfo.listCb(getCall);
                                        // 调用云端方法进行查询
                                        getInfo.getComm(dataList.get(0)
                                                .getQuest_id());

                                        Toast.makeText(context,
                                                R.string.bestAns,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int arg0, String arg1) {
                                        Toast.makeText(context,
                                                R.string.serviceBusy,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(context, R.string.bestAns,
                                Toast.LENGTH_SHORT).show();
                        getInfo.listCb(getCall);
                        // 调用云端方法进行查询
                        getInfo.getComm(dataList.get(0).getQuest_id());
                    }

                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    Toast.makeText(context, R.string.serviceBusy,
                            Toast.LENGTH_SHORT).show();
//					Log.i("失败", "code " + arg0 + " msg " + arg1);
                }
            });

            // Log.i(" ! isBeat", ""+(!isBested));

            // ==================金币============
            if (!isBested) {

                isBested = false;
                // 问题主人id
                // userObjId.add(dataList.get(0).getUser_id());
                // 问题让主人金币
                // userWealth.add(dataList.get(0).getWealth());

                // if (null != userObjId) {// 是否为第一次选择最佳答案，第二次选择无效

                for (int vv = 0; vv < dataList.size(); vv++) {

                    JSONObject param = new JSONObject();

                    // userObjectId = userObjId.get(vv);

                    // wealth = userWealth.get(vv) + 1;
                    // Log.i("wealth ", dataList.get(vv).getWealth()+"");

                    try {
                        param.put("userId", dataList.get(vv).getUser_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    int ww = dataList.get(vv).getWealth() + 1;
                    try {
                        param.put("wealth", ww);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dataList.get(vv).setWealth(ww);

                    AsyncCustomEndpoints asyncCode = new AsyncCustomEndpoints();

                    asyncCode.callEndpoint(context,
                            Contants.UPDATAWEALTH_COULDCODE, param,
                            new CloudCodeListener() {

                                @Override
                                public void onSuccess(Object arg0) {

                                    notifyDataSetChanged();

                                    getInfo.listCb(getCall);
                                    // 调用云端方法进行查询
                                    getInfo.getComm(dataList.get(0)
                                            .getQuest_id());
                                    // Log.i("success", "" + arg0.toString());
                                }

                                @Override
                                public void onFailure(int arg0, String arg1) {
                                    // Log.i("faile", "code " + arg0 + " msg "
                                    // + arg1);

                                }
                            });
                }

                // }
            }

        }

    }

    // ===========回复的弹窗=================
    private void initDialog(Comment4list comm) {

        TextView cancelTx, sendTx;

        View dialogView = myInflater.inflate(R.layout.comment_dialog, null);

        myDialog = new AlertDialog.Builder(context).setView(dialogView)
                .create();

        cancelTx = (TextView) dialogView.findViewById(R.id.dialog_bt_concel);

        sendTx = (TextView) dialogView.findViewById(R.id.dialog_bt_ok);

        replyContent = (EditText) dialogView.findViewById(R.id.dialog_et);

        cancelTx.setOnClickListener(new clickListener(comm));

        sendTx.setOnClickListener(new clickListener(comm));

        myDialog.show();
    }

    // =================点击事件监听================
    private class clickListener implements OnClickListener {

        Comment4list commlist;
        Comment fb = new Comment();

        int goodNumber = 0;
        int badNumber = 0;

        public clickListener(Comment4list commData) {

            this.commlist = commData;

            goodNumber = commlist.getGood();
            badNumber = commlist.getBad();

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.par_replycontent:

                    initDialog(commlist);

                    break;
                case R.id.chile_replycontent:

                    initDialog(commlist);
                    break;

                case R.id.dialog_bt_ok:// 发送回复

                    String Content = replyContent.getText().toString();

                    Reply reply = new Reply();

                    if (("").equals(Content)) {
                        Toast.makeText(context, "输入内容不能为空", Toast.LENGTH_SHORT)
                                .show();

                    } else {

                        // 设置回复
                        MyUser reply2user = new MyUser();

                        reply2user.setObjectId(commlist.getUser_id());

                        reply.setUser_id(userInfo);// 发出回复用户

                        reply.setComContent(Content);// 回复内容

                        reply.setComm_id(commlist.getComm_id());// 对应评论的评论id

                        reply.setToWho(reply2user);// 收到回复的用户id

                        reply.save(context, new SaveListener() {

                            @Override
                            public void onSuccess() {

                                getInfo.listCb(getCall);
                                // 调用云端方法进行查询
                                getInfo.getComm(dataList.get(0).getQuest_id());

                                Toast.makeText(context, "回复成功", Toast.LENGTH_SHORT)
                                        .show();

                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {
                                Toast.makeText(context, "回复失败 \n 错误信息： " + arg1,
                                        Toast.LENGTH_SHORT).show();

                            }
                        });

                        myDialog.dismiss();

                    }

                    break;
                case R.id.dialog_bt_concel:

                    myDialog.dismiss();

                    break;

                case R.id.par_linear_good:// 点赞

                    // 只能点赞或者是拍砖 isGood isBad

                    if (commlist.isGood()) {// 已经点赞

                        Toast.makeText(context, "已经为他点过赞了！", Toast.LENGTH_SHORT)
                                .show();

                    } else {// 未点赞

                        /**
                         * 添加点赞
                         *
                         * 在isGood数组中添加当前用户id
                         */

                        // 评论ObjectId
                        fb.setObjectId(commlist.getComm_id());
                        fb.addUnique("isGood", userInfo.getObjectId());

                        fb.update(context, commlist.getComm_id(),
                                new UpdateListener() {

                                    @Override
                                    public void onSuccess() {

                                        // Log.i("image", "" + par_Widget.goodImv);
                                        commlist.setGood(true);
                                        commlist.setGood(goodNumber + 1);

                                        notifyDataSetChanged();

                                        Toast.makeText(context, "成功为他点赞！",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int arg0, String arg1) {

                                        Toast.makeText(context,
                                                R.string.serviceBusy,
                                                Toast.LENGTH_SHORT).show();

                                        // Log.i("添加用户id失败", "错误代码  " + arg0
                                        // + " 错误信息 " + arg1);

                                    }
                                });

                        /**
                         * 删除点踩
                         *
                         * 在isBad数组删除档期那用户的id
                         */
                        if (commlist.isBad()) {

                            fb.setObjectId(commlist.getComm_id());
                            fb.removeAll("isBad",
                                    Arrays.asList(userInfo.getObjectId()));

                            fb.update(context, new UpdateListener() {

                                @Override
                                public void onSuccess() {

                                    commlist.setBad(false);
                                    commlist.setBad(badNumber - 1);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(int arg0, String arg1) {

                                    Toast.makeText(context, R.string.serviceBusy,
                                            Toast.LENGTH_SHORT).show();
                                    // Log.i("移除用户id失败", "错误代码  " + arg0
                                    // + " 错误信息 " + arg1);

                                }

                            });
                        }

                    }

                    break;

                case R.id.par_linear_bad:// 拍砖

                    /**
                     * 添加-拍砖
                     *
                     */
                    //
                    // 只能点赞或者是拍砖
                    if (commlist.isBad()) {// 已经点过踩

                        Toast.makeText(context, "已经为他拍过砖了！", Toast.LENGTH_SHORT)
                                .show();

                    } else {

                        fb.setObjectId(commlist.getComm_id());

                        fb.addUnique("isBad", userInfo.getObjectId());

                        fb.update(context, new UpdateListener() {

                            @Override
                            public void onSuccess() {

                                commlist.setBad(true);
                                commlist.setBad(badNumber + 1);

                                notifyDataSetChanged();
                                Toast.makeText(context, "成功为他拍砖！",
                                        Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {

                                Toast.makeText(context, R.string.serviceBusy,
                                        Toast.LENGTH_SHORT).show();

                                // Log.i("拍砖-添加用户id失败", "错误代码  " + arg0 + " 错误信息 "
                                // + arg1);

                            }
                        });

                        /**
                         * 删除-点赞
                         *
                         */
                        if (commlist.isGood()) {

                            fb.removeAll("isGood",
                                    Arrays.asList(userInfo.getObjectId()));

                            fb.setObjectId(commlist.getComm_id());

                            fb.update(context, new UpdateListener() {

                                @Override
                                public void onSuccess() {

                                    commlist.setGood(false);
                                    commlist.setGood(goodNumber - 1);

                                    notifyDataSetChanged();

                                }

                                @Override
                                public void onFailure(int arg0, String arg1) {

                                    Toast.makeText(context, R.string.serviceBusy,
                                            Toast.LENGTH_SHORT).show();

                                    // Log.i("拍砖-移除用户id失败", "错误代码  " + arg0 +
                                    // " 错误信息 "
                                    // + arg1);

                                }
                            });
                        }
                    }

                    break;

                case R.id.cper_best_Im:// 最佳答案
                    isbestAns(commlist);
                    // Toast.makeText(context, "点击", Toast.LENGTH_SHORT).show();

                    break;
            }

        }
    }

    public void hideSoftKeyword(TextView tv) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);

        tv.setText("");
    }

    // ==========要用到的界面控件==============
    private class UIWidget {
        private TextView item_UserName2;
        private TextView item_userName4;
        private TextView item_createdAt;
        private TextView item_Content;
        private ImageView item_userIcon;
        private ImageView item_userSex;
        private LinearLayout goodLy;
        private LinearLayout badLy;
        private ImageView goodImv;
        private ImageView badImv;
        private ImageView bestImv;
        private TextView goodQuantTx;
        private TextView badQuantTx;
        private MultiImageView multiImageView;

    }

}
