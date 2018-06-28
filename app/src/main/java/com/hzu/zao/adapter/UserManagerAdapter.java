package com.hzu.zao.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzu.zao.R;
import com.hzu.zao.adapter.baseAdapter.ViewHolder;
import com.hzu.zao.interfaces.AsyncListener;
import com.hzu.zao.model.BaseModel;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.network.BmobApi;
import com.hzu.zao.utils.DateUtils;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.view.CircleImageView;
import com.hzu.zao.view.numberPicker.NumberPickerDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.GetServerTimeListener;

/**
 * 用户管理
 * <p>
 * Created by Nearby Yang on 2016-04-25.
 */
public class UserManagerAdapter extends com.hzu.zao.adapter.baseAdapter.CommAdapter<MyUser> {

    private String hoursForLogin = "还有%d小时后才能登录";
    private NumberPickerDialog numberPickerDialog;
    private BmobDate bmobDate;
    private String times;
    private Date currentDate;


    public UserManagerAdapter(Context context) {
        super(context);
        numberPickerDialog = new NumberPickerDialog(context);
        Bmob.getServerTime(context, new GetServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                currentDate = new Date(time * 1000L);
                times = formatter.format(currentDate);
                LogUtils.i("bmob", "当前服务器时间为:" + times);
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.i("bmob", "获取服务器时间失败:" + msg);
            }
        });
    }


    @Override
    public void convert(ViewHolder holder, MyUser myUser, int position) {
        CircleImageView avatar = holder.getView(R.id.user_icon_ques);
        TextView nickName = holder.getView(R.id.name);
        ImageView gender = holder.getView(R.id.sex);
        ImageView forceLogin = holder.getView(R.id.im_force_login);
        TextView loginDate = holder.getView(R.id.txt_login_date);

        ImageLoader.getInstance().displayImage(myUser.getIcon().getFileUrl(ctx), avatar);
        nickName.setText(!TextUtils.isEmpty(myUser.getNickName()) ? myUser.getNickName() : myUser.getUsername());
        gender.setImageResource("M".equals(myUser.getSex()) ? R.drawable.man_icon : R.drawable.woman_icon);
        int hours = 0;
//        LogUtils.e("curr" + DateUtils.getCurrentStringDate());
        if (myUser.getLimitDate() != null) {
//            LogUtils.e(" limiDate = " + myUser.getLimitDate().getDate());
            hours = DateUtils.hourBetween(times, myUser.getLimitDate().getDate());
        }

        boolean isfuture = hours > 1;

//        LogUtils.e("isfuture " + isfuture + " limiDate = " + myUser.getLimitDate());
        forceLogin.setImageResource(isfuture ?
                R.drawable.bmob_update_btn_check_on_holo_light :
                R.drawable.bmob_update_btn_check_off_holo_light);

        if (isfuture) {

            loginDate.setVisibility(View.VISIBLE);
            loginDate.setText(String.format(hoursForLogin, hours));
            LogUtils.e("hours = " + loginDate.getText());

        } else {
            loginDate.setVisibility(View.GONE);
        }

        forceLogin.setOnClickListener(new MyClick(position, isfuture, forceLogin));

    }

    @Override
    public int getlayoutid(int position) {
        return R.layout.item_user_manager;
    }

    /**
     * 点击事件
     */
    private class MyClick implements View.OnClickListener {
        private int position;
        private boolean isfuture;
        private ImageView forceLogin;

        public MyClick(int position, boolean isfuture, ImageView forceLogin) {
            this.position = position;
            this.isfuture = isfuture;
            this.forceLogin = forceLogin;

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.im_force_login:

                    if (isfuture) {//已经是禁言了

                        //取消禁言
                        forceLogin.setImageResource(R.drawable.bmob_update_btn_check_off_holo_light);
                        LogUtils.e("current " + DateUtils.getgetCurrentDate());
                        bmobDate = new BmobDate(currentDate);
//限制用户登录功能
                        updateUser(dataList.get(position));
                    } else {//选择禁言时间
//选择禁言时间
                        selectForceLoginDate(dataList.get(position));
                        forceLogin.setImageResource(R.drawable.bmob_update_btn_check_on_holo_light);
                    }
//                    dataList.get(position)
                    break;

            }
        }

    }

    /**
     * 选择禁言时间
     *
     * @param myUser
     */
    private void selectForceLoginDate(final MyUser myUser) {


        //按钮结果
        numberPickerDialog.setOnResultListener(new NumberPickerDialog.OnResultListener() {
            @Override
            public void onResult(boolean isSelected, int result) {
                if (isSelected) {
                    //进行了选择
                    bmobDate = new BmobDate(DateUtils.getNextDay(currentDate, result));
                    //限制用户登录功能
                    updateUser(myUser);
                } else {
//                    bmobDate = new BmobDate(currentDate);
                    notifyDataSetChanged();
                }


            }
        });

        numberPickerDialog.show();

    }

    /**
     * 更新用户的登陆
     *
     * @param user
     */
    private void updateUser(final MyUser user) {
//
//        MyUser newUser = new MyUser();
//        newUser.setLimitDate(bmobDate);
//        user.setLimitDate(bmobDate);
//        newUser.update(ctx, user.getObjectId(), new UpdateListener() {
//            @Override
//            public void onSuccess() {
//                LogUtils.d("更新用户登录限制成功");
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                LogUtils.e("更新 限制用户登陆时间失败 code = " + i + " message = " + s);
//            }
//        });
        JSONObject params = new JSONObject();
        try {
            params.put("objectId", user.getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            params.put("limitDate", bmobDate.getDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BmobApi.AsyncFunction(ctx, params, BmobApi.FUNCTION_UPDATE_USERLOGIN, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                BaseModel baseModel = (BaseModel) object;
                if (baseModel.getCode() == BaseModel.FAILE) {//失败
                    LogUtils.e("更新 限制用户登陆时间失败  = " + object.toString());
                    toast("限制用户登录失败，请稍候再试");
                    notifyDataSetChanged();
                } else {//成功
                    user.setLimitDate(new BmobDate(currentDate));
                    notifyDataSetChanged();
                    LogUtils.d("更新 限制用户登陆时间成功  ");
                    toast("限制用户登录成功");

                }
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.e("更新 限制用户登陆时间失败 code = " + code + " message = " + msg);
            }
        });


    }

}
