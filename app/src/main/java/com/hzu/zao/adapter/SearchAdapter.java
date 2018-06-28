package com.hzu.zao.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzu.zao.BigPicActivity;
import com.hzu.zao.R;
import com.hzu.zao.SearchAcitivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.interfaces.OnImageDownload;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.model.UserQuestion;
import com.hzu.zao.utils.DataFormateUtils;
import com.hzu.zao.utils.DisplayUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.utils.ImageDownloader;
import com.hzu.zao.view.MultiImageView.MultiImageView;
import com.hzu.zao.view.XListView;

import java.io.Serializable;
import java.util.List;

public class SearchAdapter extends BaseAdapter {
    SearchAcitivity context = null;
    List<UserQuestion> list = null;
    ListItemView listItemView;
    private XListView listView;
    private ImageDownloader mDownloader;

    public class ListItemView { //
        public ImageView headIv;
        public TextView userNameTv;
        public TextView dateTv;
        public ImageView sexIv;
        public TextView userQuestionTv;
        public MultiImageView multiImageView;

    }

    public SearchAdapter(SearchAcitivity context, List<UserQuestion> list,
                         XListView listView) {
        super();
        this.context = context;
        this.list = list;
        this.listView = listView;
    }


    public SearchAdapter(SearchAcitivity context, XListView listView) {
        super();
        this.context = context;
        this.listView = listView;
        this.list = (List<UserQuestion>) MyApplication.getInstance().getCacheInstance().getAsListObject(Contants.SEARCHQUESTION_KEY);
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (null == convertView) {
            //Log.i("xy", "SearchAdapter  0");
            listItemView = new ListItemView();

            convertView = LayoutInflater.from(this.context).inflate(
                    R.layout.wantoans_list_item_card, null);

            listItemView.headIv = (ImageView) convertView
                    .findViewById(R.id.user_icon_wanToAns);
            listItemView.userNameTv = (TextView) convertView
                    .findViewById(R.id.name_wanToAns);
            listItemView.sexIv = (ImageView) convertView
                    .findViewById(R.id.sex_wanToAns);
            listItemView.dateTv = (TextView) convertView
                    .findViewById(R.id.creat_date_wanToAns);
            listItemView.userQuestionTv = (TextView) convertView
                    .findViewById(R.id.userQuestion_wanToAns);
            listItemView.multiImageView = (MultiImageView) convertView.findViewById(R.id.miv_want_ans);

            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        //listItemView.headIv
        listItemView.userNameTv.setText(list.get(position).getUser_id().getUsername());
        listItemView.dateTv.setText(list.get(position).getCreatedAt());
        listItemView.userQuestionTv.setText(list.get(position).getContent());

        if ("M".equals(list.get(position).getUser_id().getSex())) {
            listItemView.sexIv.setImageResource(R.drawable.man_icon);
        } else {
            listItemView.sexIv.setImageResource(R.drawable.woman_icon);
        }
        final List<String> imageArray = list.get(position).getImages();

        if (imageArray != null && imageArray.size() > 0) {
            listItemView.multiImageView.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams layoutParams = listItemView.multiImageView.getLayoutParams();
            layoutParams.width = DisplayUtils.getScreenWidthPixels(context) / 3 * 2;

            listItemView.multiImageView.setList(imageArray);
            listItemView.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
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
            listItemView.multiImageView.setVisibility(View.GONE);
        }


        String iconUrl = null;

        if (list.get(position).getUser_id() != null & list.get(position).getUser_id().getIcon() != null) {
//			Log.i("xy", "SearchAdapter  not null");
            //iconUrl = list.get(position).getUser_id().getIcon().getUrl();
            //Log.i("xy", "SearchAdapter  iconUrl="+iconUrl);
            iconUrl = list.get(position).getUser_id().getIcon().getFileUrl(context);
            listItemView.headIv.setTag(iconUrl);
//			 Log.i("xy", "SearchAdapter  iconUrl="+iconUrl);
            if (mDownloader == null) {
                mDownloader = new ImageDownloader();
            }
            // �첽����ͼƬ
            mDownloader.imageDownload(iconUrl, listItemView.headIv, "/zao/img", context,
                    new OnImageDownload() {
                        @Override
                        public void onDownloadSucc(Bitmap bitmap, String c_url,
                                                   ImageView mimageView) {
                            Log.i("xy", "SearchAdapter  onDownloadSucc");

                            ImageView imageView = (ImageView) listView
                                    .findViewWithTag(c_url);

                            if (imageView != null) {
                                imageView.setImageBitmap(bitmap);
                                imageView.setTag("");
                            }
                        }
                    });


        }


        return convertView;
    }

}
