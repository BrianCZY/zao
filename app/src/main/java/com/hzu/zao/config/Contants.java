package com.hzu.zao.config;

import com.hzu.zao.R;

/**
 * @author Nearby Yang
 *         <p/>
 *         Create at 2015 下午5:22:08 静�?�变量表 引用数据
 */
public class Contants {
    // bmob应用密钥
    public static final String APPID = "1576c66f1680ac9415c969b55063176c";
    public static final String APP_RESET_KEY = "56e34b6b7b0ad0b1682d5f47ff833a8d";
    public static final String REST_APP_KEY = "X-Bmob-Application-Id";
    public static final String REST_APP_REST_KEY = "X-Bmob-REST-API-Key";
    public static final String ROOT = "root";

    // item分类
    public static final int ITEM_FIRST = 0;// item第一项
    public static final int ITEM_PARENT = 1;
    public static final int ITEM_CHIND = 2;

    // 缓存文件Key
    public static final String WANTOANS_KEY = "wantoans_key";// 我来回答
    public static final String EXPERIENCE_KEY = "experience_key";// 找经验
    public static final String MYQUESTION_KEY = "myquestion_key";// 我来提问
    public static final String MYANSWER_KEY = "myanswer_key";// 我的回答
    public static final String MYEXPERIENCE_KEY = "myexperience_key";// 我的经验
    public static final String MESSAGE_KEY = "message_key";// 消息列表
    public static final String RECOMMENDED_KEY = "recommended_key";// 推荐用户
    public static final String USERINFO_KEY = "userinfo_key";// 用户信息
    // public static final String USEHEADSCULPTURE_KEY = "userinfo_key";// 用户头像
    public static final String USERICONURL_KEY = "usericonurl_key";// 问题列表对应用户的信信
    public static final String CURRENTUSER_KEY = "currentuser_key";// 当前用户
    public static final String CURRENTUSERICON_KEY = "currentusericon_key";// 当前用户的头像
    public static final String CURRENTUSERPWD_KEY = "currentuserpwd_key";// 当前用户的密码
    public static final String EXCURRENTUSERICON_KEY = "currentusericon_key";// 经验-用户的头像
    public static final String SEARCHQUESTION_KEY = "searchquestion_key";// 搜索的问题
    public static final String SEARCHEXPERIENCE_KEY = "searchexperience_key";// 搜索的经验
    public static final String ACAHE_KEY_USER = "acahe_key_user";// 用户信息

    //                                  Acache keys
    //******************************** 保存草稿 keys**********************************************
    public static final String DRAFT_TYPE = "draft_type";
    public static final String DRAFT_TYPE_DICOUNT = "draft_type_dicount";
    public static final String DRAFT_TYPE_DICOVER = "draft_type_dicover";
    public static final String DRAFT_CONTENT = "draft_content";
    public static final String DRAFT_TAG = "draft_tag";
    public static final String DRAFT_LOCATION_INFO = "draft_location_info";
    public static final String DRAFT_IMAGES_LIST = "draft_images_list";
    public static final String DRAFT_VIDEO = "draft_video";
    public static final String DRAFT_VIDEO_PREVIEW = "draft_video_preview";

    public static final String DRAFT_CONTENT_EX = "draft_content_ex";
    public static final String DRAFT_IMAGE_LIST_EX = "draft_image_list_ex";
    public static final String DRAFT_CONTENT_USER_QUESTION = "draft_content_user_question";
    public static final String DRAFT_IMAGE_LIST_USER_QUESTION = "draft_image_list_user_question";

    public static final int DARFT_LIVE_TIME = 60 * 60;//一个小时
    // 时间
    public static final int MINUTES = 60;
    public static final int HOUR = 3600;
    public static final int DAY = HOUR * 24;
    public static final int MONTH = DAY * 30;

    // 云端方法
    public static final String COULDCODE = "GetUserQuestion";// GetUserQuestion云端代码
    public static final String GETCOMM_COULDCODE = "GetComment";// 获取对应问题的评论、回复
    public static final String GETMSG_COULDCODE = "GetMessages";// 获取消息列表的数据
    public static final String UPDATAWEALTH_COULDCODE = "UpdataUserWealth";// 获取消息列表的数据
    public static final String UPDATAEXEALTH_COULDCODE = "ShareExWealth";// 获取消息列表的数据

    // 表名
    public static final String MYQUESTION_TABLE = "UserQuestion";// 用户记录——我的提问
    public static final String MYANSWER_TABLE = "Comment";// 用户记录——我的回答
    public static final String EXPERIENCE_TABLE = "Experience";// 用户记录——我的经验
    public static final String REPLY_TABLE = "Reply";// 用户记录——回复

    //***********************本地文件位置***************************
    public static final String FILE_HEAD = "file://";
    public static final String FILE_CACHE_PATH = "/zao/cache/";
    public static final String FILE_IMAGE_PATH = "/zao/images/";
    public static final String FILE_PAHT_DOWNLOAD = "/zao/download/";
    public static final String FILE_PAHT_SAVE = "/zao/save/";

    //*********************加载图片**********************
    public static final int IMAGE_NUMBER = 9;
    public static final int REQUEST_IMAGE = 2;
    public static final int MODEL_ID = 0;
    public  static final  int IAMGE_MAX_SIZE = 1080;//最大图片宽度
    public  static final  String DEFAULT_AVATAR = "file://"+ R.drawable.usericon_default;//默认头像


    //*****************************网络请求参数********************************
    public static final String PARAMS_URL = "url";//缩略图
    public static final String PARAMS_INSTALLATIONID = "installationId";//参数名
    public static final String PARAMS_USER_NAME = "username";//参数名

    //*******************************intent**********************
    public static final int REQUEST_CODE = 0x01;//
    public static final int REQUEST_CODE_LOGIN = 0x02;//


    public static final int RESULT_CODE_IMAGE_LIST = 0x1001;//图片编辑返回码
    public static final int RESULT_CODE_PLACE = 0x1002;//图片编辑返回码
    public static final int RESULT_CODE_LOGIN = 0x1003;//登陆成功后

    public static final String INTENT_IMAGE_LIST = "intent_image_list";//intent传输的照片列表
    public static final String INTENT_CURRENT_ITEM = "intent_current_item";//intent传输数据标志,点击的图片的position，那么就是当前显示的图片
    public static final int RESULT_CODE = 0x1001;//
    public static final int REQUSET_CODE_IMAGE_LIST = 0x100;//图片编辑请求码
    public static final String INTENT_IMAGE_INFO_LIST = "intent_image_info_list";//intent传输数据标志,传递图片的信息，位置、大小
    public static final String INTENT_LOGIN_RESULT = "intent_login_result";//intent传输数据标志,登陆结果
    public static final int INTENT_LOGIN_SUCCESS = 0x22;//intent传输数据标志,更新界面
    public static final String INTENT_LOGOUT_SUCCESS = "intent_logout_success";//intent传输数据标志,更新界面
//
    public static final String BROADCASTRECEIVER_LOGIN_SUCCESS = "broadcastreceiver_login_success";//广播接收者,更新界面
    public static final String BROADCASTRECEIVER_LOGOUT_SUCCESS = "broadcastreceiver_logout_success";//广播接收者,更新界面

}
