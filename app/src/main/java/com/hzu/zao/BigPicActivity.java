package com.hzu.zao;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hzu.zao.adapter.BigpicturePagerAdapter;
import com.hzu.zao.annotation.InjectView;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.network.NetworkReuqest;
import com.hzu.zao.socialMaganer.SocialShareByIntent;
import com.hzu.zao.utils.DisplayUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.view.CustomViewPager;
import com.hzu.zao.view.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * 大图查看
 * <p/>
 * Created by Nearby Yang on 2016-02-20.
 */
public class BigPicActivity extends BaseAppCompatActivity implements ViewTreeObserver.OnPreDrawListener {

    @InjectView(R.id.vp_big_picture)
    private CustomViewPager viewPager;
    @InjectView(R.id.rl_bigpic_root_layout)
    private RelativeLayout rootLayout;
    @InjectView(R.id.ll_bigpic_index)
    private LinearLayout indexLayout;

    private List<View> dotList = new ArrayList<>();
    private List<PictureInfo> pictureInfoList;
    private int currentItem;
    private BigpicturePagerAdapter viewpagerAdapter;
    private int height;
    private int width;

    @Override
    protected int getLayoutId() {
        return  R.layout.activity_bigpic;
    }


    @Override
    protected void initData() {
        pictureInfoList = (List<PictureInfo>) getIntent().getSerializableExtra(Contants.INTENT_IMAGE_INFO_LIST);
        currentItem = getIntent().getIntExtra(Contants.INTENT_CURRENT_ITEM, 0);


    }
    @Override
    protected void findView() {

    }

    @Override
    protected void bindData() {
        viewpagerAdapter = new BigpicturePagerAdapter(this);
        viewpagerAdapter.setData(pictureInfoList);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentItem);
        setPagerChangeListener(viewPager);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {

            mActivity.finish();
            return true;
        }


        return super.dispatchKeyEvent(event);
    }

    /**
     * 绘制前开始动画
     *
     * @return
     */
    @Override
    public boolean onPreDraw() {
        viewPager.getViewTreeObserver().removeOnPreDrawListener(this);
        final View view = viewpagerAdapter.getPrimaryItem();
        final PhotoView imageView = (PhotoView) ((ViewGroup) view).getChildAt(0);

        computeImageWidthAndHeight(imageView);

        final PictureInfo pictureInfo = pictureInfoList.get(currentItem);
        final float vx = pictureInfo.width * 1.0f / width;
        final float vy = pictureInfo.height * 1.0f / height;

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float animatedFraction = animation.getAnimatedFraction();

                view.setTranslationX(EvaluateUtil.evaluateInt(animatedFraction, pictureInfo.x + pictureInfo.width / 2 - imageView.getWidth() / 2, 0));
                view.setTranslationY(EvaluateUtil.evaluateInt(animatedFraction, pictureInfo.y + pictureInfo.height / 2 - imageView.getHeight() / 2, 0));
                view.setScaleX(EvaluateUtil.evaluateFloat(animatedFraction, vx, 1));
                view.setScaleY(EvaluateUtil.evaluateFloat(animatedFraction, vy, 1));

                rootLayout.setBackgroundColor((int) EvaluateUtil.evaluateArgb(animatedFraction, 0x0, 0xff000000));
            }
        });

        addIntoListener(valueAnimator);
        valueAnimator.setDuration(300);
        valueAnimator.start();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //返回键
        startActivityAnim();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_image_save://保存图片
                NetworkReuqest.call2(this, viewpagerAdapter.getImageUrl());
                break;


            case R.id.menu_image_share_all://分享全部图片
//下载图片
                SocialShareByIntent.downloadImagesAndShare(mActivity, viewpagerAdapter.getImagesUri());
//                SocialShareManager.shareImage(context, discAdapter.getImageUrl());

                break;
            case R.id.menu_image_share_singal://分享打仗图片
                SocialShareByIntent.downloadImageAndShare(mActivity,  viewpagerAdapter.getImageUrl());
                break;

        }

        return super.onContextItemSelected(item);
    }

    /**
     * 页面改动监听
     *
     * @param viewPager
     */
    private void setPagerChangeListener(CustomViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                initDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 进场动画过程监听
     *
     * @param valueAnimator
     */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootLayout.setBackgroundColor(0x0);
                indexLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                indexLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    /**
     * 退场动画过程监听
     *
     * @param valueAnimator
     */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootLayout.setBackgroundColor(0x0);
                indexLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * 开始activity的动画
     */
    public void startActivityAnim() {
//        得到当前页的View
        final View view = viewpagerAdapter.getPrimaryItem();
        final PhotoView imageView = (PhotoView) ((ViewGroup) view).getChildAt(0);
//      当图片被放大时，需要把其缩放回原来大小再做动画
        imageView.setZoomable(false);
        computeImageWidthAndHeight(imageView);

        final PictureInfo pictureInfo = pictureInfoList.get(currentItem);
        final float vx = pictureInfo.width * 1.0f / width;
        final float vy = pictureInfo.height * 1.0f / height;

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float animatedFraction = animation.getAnimatedFraction();

                view.setTranslationX(EvaluateUtil.evaluateInt(animatedFraction, 0, pictureInfo.x + pictureInfo.width / 2 - imageView.getWidth() / 2));
                view.setTranslationY(EvaluateUtil.evaluateInt(animatedFraction, 0, pictureInfo.y + pictureInfo.height / 2 - imageView.getHeight() / 2));
                view.setScaleX(EvaluateUtil.evaluateFloat(animatedFraction, 1, vx));
                view.setScaleY(EvaluateUtil.evaluateFloat(animatedFraction, 1, vy));

                if (animatedFraction > 0.95) {
                    view.setAlpha(1 - animatedFraction);
                }
            }
        });
        addOutListener(valueAnimator);
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    /**
     * 计算图片的宽高
     * @param imageView
     */
    private void computeImageWidthAndHeight(PhotoView imageView) {

//      获取真实大小
        Drawable drawable = imageView.getDrawable();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();
//        计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
        float h = DisplayUtils.getScreenHeightPixels(this) * 1.0f / intrinsicHeight;
        float w = DisplayUtils.getScreenWidthPixels(this) * 1.0f / intrinsicWidth;
        if (h > w) {
            h = w;
        } else {
            w = h;
        }
//      得出当宽高至少有一个充满的时候图片对应的宽高
        height = (int) (intrinsicHeight * h);
        width = (int) (intrinsicWidth * w);
    }


    /**
     * 初始化轮播图点
     */
    private void initDot(int index) {
        // 清空点所在集合
        dotList.clear();
        indexLayout.removeAllViews();
        for (int i = 0; i < pictureInfoList.size(); i++) {
            ImageView view = new ImageView(this);
            if (i == index || pictureInfoList.size() == 1) {
                view.setBackgroundResource(R.drawable.icon_type_selected);
            } else {
                view.setBackgroundResource(R.drawable.icon_type_normal);
            }
            // 指定点的大小
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    DisplayUtils.dip2px(this, 5), DisplayUtils.dip2px(this, 5));
            // 指定点的间距
            layoutParams.setMargins(DisplayUtils.dip2px(this, 2), 0, DisplayUtils.dip2px(this, 2), 0);
            // 添加到线性布局中
            indexLayout.addView(view, layoutParams);
            // 添加到集合中去
            dotList.add(view);
        }
    }
}
