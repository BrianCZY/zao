package com.hzu.zao.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzu.zao.BigPicActivity;
import com.hzu.zao.ExDetailActivity;
import com.hzu.zao.ExperienceActivity;
import com.hzu.zao.R;
import com.hzu.zao.config.Contants;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.utils.DataFormateUtils;
import com.hzu.zao.utils.DisplayUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.utils.ImageDownloader;
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

public class ExperienceAdapter extends BaseAdapter {

    private ExperienceActivity context;
    private XListView cardlist;
    private JSONArray jsonArray = null;
    private JSONObject jsonObject = null;
    private ImageDownloader mDownloader;

    public ExperienceAdapter(ExperienceActivity experienceActivity,
                             XListView cardlist) {
        this.context = experienceActivity;
        this.cardlist = cardlist;
    }


    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        int size = 0;
//		jsonArray = cacheUtile.ReadJSONArray(Contants.EXPERIENCE_KEY);

        // Log.i("getCount", "jsonArray: " + jsonArray);

        if (jsonArray == null) {
            size = 0;
        } else {

            size = jsonArray.length();

        }

        return size;
    }

    @Override
    public Object getItem(int position) {
        try {
            return jsonArray.get(jsonArray.length() - position - 1);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ExperienceAdapter.viewHolder viewHolder;
        if (null == convertView) {

            viewHolder = new viewHolder();

            convertView = LayoutInflater.from(this.context).inflate(
                    R.layout.experience_listview_item, null);

            viewHolder.userIcon = (ImageView) convertView
                    .findViewById(R.id.user_icon_experi);
            viewHolder.createDate = (TextView) convertView
                    .findViewById(R.id.creat_date);
            viewHolder.userExperience = (TextView) convertView
                    .findViewById(R.id.userexperi);
            viewHolder.multiImageView = (MultiImageView) convertView.findViewById(R.id.miv_ex);

            convertView.setTag(viewHolder);

        } else {//
            viewHolder = (viewHolder) convertView.getTag();

        }

        // ����Դ
        try {
            jsonObject = jsonArray.getJSONObject(jsonArray.length() - position
                    - 1);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        // viewHolder.userIcon.setImageBitmap(userIconBitmap);
        try {
            viewHolder.createDate.setText(jsonObject.getString("createdAt"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            viewHolder.userExperience.setText(StringUtils.getEmotionContent(context, jsonObject.getString("shareEx")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String iconUrl = "";
        try {
            iconUrl = jsonObject.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        viewHolder.userIcon.setTag(iconUrl);

        ImageLoader.getInstance().displayImage(iconUrl, viewHolder.userIcon);
//        if (mDownloader == null) {
//            mDownloader = new ImageDownloader();
//        }
//
//        //
//        mDownloader.imageDownload(iconUrl, viewHolder.userIcon, "/zao/img",
//                context, new OnImageDownload() {
//                    @Override
//                    public void onDownloadSucc(Bitmap bitmap, String c_url,
//                                               ImageView mimageView) {
//
//                        ImageView imageView = (ImageView) cardlist
//                                .findViewWithTag(c_url);
//
//                        if (imageView != null) {
//                            imageView.setImageBitmap(bitmap);
//                            imageView.setTag("");
//                        }
//                    }
//                });

        cardlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intents = new Intent();

                int num = jsonArray.length() - position;
                // Bundle bundle=new Bundle();
                //
                //
                intents.putExtra("object", num);
                intents.setClass(context, ExDetailActivity.class);

                context.startActivity(intents);
                context.overridePendingTransition(
                        R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);


            }

        });

        JSONArray images = null;
        try {
            images = jsonObject.getJSONArray("images");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (images != null) {
            final List<String> list = new ArrayList<>();

            for (int i = 0; i < images.length(); i++) {
                try {

                    if (!TextUtils.isEmpty(images.getString(i))) {
                        list.add(images.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ViewGroup.LayoutParams layoutParams = viewHolder.multiImageView.getLayoutParams();
            layoutParams.width = DisplayUtils.getScreenWidthPixels(context) / 3 * 2;

            viewHolder.multiImageView.setVisibility(View.VISIBLE);
            viewHolder.multiImageView.setList(list);
            viewHolder.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
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
            viewHolder.multiImageView.setVisibility(View.GONE);


        }


        return convertView;
    }

    public class viewHolder {
        public ImageView userIcon;
        public TextView createDate;
        public TextView userExperience;
        public MultiImageView multiImageView;
    }
}
