package com.hzu.zao.adapter;

import android.content.Intent;
import android.os.Bundle;
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
import com.hzu.zao.R;
import com.hzu.zao.SearchExperienceActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.Experience;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.utils.DataFormateUtils;
import com.hzu.zao.utils.DisplayUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.utils.ImageDownloader;
import com.hzu.zao.view.MultiImageView.MultiImageView;
import com.hzu.zao.view.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class SearchExperienceAdapter extends BaseAdapter {

    private SearchExperienceActivity context;
    private XListView cardlist;
    private viewHolder viewHolder;
    private JSONArray jsonArray = null;
    private JSONObject jsonObject = null;
    private ImageDownloader mDownloader;
    private List<Experience> list;

    public SearchExperienceAdapter(SearchExperienceActivity experienceActivity,
                                   XListView cardlist) {
        this.context = experienceActivity;
        this.cardlist = cardlist;
        list = (List<Experience>) MyApplication.getInstance().getCacheInstance().getAsListObject(Contants.SEARCHEXPERIENCE_KEY);
    }


    @Deprecated
    public SearchExperienceAdapter(SearchExperienceActivity context,
                                   XListView cardlist, List<Experience> list) {
        super();
        this.context = context;
        this.cardlist = cardlist;
        this.list = list;
    }

    @Override
    public int getCount() {
        // Log.i("xy", "searchAdapter    list.size()"+list.size());
        if (list != null) {
            return list.size();
        } else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        } else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

        //Log.i("xy", "SearchExpAdap    before do set");
        viewHolder.createDate.setText(list.get(+position).getCreatedAt() + "");
        //Log.i("xy", "SearchExpAdap    createAt="
        //		+ list.get(position).getCreatedAt());
        viewHolder.userExperience.setText(list.get(position).getShareEx() + "");

        String iconUrl = null;
        if (list.get(position).getUser_id() != null
                && list.get(position).getUser_id().getIcon() != null) {

            iconUrl = list.get(position).getUser_id().getIcon()
                    .getFileUrl(context);
            //Log.i("xy", "SearchExpAdap    iconUrl=" + iconUrl);
            ImageLoader.getInstance().displayImage(iconUrl, viewHolder.userIcon);
//			if (mDownloader == null) {
//				mDownloader = new ImageDownloader();
//			}
//			//
//			mDownloader.imageDownload(iconUrl, viewHolder.userIcon, "/zao/img",
//					context, new OnImageDownload() {
//						@Override
//						public void onDownloadSucc(Bitmap bitmap, String c_url,
//								ImageView mimageView) {
//							Log.i("xy", "SearchExpAdap    onDownloadSucc");
//							ImageView imageView = (ImageView) cardlist
//									.findViewWithTag(c_url);
//
//							if (imageView != null) {
//								imageView.setImageBitmap(bitmap);
//								imageView.setTag("");
//							}
//						}
//					});
        }
        //图片
        if (list != null && list.size() > 0) {
            handlerImages(list.get(position).getImages());
        }

        cardlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intents = new Intent();

//				int num = list.size() - position;
                // Bundle bundle=new Bundle();
                //
                //
                JSONObject experienceObj = new JSONObject();
                Experience experience = list.get((int) id);

                try {
                    experienceObj.put("updatedAt", experience.getUpdatedAt());
                    experienceObj.put("icon", experience.getUser_id().getIcon().getFileUrl(context));
                    experienceObj.put("username", experience.getUser_id().getUsername());
                    experienceObj.put("sex", experience.getUser_id().getSex());
                    experienceObj.put("shareEx", experience.getShareEx());
                    experienceObj.put("nickName", experience.getUser_id().getNickName());
                    experienceObj.put("objectId", experience.getObjectId());
                    experienceObj.put("createdAt", experience.getCreatedAt());
                    experienceObj.put("user_id", experience.getUser_id());
                    experienceObj.put("good", experience.getGood());
                    experienceObj.put("bad", experience.getBad());

                    experienceObj.put("images", experience.getImages() != null ?
                            new JSONArray(experience.getImages()) : new JSONArray());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intents.putExtra("experienceObj", experienceObj.toString());
                intents.setClass(context, ExDetailActivity.class);

                context.startActivity(intents);
                context.overridePendingTransition(
                        R.anim.enter_from_right_animation,
                        R.anim.exit_to_left_animation);

            }

        });

        return convertView;
    }

    /**
     * 展示图片
     *
     * @param imageArray 图片
     */
    private void handlerImages(final List<String> imageArray) {
        if (imageArray != null && imageArray.size() > 0) {
            viewHolder.multiImageView.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams layoutParams = viewHolder.multiImageView.getLayoutParams();
            layoutParams.width = DisplayUtils.getScreenWidthPixels(context) / 3 * 2;

            viewHolder.multiImageView.setList(imageArray);
            viewHolder.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    List<PictureInfo> pictureInfoList;
                    pictureInfoList = DataFormateUtils.formate2PictureInfo(context, imageArray);

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
    }

    public class viewHolder {
        public ImageView userIcon;
        public TextView createDate;
        public TextView userExperience;
        public MultiImageView multiImageView;

    }
}
