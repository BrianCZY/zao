package com.hzu.zao;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.utils.DataFormateUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.utils.ImageDownloader;
import com.hzu.zao.utils.LogUtils;
import com.hzu.zao.utils.StringUtils;
import com.hzu.zao.view.MultiImageView.MultiImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 经验的详细页面
 * Created by Nearby Yang on 2015-04-06.
 */
public class ExDetailActivity extends BaseAppCompatActivity {
    private TextView content_tx, name_tx, date_tx;
    private MultiImageView multiImageView;
    private JSONArray jsonArray = null;
    private JSONObject jsonObject = null;
    private ImageView userIcon;
    private ImageView sexIcon;
    ImageDownloader getImage = null;

    private String iconUrl = "";
    private int num = -1;

    Bitmap softRefBitmap = null;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_ex_detail;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        setTitle("正文");
    }

    @Override
    protected void findView() {
        content_tx = (TextView) findViewById(R.id.ex_content);
        name_tx = (TextView) findViewById(R.id.ex_username);
        date_tx = (TextView) findViewById(R.id.ex_date);
        userIcon = (ImageView) findViewById(R.id.exd_user_icon);
        sexIcon = (ImageView) findViewById(R.id.exd_sex);
        multiImageView = $(R.id.miv_ex_detail);

    }

    @Override
    protected void bindData() {
        initView();
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_ex_detail);
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

    private void initView() {

        String Gender = "";
        int drawable = 0;


        Intent intents = getIntent();
        num = intents.getIntExtra("object", -1);

        String experienceObj = intents.getStringExtra("experienceObj");

//我的经验
        String myEx = intents.getStringExtra("myEx");
//SEARCHEXPERIENCE_KEY
        jsonArray = 	MyApplication.getInstance().getCacheInstance().getAsJSONArray(Contants.EXPERIENCE_KEY);


        if (num >= 0) {

            try {
                jsonObject = jsonArray.getJSONObject(num);
//				Log.i("xy", "ExDetailActi     jsonObject="+jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (experienceObj != null) {

            try {
//				JSONObject questionObject  = new JSONObject(searchContent);
                jsonObject = new JSONObject(experienceObj);
//				Log.i("xy", "wantoAnsDetailAct  questionObject Obj =" + questionObject.getString("objectId"));
            } catch (JSONException e3) {
                e3.printStackTrace();
            }

        } else if (null != myEx) {
            try {
                jsonObject = new JSONObject(myEx);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            LogUtils.e("something wrong!!");
        }


        try {
            name_tx.setText(jsonObject.getString("nickName"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        try {
            content_tx.setText(StringUtils.getEmotionContent(this, jsonObject.getString("shareEx")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            date_tx.setText(jsonObject.getString("createdAt"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Gender = jsonObject.getString("sex");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (("F").equals(Gender)) {
            drawable = R.drawable.woman_icon;
        } else {
            drawable = R.drawable.man_icon;
        }

        sexIcon.setBackgroundResource(drawable);

        try {
            iconUrl = jsonObject.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageLoader.getInstance().displayImage(iconUrl, userIcon);

        JSONArray imagesArray = null;
        try {
            imagesArray = jsonObject.getJSONArray("images");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (imagesArray != null) {
            final List<String> list = new ArrayList<>();
            for (int i = 0; i < imagesArray.length(); i++) {
                try {
                    list.add(imagesArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            multiImageView.setList(list);
            multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(mActivity, list);

                    EvaluateUtil.setupCoords(mActivity, (ImageView) view, pictureInfoList, position);
                    Intent intent = new Intent(mActivity, BigPicActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putSerializable(Contants.INTENT_IMAGE_INFO_LIST, (Serializable) pictureInfoList);
                    intent.putExtras(bundle);
                    intent.putExtra(Contants.INTENT_CURRENT_ITEM, position);

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }


//        getImage = new ImageDownloader();
//        String imageName = ImageUtil.getInstance().getImageName(iconUrl);
//
//        softRefBitmap = getImage.getBitmapFromFile(ExDetailActivity.this, imageName, "/zao/img");
//
//        if (softRefBitmap != null) {
//
//            userIcon.setImageBitmap(softRefBitmap);
//        }

    }

//    @Override
//    protected void onPause() {
//        overridePendingTransition(R.anim.enter_from_left_animation,
//                R.anim.exit_to_right_animaion);
//        super.onPause();
//    }
}
