package com.hzu.zao.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hzu.zao.BigPicActivity;
import com.hzu.zao.R;
import com.hzu.zao.adapter.baseAdapter.BasePagerAdapter;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.utils.ImageHandlerUtils;
import com.hzu.zao.view.photoview.PhotoView;
import com.hzu.zao.view.photoview.PhotoViewAttacher;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 大图浏览 适配器
 * <p/>
 * Created by Nearby Yang on 2016-02-20.
 */
public class BigpicturePagerAdapter extends BasePagerAdapter<PictureInfo> implements PhotoViewAttacher.OnPhotoTapListener {

    private View mCurrentView;
    private String imageUrl;
    private List<String> imagesUri;

    public BigpicturePagerAdapter(Context context) {
        super(context);
        imagesUri =new ArrayList<>();
    }

    public String getImageUrl() {
        return imageUrl;
    }


    /**
     * 获取全部高清大图的地址
     * @return
     */
    public List<String> getImagesUri() {

        for (PictureInfo  pictureInfo :dataList){
            imagesUri.add(pictureInfo.getImageUrl());
        }

        return imagesUri;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View) object;
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_photoview;

    }

    @Override
    protected void instanceItem(View v, final PictureInfo pictureInfo, int position) {

        PhotoView photoView = (PhotoView) v.findViewById(R.id.pv_item_photoview);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.pb_item_photoview);
        photoView.setOnPhotoTapListener(this);

        if (pictureInfo != null && !TextUtils.isEmpty(pictureInfo.getImageUrl())) {
            ((Activity) context).registerForContextMenu(photoView);
            photoView.setOnCreateContextMenuListener(new OnContextMenuCreat());
            photoView.setImageResource(R.color.black);
            setNetImage(photoView, progressBar, pictureInfo);

            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    imageUrl = pictureInfo.getImageUrl();
                    return false;
                }
            });

        } else {//图片不存在
            photoView.setImageResource(R.drawable.icon_iamge_uri_empty);
        }

    }

    /**
     * context menu 创建
     *
     */
    private class OnContextMenuCreat implements View.OnCreateContextMenuListener{

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            ((Activity)context).getMenuInflater().inflate(R.menu.menu_context_image_option,contextMenu);
        }
    }

    /**
     * 加载图片，先从内存缓存中加载图片，如果没有找到，那么就不能显示图片了
     *
     * @param photoView
     * @param progressBar
     * @param pictureInfo
     */
    private void setNetImage(final PhotoView photoView, final ProgressBar progressBar, final PictureInfo pictureInfo) {

        ImageLoader.getInstance().displayImage(pictureInfo.getImageUrl(), photoView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                startLoad(progressBar);
                loadImageFromCache(photoView, pictureInfo.getSmallImageUrl());
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                overLoad(progressBar);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });


    }

    /**
     * 从内存中读取刚刚的缩略图
     *
     * @param photoView 显示图片的imageView
     * @param imageUrl  图片地址
     */
    private void loadImageFromCache(PhotoView photoView, String imageUrl) {
        Bitmap bitmap = ImageHandlerUtils.getBitmapFromCache(imageUrl, ImageLoader.getInstance());
        if (bitmap != null) {
            photoView.setImageBitmap(bitmap);
        } else {//内存中没有改缩略图
            photoView.setImageResource(R.drawable.icon_default_iamge);
        }


    }

    /**
     * 显示进度条
     *
     * @param pb
     */
    private void startLoad(ProgressBar pb) {
        pb.setVisibility(View.VISIBLE);
    }

    /**
     * 结束进度条
     *
     * @param pb
     */
    private void overLoad(ProgressBar pb) {
        pb.setVisibility(View.GONE);
    }


    @Override
    public void onPhotoTap(View view, float x, float y) {
        ((BigPicActivity) context).startActivityAnim();
    }
}
