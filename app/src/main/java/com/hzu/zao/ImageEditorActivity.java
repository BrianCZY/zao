package com.hzu.zao;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hzu.zao.adapter.ImageEditorPagerAdapter;
import com.hzu.zao.annotation.InjectView;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.model.PictureInfo;
import com.hzu.zao.utils.DisplayUtils;
import com.hzu.zao.utils.EvaluateUtil;
import com.hzu.zao.view.CustomViewPager;
import com.hzu.zao.view.photoview.PhotoView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片查看的activity
 * 在准备发送分享信息的时候，点击照片，可以查看图片
 * <p/>
 * Created by Administrator on 2016/2/3.
 */
public class ImageEditorActivity extends BaseAppCompatActivity implements ViewTreeObserver.OnPreDrawListener {

    @InjectView(R.id.vp_image_brower)
    private CustomViewPager viewPager;
    @InjectView(R.id.rl_image_brower_root_layout)
    private RelativeLayout rootLayout;
    @InjectView(R.id.ll_image_brower_index)
    private LinearLayout indexLayout;

    private ActionBar actionBar;
    private ImageEditorPagerAdapter pagerAdapter;

    private List<PictureInfo> pictureInfoList;
    private List<View> dotList = new ArrayList<>();
    private boolean isLocal = false;//是否是本地图片
    private int currentItem = 0;//当前对应的position
    private int height;//图片宽高
    private int width;//

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_brower;
    }


    @Override
    public void initData() {
        initializeToolbar();

        pictureInfoList = (List<PictureInfo>) getIntent().getSerializableExtra(Contants.INTENT_IMAGE_LIST);
        setTitle(String.format("1/%s", String.valueOf(pictureInfoList.size())));
        currentItem = getIntent().getIntExtra(Contants.INTENT_CURRENT_ITEM, 0);

        //格式化数据
//        formateData();
    }

    @Override
    public void findView() {
        pagerAdapter = new ImageEditorPagerAdapter(mActivity, pictureInfoList, actionBar);
    }

    @Override
    public void bindData() {

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentItem);
        setPagerChangeListener(viewPager);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);
    }

    @Override
    public void handerMessage(Message msg) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_iamge_delete,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 删除一张图片
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        viewPager.removeView(pagerAdapter.getPrimaryItem());
//        viewPager.removeViewAt(pagerAdapter.getItemPosition(pagerAdapter.getPrimaryItem()));
        pagerAdapter.getDataList().remove(currentItem);
        pagerAdapter.notifyDataSetChanged();
        if (pictureInfoList.size() == 0) {
            finisActivity();
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * 绘制，activity进入动画，从特定位置大小
     *
     * @return
     */
    @Override
    public boolean onPreDraw() {
        viewPager.getViewTreeObserver().removeOnPreDrawListener(this);
        final View view = pagerAdapter.getPrimaryItem();
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

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_image_editor);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityAnim();
            }
        });
        actionBar = getSupportActionBar();
    }

    /**
     * 页面改动监听
     * 获取当前的position
     *
     * @param viewPager
     */
    private void setPagerChangeListener(CustomViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTitle(String.format("%s/%s", String.valueOf(position), String.valueOf(pictureInfoList.size())));
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
                actionBar.hide();
                rootLayout.setBackgroundColor(0x0);
                indexLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                actionBar.show();
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
                actionBar.hide();
                rootLayout.setBackgroundColor(0x0);
                indexLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finisActivity();
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
     * 退出当前activity
     * <p/>
     * 开始activity的动画
     */
    public void startActivityAnim() {
//        得到当前页的View
        final View view = pagerAdapter.getPrimaryItem();
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
     *
     * @param imageView
     */
    private void computeImageWidthAndHeight(PhotoView imageView) {

//      获取真实大小
//        Drawable drawable = imageView.getDrawable();
//        LogUtils.E("drawable = " + drawable);
//        int intrinsicHeight = drawable.getIntrinsicHeight();
//        int intrinsicWidth = drawable.getIntrinsicWidth();
        //通过上面的方式获取不行，获取的drawable是null
        int intrinsicHeight = DisplayUtils.px2dip(this, imageView.getHeight());
        int intrinsicWidth = DisplayUtils.px2dip(this, imageView.getWidth());
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
     * 指示器
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

    /**
     * 格式化数据
     */
    private void formateData() {
        for (int i = 0; i < pictureInfoList.size(); i++) {
            //如果是本地数据，使用universal image loader 需要哦添加文件头，本地文件才能加载
            PictureInfo pictureInfom = pictureInfoList.get(i);
            pictureInfom.imageUrl = Contants.FILE_HEAD + pictureInfom.imageUrl;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {//返回键监听
            //隐藏输入法
            if (pagerAdapter.getDataList() != null && pagerAdapter.getDataList().size() > 0) {
                startActivityAnim();
                return true;

            } else {
                finisActivity();
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 结束当前activity
     */
    private void finisActivity() {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Contants.INTENT_IMAGE_LIST, (Serializable) pagerAdapter.getDataList());
        intent.putExtras(bundle);
        setResult(Contants.RESULT_CODE_IMAGE_LIST, intent);
        finish();
    }


}
