package com.hzu.zao.utils;

import android.content.Context;
import android.widget.Toast;

import com.hzu.zao.config.Contants;
import com.hzu.zao.interfaces.MyCallback;
import com.hzu.zao.model.MyUser;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;

/**
 * 获取用户记录云端代码 将返回的json数据解析完成，并且存储到对应的缓存文件
 *
 * @author Nearby Yang
 *         <p/>
 *         Create at 2015 下午4:38:38
 */
public class GetUserRecord {

    private JSONObject param = null;// 上传参数
    private Context context;
    private JsonUtility jsonUtil;
    MyCallback mc = null;
    AsyncCustomEndpoints asyncCode;// 使用云端代码
    MyUser user;
    String UserId = null;// 用于存放当前用户的id
    String table = null;// 要查询的表

    public GetUserRecord(Context context, String myTable) {
        this.context = context;
        this.table = myTable;

        jsonUtil = new JsonUtility(context);
        asyncCode = new AsyncCustomEndpoints();
        user = BmobUser.getCurrentUser(context, MyUser.class);
    }

    // ==================回调函数==================
    public void myCallback(MyCallback mcb) {
        this.mc = mcb;
    }

    // ===================云端代码=====================
    public void AsyncCode() {

        param = new JSONObject();
        UserId = user.getObjectId();

        // 调用的方法
        try {
            param.put("type", "r");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 要查询的用户的id
        try {
            param.put("userId", UserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 要查询的表
        try {
            param.put("myTable", table);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        asyncCode.callEndpoint(context, Contants.COULDCODE, param,
                new CloudCodeListener() {

                    @Override
                    public void onSuccess(Object arg0) {
                        LogUtils.e("arg0 = " + arg0);
                        // Log.i("返回数据", arg0.toString());
                        if ((!("").equals(arg0.toString()))
                                && (!("-1").equals(arg0.toString()))) {

                            switch (table) {

                                case "UserQuestion":// 用户提问

                                    jsonUtil.GetMyQuestion(arg0.toString());

                                    break;

                                case "Comment":// 用户回答

                                    jsonUtil.GetMyComment(arg0.toString());

                                    break;

                                case "Experience":// 用户经验
                                    jsonUtil.GetMyExperience(arg0.toString());

                                    break;
                                default:
                                    break;

                            }

                            mc.getSuccess(true);

                        } else {

                            mc.getSuccess(false);
//							Toast.makeText(context, "暂无数据，请稍后再刷新",
//									Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {

                        mc.getSuccess(false);
                        if (arg0 != 9010) {
                            Toast.makeText(
                                    context,
                                    "服务器异常 \n" + "错误代码：" + arg0 + " 错误信息："
                                            + arg1, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 触发刷新本地缓存数据
    public void reflash() {

        param = new JSONObject();
        UserId = user.getObjectId();

        // 调用的方法
        try {
            param.put("type", "r");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 要查询的用户的id
        try {
            param.put("userId", UserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 要查询的表
        try {
            param.put("myTable", table);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        asyncCode.callEndpoint(context, Contants.COULDCODE, param,
                new CloudCodeListener() {

                    @Override
                    public void onSuccess(Object arg0) {

                        // Log.i("返回数据", arg0.toString());
                        if ((!("").equals(arg0.toString()))
                                && (!("-1").equals(arg0.toString()))) {

                            switch (table) {

                                case "UserQuestion":// 用户提问

                                    jsonUtil.GetMyQuestion(arg0.toString());

                                    break;

                                case "Comment":// 用户回答

                                    jsonUtil.GetMyComment(arg0.toString());

                                    break;

                                case "Experience":// 用户经验
                                    jsonUtil.GetMyExperience(arg0.toString());

                                    break;

                            }

                        } else {

                            // Toast.makeText(context, "暂无数据，请稍后再刷新",
                            // Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {

                        // if (arg0 != 9010) {
                        // Toast.makeText(
                        // context,
                        // "服务器异常 \n" + "错误代码：" + arg0 + " 错误信息："
                        // + arg1, Toast.LENGTH_SHORT).show();
                        // }
                    }
                });

    }

}
