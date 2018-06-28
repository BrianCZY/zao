package com.hzu.zao.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.jsonorder.sortJsonArrayByDate;
import com.hzu.zao.model.Comment4JSON;
import com.hzu.zao.model.Comment4JSON.CommentDetail;
import com.hzu.zao.model.Comment4JSON.ReplyDetail;
import com.hzu.zao.model.Comment4list;
import com.hzu.zao.model.Experience4JSON;
import com.hzu.zao.model.Experience4JSON.ResultDetail;
import com.hzu.zao.model.Experience4JSON.UserDetail;
import com.hzu.zao.model.Message4JSON;
import com.hzu.zao.model.Message4JSON.comm4replyDetail;
import com.hzu.zao.model.Message4JSON.commentDetail;
import com.hzu.zao.model.Message4JSON.questionDetail;
import com.hzu.zao.model.Message4JSON.replyDetail;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.model.UserQuertion4JSON;
import com.hzu.zao.model.UserQuertion4JSON.resultsDetail;
import com.hzu.zao.model.UserRecord.AnswerDetail;
import com.hzu.zao.model.UserRecord.ExDetail;
import com.hzu.zao.model.UserRecord.MyAnswer;
import com.hzu.zao.model.UserRecord.MyExperience;
import com.hzu.zao.model.UserRecord.MyQuestion;
import com.hzu.zao.model.UserRecord.QresultsDetail;
import com.hzu.zao.model.UserRecord.questDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * JSON 解析工具类
 *
 * @author Nearby Yang
 *         <p/>
 *         Create at 2015 下午1:23:49
 */
public class JsonUtility {

    private Gson gson = null;
    private Context context;
    private MyUser usrinfo = null;

    public JsonUtility(Context context) {
        this.context = context;
        // this.myAdapter = myAdapter;
        gson = new Gson();
        usrinfo = BmobUser.getCurrentUser(context, MyUser.class);
    }

    /**
     * 我来回答问题
     *
     * @param jsonString 云端代码执行返回数据
     */

    public void getQuestion(String jsonString) {

        JSONArray jsonArray = null;
        // JSONArray tp_jsonArray = null;
        JSONObject jsonObject = null;
        // JSONObject tm_jsonObject = null;
        JSONArray tempArray = null;
        MyUser userInfo = null;

        // String crAt_new = "";
        // String crAt_old = "";

        List<resultsDetail> results4json = null;

        // int number = 0;

        UserQuertion4JSON userQuestion = null;

        // Log.i("jsonString", jsonString);

        try {
            userQuestion = gson.fromJson(jsonString, UserQuertion4JSON.class);
        } catch (Exception e) {

            System.out.print("解析错误" + e);
        }

        results4json = userQuestion.results;

        // 检查本地是否有缓存

        tempArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.WANTOANS_KEY);

        // ------------------------------------------------------

        // Log.i("本地数据为空 ", (null == tempArray) + "");
        //
        // Log.i("tempArray ", tempArray + "");

        if (null == tempArray) {// 本地有缓存，先取出来，在缓存对象末尾插入新的数据

            jsonArray = new JSONArray();

        } else {// 本地没有缓存，重新开始缓存

            if (results4json.size() > 1) {

                // Log.i("rm前", "" + results4json.size());
                results4json.remove(results4json.size() - 1);
                // Log.i("rm后", "" + results4json.size());

                // 最终有没有数据
                if (results4json.size() > 0) {

                    jsonArray = tempArray;

                } else {

                    return;
                }

            } else {

                return;
            }

        }

        // Log.i("Question ", " size " + results4json.size());

        // 返回数据的长度
        int QuestionSize = results4json.size() - 1;

        for (int i = 0; i < results4json.size(); i++) {

            int order = QuestionSize - i;

            jsonObject = new JSONObject();

            // 问题id
            try {
                jsonObject.put("objectId", results4json.get(order).objectId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 问题内容
            try {
                jsonObject.put("content", results4json.get(order).content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 问题创建时间
            try {
                jsonObject.put("createdAt", results4json.get(order).createdAt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 问题更新时间
            try {
                jsonObject.put("updatedAt", results4json.get(order).updatedAt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (results4json.get(order).images != null && results4json.get(order).images.size() > 0) {
                JSONArray imageArray = new JSONArray(results4json.get(order).images);
                try {
                    jsonObject.put("images", imageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            userInfo = results4json.get(order).user_id;

            // Log.i("questionUrl ", userInfo + " -" +
            // " isNull "+("").equals(userInfo.icon.url) );

            // 提出问题的用户id
            try {
                jsonObject.put("user_id", userInfo.getObjectId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 用户名
            try {
                jsonObject.put("username", userInfo.getUsername());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // // 用户金币
            // try {
            // jsonObject.put("wealth", userInfo.wealth);
            // } catch (JSONException e) {
            // e.printStackTrace();
            // }
            // --------------------------------------------------------------------------------------------
            // ============== 用户头像=============
            //
            String iconUrl = userInfo.getIcon().getFileUrl(context);

            try {
                jsonObject.put("icon", iconUrl);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 用户昵称

            String NickName = userInfo.getNickName();

            if (null == NickName) {
                NickName = "无昵称";
            }

            try {
                jsonObject.put("nickName", NickName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 用户性别
            String gender = "";

            gender = userInfo.getSex();

            if (("").equals(gender) || null == gender) {
                gender = "M";
            }

            try {
                jsonObject.put("sex", gender);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);

        }

        //
        // Log.i("解析完成的数据", jsonArray.toString() + "");
        // ==================设置缓存===============

        if (jsonArray.length() > 0) {

            MyApplication.getInstance().getCacheInstance().put(Contants.WANTOANS_KEY, jsonArray);
        }

        // Log.i("running ", "888");
    }

    /**
     * 我的经验
     *
     * @param JSONArray
     */

    public void getExperience(String JSONArray) {

        String tempUserId = "";// 存放user_id用作对比

        JSONArray JsonArray = null;
        JSONObject jsonObject = null;
        JSONObject userJsonObject = null;
        JSONArray userJsonArray = null;
        Experience4JSON experience4JSON = null;

        try {

            experience4JSON = gson.fromJson(JSONArray, Experience4JSON.class);
        } catch (Exception e) {
            System.out.print("解析错误： " + e);

        }

        List<ResultDetail> experienceList = experience4JSON.Experience;
        List<MyUser> userInfo = experience4JSON.userInfo;

        // JsonArray = new JSONArray();

        JSONArray tempArray = MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.EXPERIENCE_KEY);

        if (null == tempArray) {// 原来有缓存，读取原来的缓存，为了能在后面直接继续在本地缓存插入数据，而不是覆盖掉
            JsonArray = new JSONArray();
        } else {
            // 判断原来的数据是否已经有了
            if (experienceList.size() > 1) {
                experienceList.remove(experienceList.size() - 1);
                if (experienceList.size() > 0) {
                    JsonArray = tempArray;
                } else {
                    return;
                }

            } else {// 更新数据的时候只有一条数据，这条数据是原来已经有的数据，此处不更新
                return;
            }
        }

        // ===========================将解析的数据放入JSONARRAY=============================

        // ========================经验的详细信息=========================
        // 倒序放入缓存
        int experienceSiz = experienceList.size() - 1;

        for (int i = 0; i < experienceList.size(); i++) {

            int experienceOrder = experienceSiz - i;
            jsonObject = new JSONObject();

            UserDetail userId = experienceList.get(i).user_id;

            // unlike
            try {
                jsonObject.put("bad", experienceList.get(experienceOrder).bad);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                jsonObject
                        .put("good", experienceList.get(experienceOrder).good);
            } catch (JSONException e) {
                e.printStackTrace();
            }// like

            try {
                jsonObject.put("objectId",
                        experienceList.get(experienceOrder).objectId);
            } catch (JSONException e) {
                e.printStackTrace();
            }// 经验的id

            try {
                jsonObject.put("shareEx",
                        experienceList.get(experienceOrder).shareEx);
            } catch (JSONException e) {
                e.printStackTrace();
            }// 分享经验的内容

            try {
                jsonObject.put("createdAt",
                        experienceList.get(experienceOrder).createdAt);
            } catch (JSONException e) {
                e.printStackTrace();
            }// 创建时间

            try {
                jsonObject.put("updatedAt",
                        experienceList.get(experienceOrder).updatedAt);
            } catch (JSONException e) {
                e.printStackTrace();
            }// 更新时间

//            LogUtils.e("image = " + experienceList.get(experienceOrder).images + "-------------------");
            if (experienceList.get(experienceOrder).images != null && experienceList.get(experienceOrder).images.size() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(experienceList.get(experienceOrder).images);
                    jsonObject.put("images", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            tempUserId = experienceList.get(experienceOrder).user_id.objectId;

            try {
                jsonObject.put("user_id", tempUserId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 匹配用户信息
            for (int j = 0; j < userInfo.size(); j++) {

                String id = userInfo.get(j).getObjectId();

                if (tempUserId.equals(id)) {

                    try {
                        jsonObject.put("icon", userInfo.get(j).getIcon().getFileUrl(context));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 可能为空
                    String nickName = userInfo.get(j).getNickName();
                    if (null == nickName) {// 昵称为空

                        nickName = "无昵称";
                    }

                    try {
                        jsonObject.put("nickName", nickName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("username", userInfo.get(j).getUsername());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject.put("sex", userInfo.get(j).getSex());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                }

            }
            JsonArray.put(jsonObject);
        }

        // =======================处理用户的个人信息============================
        // 存放用户信息的Array
        userJsonArray = new JSONArray();

        for (int num = 0; num < userInfo.size(); num++) {
            // 存放用户信息的object
            userJsonObject = new JSONObject();

            if (userInfo.get(num).getIcon() != null) {

                try {
                    userJsonObject.put("icon", userInfo.get(num).getIcon().getFileUrl(context));
                } catch (JSONException e) {
                    e.printStackTrace();
                }// url

            }

            // 登陆名
            try {
                userJsonObject.put("username", userInfo.get(num).getUsername());
            } catch (JSONException e2) {
                e2.printStackTrace();
            }

            // 性别
            try {
                userJsonObject.put("sex", userInfo.get(num).getSex());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            // 昵称
            try {
                String name = userInfo.get(num).getNickName();
                if (("").equals(name)) {
                    name = "昵称";
                }

                userJsonObject.put("nickName", name);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            userJsonArray.put(userJsonObject);

        }

        // -----------问题信息----------------------
        if (JsonArray.length() > 0) {
            MyApplication.getInstance().getCacheInstance().put(Contants.EXPERIENCE_KEY, JsonArray);
        }
        // Log.i("ex ", JsonArray.toString());
        // -----------对应的用户信息（用户不重复）-----------
        if (userJsonArray.length() > 0) {
            MyApplication.getInstance().getCacheInstance().put(Contants.EXCURRENTUSERICON_KEY,
                    userJsonArray);
        }
        // Log.i("ex ", userJsonArray.toString());
    }

    // ===========================我的回答===========================================

    /**
     * 获取用户记录————我的提问 1、JSON解析 2、缓存到本地
     *
     * @param JSONArray 返回的JSON数据
     */
    public void GetMyQuestion(String JSONArray) {
        JSONArray JsonArray = null;
        JSONObject jsonObject = null;

        MyQuestion myquestion = null;
        List<QresultsDetail> result = null;

        myquestion = gson.fromJson(JSONArray, MyQuestion.class);

        result = myquestion.results;

        JsonArray = new JSONArray();

        for (int i = 0; i < result.size(); i++) {

            jsonObject = new JSONObject();

            // 问题内容
            try {
                jsonObject.put("content", result.get(i).content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 问题id
            try {
                jsonObject.put("objectId", result.get(i).objectId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 问题提出用户
            try {
                jsonObject.put("user_id", result.get(i).user_id.objectId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 问题提出用户昵称
            try {
                jsonObject.put("nickName", result.get(i).user_id.nickName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 问题提出用户名
            try {
                jsonObject.put("username", result.get(i).user_id.username);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 问题提出用户性别
            try {
                jsonObject.put("sex", result.get(i).user_id.sex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 问题提出用户头像
            try {
                jsonObject.put("icon", "http://file.bmob.cn/"
                        + result.get(i).user_id.icon.url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result.get(i).images != null && result.get(i).images.size() > 0) {

                JSONArray imageArray = new JSONArray(result.get(i).images);

                try {
                    jsonObject.put("images", imageArray);
                } catch (JSONException e) {

//                    LogUtils.e("将图片地址的数组转成jsonArray失败");
                }
            }
            // 问题创建时间
            try {
                jsonObject.put("createdAt", result.get(i).createdAt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 用户金币

            try {
                jsonObject.put("wealth", result.get(i).user_id.wealth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonArray.put(jsonObject);

        }

        // ==========设置缓存============
        if (JsonArray.length() > 0) {
            MyApplication.getInstance().getCacheInstance().put(Contants.MYQUESTION_KEY, JsonArray,
                    5 * Contants.MINUTES);
        }
    }

    // ================================我的回答==================================================

    /**
     * 获取用户记录————我的回答 1、JSON解析 2、缓存到本地
     *
     * @param JSONArray 返回的JSON数据
     */
    public void GetMyComment(String JSONArray) {

        String questionId = "";
        JSONArray myJsonArray = null;
        JSONObject jsonObject = null;

        List<AnswerDetail> results = null;
        List<questDetail> quest = null;

        MyAnswer myAnswer = null;

        myAnswer = gson.fromJson(JSONArray, MyAnswer.class);

        // 回答
        results = myAnswer.comment;
        // 回答对应的问题
        quest = myAnswer.question;

        myJsonArray = new JSONArray();

        // Log.i("解析数据", "" + results);
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {

                jsonObject = new JSONObject();

                // 评论内容
                try {
                    jsonObject.put("comContent", results.get(i).comContent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 对应的问题的id
                questionId = results.get(i).quest_id;
                try {
                    jsonObject.put("quest_id", questionId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 评论的接收者
                try {
                    jsonObject.put("toWho", results.get(i).toWho.objectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 发出评论者
                try {
                    jsonObject.put("comm_user_id", results.get(i).user_id.objectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 创建时间createdAt
                try {
                    jsonObject.put("comm_createdAt", results.get(i).createdAt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int kk = 0; kk < quest.size(); kk++) {

                    if (questionId.equals(quest.get(kk).objectId)) {

                        // 问题内容

                        try {
                            jsonObject.put("content", quest.get(kk).content);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题id

                        try {
                            jsonObject.put("objectId", quest.get(kk).objectId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题创建时间createdAt

                        try {
                            jsonObject.put("createdAt", quest.get(kk).createdAt);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (quest.get(kk).images != null && quest.get(kk).images.size() > 0) {
                            JSONArray imageArray = new JSONArray(quest.get(kk).images);

                            try {
                                jsonObject.put("images", imageArray);
                            } catch (JSONException e) {
                                LogUtils.e("添加图片数组 失败 " + e.toString());
                            }
                        }
                        // 问题用户id
                        try {
                            jsonObject.put("user_id",
                                    quest.get(kk).user_id.objectId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题用户名

                        try {
                            jsonObject.put("username",
                                    quest.get(kk).user_id.username);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题用户昵称

                        try {
                            jsonObject.put("nickName",
                                    quest.get(kk).user_id.nickName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题用户头像

                        try {
                            jsonObject.put("icon",
                                    "http://file.bmob.cn/"
                                            + quest.get(kk).user_id.icon.url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题用户性别

                        try {
                            jsonObject.put("sex", quest.get(kk).user_id.sex);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 问题用户金币

                        try {
                            jsonObject.put("wealth", quest.get(kk).user_id.wealth);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                // Log.i("解析数据", jsonObject.toString() + "");

                myJsonArray.put(jsonObject);
            }
        }

        // =========设置缓存==========

        if (myJsonArray.length() > 0)

        {
            MyApplication.getInstance().getCacheInstance().put(Contants.MYANSWER_KEY, myJsonArray,
                    5 * Contants.MINUTES);
        }

    }

    /**
     * 获取用户记录————我的经验 1、JSON解析 2、缓存到本地
     *
     * @param JSONArray 返回的JSON数据
     */
    public void GetMyExperience(String JSONArray) {

        // Log.i("ex",JSONArray);
        JSONArray myJsonArray = null;
        JSONObject jsonObject = null;

        MyExperience myExperience = null;
        List<ExDetail> results = null;

        myExperience = gson.fromJson(JSONArray, MyExperience.class);

        results = myExperience.results;

        myJsonArray = new JSONArray();

        for (int i = 0; i < results.size(); i++) {
            jsonObject = new JSONObject();

            // 点赞
            try {
                jsonObject.put("good", results.get(i).good);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 糟糕的
            try {
                jsonObject.put("bad", results.get(i).bad);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 创建时间
            try {
                jsonObject.put("createdAt", results.get(i).createdAt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 经验内容
            try {
                jsonObject.put("shareEx", results.get(i).shareEx);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 经验的id

            try {
                jsonObject.put("objectId", results.get(i).objectId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 分享经验者id
            try {
                jsonObject.put("user_id", results.get(i).user_id.objectId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 分享经验者头像
            try {
                jsonObject.put("icon", "http://file.bmob.cn/"
                        + results.get(i).user_id.icon.url);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (results.get(i).images != null && results.get(i).images.size() > 0) {

                JSONArray imageArray = new JSONArray(results.get(i).images);

                try {
                    jsonObject.put("images", imageArray);
                } catch (JSONException e) {
                    LogUtils.e("添加图片数组 失败 " + e.toString());
                }
            }
            myJsonArray.put(jsonObject);
        }

        // =============设置缓存============

        if (myJsonArray.length() > 0) {

            MyApplication.getInstance().getCacheInstance().put(Contants.MYEXPERIENCE_KEY, myJsonArray,
                    5 * Contants.MINUTES);
        }

    }

    /**
     * 评论列表 1、使用云端代码，查询评论、对应回复 2、解析数据，格式化成list
     *
     * @param JsonString
     * @return
     */
    public List<Comment4list> GetComment(String JsonString) {

        // int commSize = 0;
        String commId = "";// 临时存放评论的id
        String comm4reply = "";// 临时存放reply对应的comm_id
        // 存放数据，用于返回
        List<Comment4list> commList = new ArrayList<Comment4list>();
        Comment4list commentObj = null;

        // json解析
        List<CommentDetail> commentList = null;
        List<ReplyDetail> replyList = null;
        MyUser quest_user = null;

        Comment4JSON comment4JSON = null;
        // Log.i("评论", JsonString);
        comment4JSON = gson.fromJson(JsonString, Comment4JSON.class);

        commentList = comment4JSON.Comment;
        replyList = comment4JSON.Reply;
        quest_user = comment4JSON.quest_user;

//		Log.i("解析的数据",commentList+"----"+replyList+"----"+quest_user);

        // 问题用户金币信息
        commentObj = new Comment4list();

        commentObj.setUser_id(quest_user.getObjectId());

        commentObj.setWealth(quest_user.getWealth());

        commList.add(commentObj);

        // ==============评论=============
        for (int i = 0; i < commentList.size(); i++) {

            commentObj = new Comment4list();

            // 问题id
            commentObj.setQuest_id(commentList.get(i).quest_id);

            // 评论id
            commId = commentList.get(i).objectId;

            commentObj.setComm_id(commId);

            // 评论内容
            commentObj.setComContent(commentList.get(i).comContent);

            // 评论时间
            commentObj.setCreatAt(commentList.get(i).createdAt);

            // 发出评论者id
            commentObj.setUser_id(commentList.get(i).user_id.getObjectId());

            // 发出评论者 登陆名
            commentObj.setUserName4(commentList.get(i).user_id.getUsername());

            // 是否为最佳答案
            if (commentList.get(i).isBest) {

                commentObj.setBest(true);
            } else {
                commentObj.setBest(false);
            }

            // Log.i("JSON   isBest", "解析 "+commentList.get(i).isBest+" get "+
            // commentObj.isBest());

            // 发出评论者的金币
            commentObj.setWealth(commentList.get(i).user_id.getWealth());

            // Log.i("comm_____setWealth", commentList.get(i).user_id.wealth +
            // "");

            // 发出评论者昵称
            String nickname = "";
            if (("").equals(commentList.get(i).user_id.getNickName())) {

                nickname = "无昵称";
            } else {
                nickname = commentList.get(i).user_id.getNickName();
            }

            commentObj.setNickName(nickname);


            // 评论者头像
            commentObj.setUserIcon(commentList.get(i).user_id.getIcon().getFileUrl(context));

            // 收到评论者--
            commentObj.setToWho(commentList.get(i).toWho.getObjectId());

            // -----------------------------------------------

            // Log.i("isGood", commentList.get(i).isGood + "");
            // Log.i("isBad", commentList.get(i).isBad + "");

            // good数量
            int g = 0;
            if (null != commentList.get(i).isGood) {

                g = commentList.get(i).isGood.size();

                commentObj.setGoodArray(commentList.get(i).isGood);

                for (int kk = 0; kk < g; kk++) {
                    //
                    // Log.i("good user  objectid", "array "
                    // + commentList.get(i).isGood.get(kk)
                    // + "  -- current  " + usrinfo.getObjectId());

                    // 存在用户已经点赞
                    if (commentList.get(i).isGood.get(kk).equals(
                            usrinfo.getObjectId())) {

                        // Log.i("good--对比结果", "相等");
                        commentObj.setGood(true);

                        break;
                    }
                }
                // usrinfo.getObjectId();

            }

            commentObj.setGood(g);

            // commentObj.setGood(commentList.get(i).good);

            // bad数量
            int bad = 0;

            if (null != commentList.get(i).isBad) {

                bad = commentList.get(i).isBad.size();

                commentObj.setBadArray(commentList.get(i).isBad);

                for (int yy = 0; yy < commentList.get(i).isBad.size(); yy++) {

                    // Log.i("good user  objectid", "array "
                    // + commentList.get(i).isBad.get(yy)
                    // + "  -- current  " + usrinfo.getObjectId());

                    // 当前用户点踩
                    if (commentList.get(i).isBad.get(yy).equals(
                            usrinfo.getObjectId())) {

                        // Log.i("bad--对比结果", "相等");

                        commentObj.setBad(true);

                        break;
                    }
                }

            }

            commentObj.setBad(bad);

            // 设置显示类型
            commentObj.setType(1);

            // ------------------ 存放object-------------------------
            commList.add(commentObj);

            // ===============End======================

            // Log.i("评论", commentObj.toString() + "");

            // ============回复===============

            for (int j = 0; j < replyList.size(); j++) {

                comm4reply = replyList.get(j).comm_id;// 对应评论的id

                if (comm4reply.equals(commId)) {

                    commentObj = new Comment4list();

                    // 评论的id
                    commentObj.setComm_id(comm4reply);
                    // 回复内容
                    commentObj.setReplyContent(replyList.get(j).comContent);

                    // 回复时间
                    commentObj.setCreatAt(replyList.get(j).createdAt);

                    // 发出回复者用户名
                    commentObj.setUserName4(replyList.get(j).user_id.getUsername());

                    // 发出回复者id
                    commentObj.setUser_id(replyList.get(j).user_id.getObjectId());

                    // 发出回复者昵称
                    commentObj.setNickName(replyList.get(j).user_id.getNickName());

                    // 发出回复者头像
                    commentObj.setUserIcon(
                            replyList.get(j).user_id.getIcon().getFileUrl(context));

                    // 发出回复者金币
                    commentObj.setWealth(replyList.get(j).user_id.getWealth());

                    // Log.i("rep____setWealth", replyList.get(j).user_id.wealth
                    // +
                    // "");
                    // 收到回复者昵称
                    commentObj.setToWho(replyList.get(j).toWho.getNickName());

                    // 收到回复者id
                    commentObj.setToWho_id(replyList.get(j).toWho.getObjectId());

                    // 收到回复者用户名
                    commentObj.setToWho(replyList.get(j).toWho.getNickName());

                    // 设置显示类型
                    commentObj.setType(2);

                    // 存放object
                    commList.add(commentObj);

                    // Log.i("回复", commentObj.toString() + "");
                }

            }
        }
        return commList;
    }

    /**
     * 获取用户消息记录，包括回复、评论
     *
     * @param jsonString
     */

    public void GetMessage(String jsonString) {

//		 Log.i("getMessage",jsonString+"");

        JSONArray resultArray = new JSONArray();
//        JSONArray jsonArray_temp = null;
        JSONObject tempObject = null;

        List<commentDetail> commentList = null;
        List<replyDetail> reply = null;
        List<comm4replyDetail> comm_reply = null;
        List<questionDetail> question = null;

        Message4JSON message4JSON = null;

        String reply2commId;
        String comm2quest;

        // 解析JSON数据
        message4JSON = gson.fromJson(jsonString, Message4JSON.class);

        // 回复
        reply = message4JSON.reply;

        // 评论
        commentList = message4JSON.comment;

        // 全部问题
        question = message4JSON.question;

        // 问题评论的对应关系
        comm_reply = message4JSON.comm_reply;


        // 没有评论，也就是没有回复。消息列表为空
        if (0 < commentList.size()) {

            // ============评论==============

            for (int jk = 0; jk < commentList.size(); jk++) {

                tempObject = new JSONObject();

                // 当前评论对应的问题id

                comm2quest = commentList.get(jk).quest_id;

                try {
                    tempObject.put("ms_objectId", commentList.get(jk).objectId);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                // Log.i("comm2quest ", commentList.size()+"");
                // 用户id

                try {
                    tempObject.put("c_userId",
                            commentList.get(jk).user_id.getObjectId());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                // 头像
                try {
                    tempObject.put("_Icon", commentList.get(jk).user_id.getIcon().getFileUrl(context));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                // 昵称
                try {
                    tempObject.put("_nickName",
                            commentList.get(jk).user_id.getNickName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 用户名
                try {
                    tempObject.put("c_username",
                            commentList.get(jk).user_id.getUsername());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 内容
                try {
                    tempObject.put("_comContent",
                            commentList.get(jk).comContent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 创建时间
                try {
                    tempObject
                            .put("c_createdAt", commentList.get(jk).createdAt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 是否已读
                try {
                    tempObject.put("isRead", commentList.get(jk).isRead);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Log.i("json isRead ", "评论 "+commentList.get(jk).isRead);

                for (int zz = 0; zz < question.size(); zz++) {

//					Log.i("评论匹配问题",
//							"评论中的问题id "
//									+ comm2quest
//									+ " 问题id "
//									+ question.get(yy).objectId
//									+ (comm2quest.equals(question.get(yy).objectId)));

                    if (comm2quest.equals(question.get(zz).objectId)) {

                        // 问题的id
                        try {
                            tempObject.put("objectId",
                                    question.get(zz).objectId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题的内容
                        try {
                            tempObject.put("content", question.get(zz).content);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题创建时间
                        try {
                            tempObject.put("createdAt",
                                    question.get(zz).createdAt);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题的提出用户的id

                        try {
                            tempObject.put("user_id",
                                    question.get(zz).user_id.getObjectId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题的提出用户的头像
                        try {
                            tempObject.put("icon", question.get(zz).user_id.getIcon().getFileUrl(context));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题的提出用户的用户名
                        try {
                            tempObject.put("username",
                                    question.get(zz).user_id.getUsername());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题的提出用户的昵称
                        try {
                            tempObject.put("nickName",
                                    question.get(zz).user_id.getNickName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 问题的提出用户的性别
                        try {
                            tempObject.put("sex", question.get(zz).user_id.getSex());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }

                try {
                    tempObject.put("type", "comment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//				Log.i("评论", tempObject.toString());


                resultArray.put(tempObject);

            }

        }

        // =========回复========
        /**
         *
         * 回复匹配评论，再通过评论匹配问题
         *
         */

        if (0 < reply.size()) {// 有回复数据

            for (int ii = 0; ii < reply.size(); ii++) {// 回复的对应数据

                tempObject = new JSONObject();

                // 对应评论的id
                reply2commId = reply.get(ii).comm_id;

                try {
                    tempObject.put("ms_objectId", reply.get(ii).objectId);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                // 用户id

                try {
                    tempObject.put("r_user_id", reply.get(ii).user_id.getObjectId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 头像
                try {
                    tempObject.put("_Icon", reply.get(ii).user_id.getIcon().getFileUrl(context));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 昵称
                try {
                    tempObject.put("_nickName", reply.get(ii).user_id.getNickName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 用户名
                try {
                    tempObject
                            .put("r_username", reply.get(ii).user_id.getUsername());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 内容
                try {
                    tempObject.put("_comContent", reply.get(ii).comContent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 创建时间
                try {
                    tempObject.put("c_createdAt", reply.get(ii).createdAt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 消息是否已读
                try {
                    tempObject.put("isRead", reply.get(ii).isRead);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Log.i("json isRead", "评论" + reply.get(ii).isRead);
                // ========问题==========
                for (int kkk = 0; kkk < comm_reply.size(); kkk++) {

                    // 是否对应评论。一个回复只对应一条评论
                    // 相等

                    Log.i("回复匹配评论",
                            "回复对应的评论id "
                                    + reply2commId
                                    + " 评论id "
                                    + comm_reply.get(kkk).objectId
                                    + " "
                                    + (reply2commId.equals(comm_reply.get(kkk).objectId)));

                    if (reply2commId.equals(comm_reply.get(kkk).objectId)) {

                        // 存在评论与回复相对应。根据评论的quest_id获取对应的问题详细信息
                        comm2quest = comm_reply.get(kkk).quest_id;

                        Log.i("size ", question.size() + "");

                        for (int yy = 0; yy < question.size(); yy++) {

                            Log.i("评论匹配问题",
                                    "评论对应的问题id "
                                            + comm2quest
                                            + " 问题id "
                                            + question.get(yy).objectId
                                            + " "
                                            + (comm2quest.equals(question
                                            .get(yy).objectId)));

                            if (comm2quest.equals(question.get(yy).objectId)) {

                                // 问题的id
                                try {
                                    tempObject.put("objectId",
                                            question.get(yy).objectId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // 问题的内容
                                try {
                                    tempObject.put("content",
                                            question.get(yy).content);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // 问题创建时间
                                try {
                                    tempObject.put("createdAt",
                                            question.get(yy).createdAt);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // 问题的提出用户的id

                                try {
                                    tempObject.put("user_id",
                                            question.get(yy).user_id.getObjectId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // 问题的提出用户的头像
                                try {
                                    tempObject
                                            .put("icon",
                                                    "http://file.bmob.cn/"
                                                            + question.get(yy).user_id.getIcon().getFileUrl(context));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // 问题的提出用户的用户名
                                try {
                                    tempObject.put("username",
                                            question.get(yy).user_id.getUsername());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // 问题的提出用户的昵称
                                try {
                                    tempObject.put("nickName",
                                            question.get(yy).user_id.getNickName());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // 问题的提出用户的性别
                                try {
                                    tempObject.put("sex",
                                            question.get(yy).user_id.getSex());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;
                            }
                        }
                        break;
                    }

                }

                try {
                    tempObject.put("type", "reply");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//				Log.i("回复", tempObject.toString());

                resultArray.put(tempObject);

            }

        }

        // 有数据返回为空，所以出现执行两次

        // =======缓存=====

        // 排序

        if (resultArray.length() > 0) {

            sortJsonArrayByDate sort = new sortJsonArrayByDate();
            resultArray = sort.sortJson(resultArray, "r_createdAt");

            MyApplication.getInstance().getCacheInstance().put(Contants.MESSAGE_KEY, resultArray);
        }

        // Log.i("resultArray", resultArray.toString()+"");
    }

}
