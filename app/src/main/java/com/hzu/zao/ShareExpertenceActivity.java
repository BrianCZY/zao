package com.hzu.zao;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.interfaces.GoToUploadImages;
import com.hzu.zao.model.Experience;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.network.BmobApi;
import com.hzu.zao.utils.BitmapCompression;
import com.hzu.zao.utils.DataFormateUtils;
import com.hzu.zao.utils.DisplayUtils;
import com.hzu.zao.utils.EmotionUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.utils.ImageHandlerUtils;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.utils.StorageUtils;
import com.hzu.zao.utils.StringUtils;
import com.hzu.zao.view.CusProcessDialog;
import com.hzu.zao.view.Dialog4Tips;
import com.hzu.zao.view.MultiImageView.MultiImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 分享经验的activity
 * Created by Nearby Yang on 2016-04-22.
 */
public class ShareExpertenceActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ImageView addImagsIm;
    private ImageView addEmoationIm;
    private EditText contenTxt;
    private MultiImageView multiImageView;
    private ViewPager emotionViewPager;
    private LinearLayout emotionPanel_bg;
    private Dialog4Tips dialog;//保存草稿的提示框
    private BitmapCompression.DarftUtils darftUtils;//草稿
    private ProgressDialog progressDialog;
    private String content;


    private static final int MESSAGE_SHARE_FINISH = 0x1001;
    private static final int MESSAGE_SHARE_NOT_EMPTY = 0x1002;
    private static final int MESSAGE_SHARE_FILURE = 0x1003;
    private static final int MESSAGE_UPLOAD_IMAGES_FILURE = 0x1004;
    private static final int MESSAGE_SHARE_STAR = 0x1005;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share_ex_activity;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        setTitle(R.string.share_ex);

    }

    @Override
    protected void findView() {
        addImagsIm = $(R.id.im_activity_share_message_addimg);
        addEmoationIm = $(R.id.im_content_dialog_share_emotion);
        contenTxt = $(R.id.et_contnent_popupwin_content);
        multiImageView = $(R.id.miv_share_message_image);
        emotionViewPager = $(R.id.vp_popupwindow_emotion_dashboard);
        emotionPanel_bg = $(R.id.ll_popip_window_emotion_panel);
        ViewGroup.LayoutParams layoutParams = multiImageView.getLayoutParams();
        layoutParams.width = DisplayUtils.getScreenWidthPixels(this) / 2;
        progressDialog = CusProcessDialog.commenProgressDialog(mActivity, null);
        darftUtils = BitmapCompression.DarftUtils.builder(mActivity);
    }


    @Override
    protected void bindData() {
        //表情
        new EmotionUtils(mActivity, emotionViewPager, contenTxt);

        addEmoationIm.setOnClickListener(this);
        addImagsIm.setOnClickListener(this);
        contenTxt.setOnClickListener(this);

        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<PictureInfo> pictureInfoList =
                        DataFormateUtils.formate2PictureInfo4Local(multiImageView.getImagesList());

                EvaluateUtil.setupCoords(mActivity, (ImageView) view, pictureInfoList, position);
                Intent intent = new Intent(mActivity, ImageEditorActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable(Contants.INTENT_IMAGE_LIST, (Serializable) pictureInfoList);
                intent.putExtras(bundle);
                intent.putExtra(Contants.INTENT_CURRENT_ITEM, position);

                startActivityForResult(intent, Contants.REQUSET_CODE_IMAGE_LIST);
                overridePendingTransition(0, 0);
            }
        });

        dialog = new Dialog4Tips(mActivity);
//恢复草稿
        setDraft();
    }


    /**
     * 回复草稿
     */
    private void setDraft() {

        final List<String> list = new ArrayList<>();
        final String draft_content = app.getCacheInstance().getAsString(Contants.DRAFT_CONTENT_EX);
        final JSONArray imgJsArray = app.getCacheInstance().getAsJSONArray(Contants.DRAFT_IMAGE_LIST_EX);

        if (draft_content != null || imgJsArray != null) {

            dialog.setContent(getString(R.string.had_draft_would_reset_tips));
            dialog.setBtnOkText(getString(R.string.reset));
            dialog.setBtnCancelText(getString(R.string.do_not_reset));

            dialog.setDialogListener(new Dialog4Tips.Listener() {
                @Override
                public void btnOkListenter() {

                    if (!TextUtils.isEmpty(draft_content)) {
                        contenTxt.setText(StringUtils.getEmotionContent(
                                mActivity, draft_content));
                    }


                    if (imgJsArray != null) {
                        for (int i = 0; i < imgJsArray.length(); i++) {

                            try {
                                list.add(imgJsArray.getString(i));
                            } catch (JSONException e) {
                                LogUtils.e("读取jsonArray数据出错" + e.toString());
                            }

                        }
                        multiImageView.setList(list);
//                        gridViewAdapter.setDatas(DataFormateUtils.formateLocalImage(list));
                    }


                    //删除草稿
                    darftUtils.deleteDraft();
                    dialog.dismiss();
                }

                @Override
                public void btnCancelListener() {

                    //删除草稿
                    darftUtils.deleteDraft();

                    dialog.dismiss();
                }
            });
            dialog.show();
            LogUtils.i("有草稿 存在");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_send, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_send) {
            content = contenTxt.getText().toString();

            if (!TextUtils.isEmpty(content))
             /*发送*/
//                toast(R.string.txt_share_uploading);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        shareEx();
                    }
                }).start();
            return true;
        } else {
            toast(R.string.txt_share_content_empty);
            return true;
        }

//        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void handerMessage(Message msg) {
        HandlerMessage handlerMessage = null;
        switch (msg.what) {
            case MESSAGE_SHARE_FINISH:
                progressDialog.dismiss();
                toast("分享成功");
                shareFinish();
                break;

            case MESSAGE_SHARE_NOT_EMPTY:
                toast("分享的内容不能为空");
                break;
            case MESSAGE_SHARE_FILURE:
                handlerMessage = (HandlerMessage) msg.obj;
                toast("分享信息失败!! code = " + handlerMessage.getCode() + " message = " + handlerMessage.getMessage());
                break;
            case MESSAGE_UPLOAD_IMAGES_FILURE:
                handlerMessage = (HandlerMessage) msg.obj;
                toast("上传图片失败!! code = " + handlerMessage.getCode() + " message = " + handlerMessage.getMessage());
                break;
            case MESSAGE_SHARE_STAR:

                progressDialog.setMessage(getString(R.string.txt_sending));
                progressDialog.show();
                break;
        }
    }

    /**
     * 分享经验
     */
    private void shareEx() {
        if (!TextUtils.isEmpty(content)) {
            mHandler.sendEmptyMessage(MESSAGE_SHARE_STAR);
            final Experience experience = new Experience();
            experience.setShareEx(content);
            experience.setUser_id(cuser);
            String[] file = null;

            if (multiImageView.getImagesList() != null && multiImageView.getImagesList().size() > 0) {

                file = new String[multiImageView.getImagesList().size()];
                //压缩图片
                for (int i = 0; i < multiImageView.getImagesList().size(); i++) {
                    String path = multiImageView.getImagesList().get(i);
                    String targetPath = StorageUtils.createImageFile(mActivity).getAbsolutePath() + "/" + StorageUtils.getFileName(path);
                    path = path.substring(Contants.FILE_HEAD.length());
                    LogUtils.d(" sources path = " + path + " target path = " + targetPath);
                    BitmapCompression.compressBitmap(path, targetPath);
                    file[i] = targetPath;
                }

//上传图片
                BmobApi.UploadImages(mActivity, file, new GoToUploadImages() {
                    @Override
                    public void Result(List<String> urls, List<BmobFile> files) {

                        experience.setImages(urls);
                        saveEx2Bmob(experience);
                        ;

                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        Message ms = new Message();
                        ms.what = MESSAGE_UPLOAD_IMAGES_FILURE;
                        ms.obj = new HandlerMessage(statuscode, errormsg);
                        mHandler.sendMessage(ms);
                    }
                });

            } else {//没有图片，直接进行 分享
                saveEx2Bmob(experience);

            }

        } else {
            mHandler.sendEmptyMessage(MESSAGE_SHARE_NOT_EMPTY);

        }

    }

    /**
     * 保存经验到Bmob中
     *
     * @param experience
     */
    private void saveEx2Bmob(Experience experience) {
        experience.save(mActivity,
                new SaveListener() {

                    @Override
                    public void onSuccess() {
                        //添加金币
                        MyUser newUser = new MyUser();
                        newUser.increment("wealth", 2);//分享金币 +2
                        newUser.update(mActivity, cuser.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                LogUtils.e("添加金币成功 ");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                LogUtils.e("添加金币失败 code = " + i + " message = " + s);
                            }
                        });
                        mHandler.sendEmptyMessage(MESSAGE_SHARE_FINISH);

                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {

                        Message message = new Message();
                        message.what = MESSAGE_SHARE_FILURE;
                        message.obj = new HandlerMessage(arg0, arg1);
                        mHandler.sendMessage(message);
//                            toast("error \n errorMsg " + arg1);

                    }
                });


    }

    /**
     * 分享结束
     */
    private void shareFinish() {

        mActivity.setResult(Contants.RESULT_CODE);
        mActivity.finish();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            if (emotionPanel_bg.getVisibility() == View.VISIBLE) {
                emotionPanel_bg.setVisibility(View.GONE);
            } else {
                mActivity.finish();
            }

            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_share_message);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 返回上一级前，先询问是否保存草稿
                 */
                if (!TextUtils.isEmpty(contenTxt.getText().toString()) ||
                        multiImageView.getImagesList() != null && multiImageView.getImagesList().size() > 0) {
                    saveDarft();
                } else {
                    shareFinish();
                }

            }
        });

    }

    /**
     * 退出的提示
     */
    private void saveDarft() {

        dialog.setContent(getString(R.string.need_to_save_draft));
        dialog.setBtnOkText(getString(R.string.save));
        dialog.setBtnCancelText(getString(R.string.do_not_save));
        dialog.setDialogListener(new Dialog4Tips.Listener() {
            @Override
            public void btnOkListenter() {


                darftUtils.saveDraft(
                        contenTxt.getText().toString(),
                        multiImageView.getImagesList());
                mActivity.finish();

            }

            @Override
            public void btnCancelListener() {
                mActivity.finish();

            }
        });
        dialog.show();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_activity_share_message_addimg://添加照片
                emotionPanel_bg.setVisibility(View.GONE);
                selectImages();

                break;
            case R.id.im_content_dialog_share_emotion://添加表情

                emotionPanel_bg.setVisibility(emotionPanel_bg.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.et_contnent_popupwin_content:
                emotionPanel_bg.setVisibility(View.GONE);
                break;
        }


    }

    /**
     * 获取图片返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        RESULT_CODE_IMAGE_LIST
        if (requestCode == Contants.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT).size() > 0) {

                    List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

                    //图片路径
                    multiImageView.setList(urlAddHead(pathList));
                    multiImageView.setVisibility(View.VISIBLE);
                }

            }
        }

        if (Contants.RESULT_CODE_IMAGE_LIST == resultCode) {

            List<PictureInfo> picList = (List<PictureInfo>) data.getExtras().getSerializable(Contants.INTENT_IMAGE_LIST);
            List<String> imageList = new ArrayList<>();

            if (picList.size() == 0) {//删除全部的数据
                multiImageView.setVisibility(View.GONE);
                multiImageView.setList(new ArrayList<String>());
            } else if (multiImageView.getImagesList().size() > picList.size()) {

                for (String imgUrl : multiImageView.getImagesList()) {
                    for (PictureInfo picUrl : picList) {
                        if (imgUrl.equals(picUrl.getImageUrl())) {
                            imageList.add(imgUrl);
                            break;
                        }
                    }

                }
                multiImageView.setList(imageList);
            }
        }


    }


    /**
     * 选择图片
     */
    private void selectImages() {
        emotionPanel_bg.setVisibility(emotionPanel_bg.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);
//        List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(this, multiImageView.getImagesList());

        ArrayList<String> l = new ArrayList<>();
        if (multiImageView.getImagesList() != null && multiImageView.getImagesList().size() > 0) {
//            for (String path : multiImageView.getImagesList()) {
//                l.add(path.substring(Contants.FILE_HEAD.length()));
//                LogUtils.d("file path = " + path.substring(Contants.FILE_HEAD.length()));
//            }
            l.addAll(multiImageView.getImagesList());
        }

        //选择图片
        ImageHandlerUtils.starSelectImages(mActivity, l);
    }


    /**
     * 将图片路径添加file://，为了实现universal image loader 加载本地图片
     *
     * @param pathList
     * @return
     */
    private List<String> urlAddHead(List<String> pathList) {
        ArrayList<String> mSelectPath = new ArrayList<>();
        for (String path : pathList) {
            path = Contants.FILE_HEAD + path;
            mSelectPath.add(path);

        }

        return mSelectPath;
    }


    private class HandlerMessage {

        int code;
        String message;

        public HandlerMessage(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }


        public String getMessage() {
            return message;
        }


    }
}
