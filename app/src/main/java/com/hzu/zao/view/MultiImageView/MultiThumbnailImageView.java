package com.hzu.zao.view.MultiImageView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.hzu.zao.R;
import com.hzu.zao.utils.DisplayUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 取代gridview 为了更高效显示图片
 *
 * @author shoyu
 * @ClassName MultiImageView.java
 * @Description: 显示1~4张图片的View
 */

public class MultiThumbnailImageView extends LinearLayout {
    public static int MAX_WIDTH = 0;
    private Context context;
    // 照片的Url列表
    private List<String> imagesList;
    private List<String> bigImagesList;
    private List<ImageView> imageViewList;

    /**
     * 长度 单位为Pixel
     **/
    private int pxOneMaxWandH;  // 单张图最大允许宽高
    private int pxMoreWandH = 0;// 多张图的宽高
    private int pxImagePadding = DisplayUtils.dip2px(getContext(), 1);// 图片间的间距

    private int MAX_PER_ROW_COUNT = 2;// 每行显示最大数

    private LayoutParams onePicPara;
    private LayoutParams morePara, moreParaColumnFirst;
    private LayoutParams rowPara;
    private boolean RegisterForContextMenu = false;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;


    public MultiThumbnailImageView(Context context) {
        super(context, null);
        this.context = context;
    }

    public MultiThumbnailImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        imageViewList = new ArrayList<>();
    }

    /**
     * 是否注册contextMenu
     *
     * @param RegisterForContextMenu
     */
    public void setRegisterForContextMenu(boolean RegisterForContextMenu) {
        this.RegisterForContextMenu = RegisterForContextMenu;
    }

    public void setList(List<String> lists) throws IllegalArgumentException {
        if (lists == null) {
            throw new IllegalArgumentException("imageList is null...");
        }


        imagesList = lists.size() <= 4 ? lists : lists.subList(0, 4);

        if (MAX_WIDTH > 0) {
            pxMoreWandH = (MAX_WIDTH - pxImagePadding * 2) / 2; //解决右侧图片和内容对不齐问题
            pxOneMaxWandH = MAX_WIDTH * 2 / 2;
            initImageLayoutParams();
        }

        initView();
    }

    /**
     * 传入大图的uri
     *
     * @param bigImagesList
     */
    public void setBigImagesList(List<String> bigImagesList) {
        this.bigImagesList = bigImagesList;
    }

    public List<String> getBigImagesList() {
        return bigImagesList;
    }

    /**
     * 刷新数据
     */
    public void notificationDataChange() {
        initView();
    }

    /**
     * item、 click
     *
     * @param onItemClickListener 回调
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * long click listener
     *
     * @param onItemLongClickListener 回调
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MAX_WIDTH == 0) {
            int width = measureWidth(widthMeasureSpec);
            if (width > 0) {
                MAX_WIDTH = width;
                if (imagesList != null && imagesList.size() > 0) {
                    setList(imagesList);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            // result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
            // + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void initImageLayoutParams() {
        int wrap = LayoutParams.WRAP_CONTENT;
        int match = LayoutParams.MATCH_PARENT;

        onePicPara = new LayoutParams(pxOneMaxWandH, wrap);

        moreParaColumnFirst = new LayoutParams(pxMoreWandH, pxMoreWandH);
        morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
        morePara.setMargins(pxImagePadding, 0, 0, 0);

        rowPara = new LayoutParams(match, wrap);
    }

    // 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
    private void initView() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        if (MAX_WIDTH == 0) {
            //为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
            addView(new View(getContext()));
            return;
        }

        if (imagesList == null || imagesList.size() == 0) {
            return;
        }

        if (imagesList.size() == 1) {
            addView(createImageView(0, false));
        } else {
            int allCount = imagesList.size();
//            if (allCount == 4) {
            MAX_PER_ROW_COUNT = 2;
//            } else {
//                MAX_PER_ROW_COUNT = 3;
//            }
            int rowCount = allCount / MAX_PER_ROW_COUNT
                    + (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);// 行数
            /*设置行数*/
            for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
                LinearLayout rowLayout = new LinearLayout(getContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                rowLayout.setLayoutParams(rowPara);
                if (rowCursor != 0) {
                    rowLayout.setPadding(0, pxImagePadding, 0, 0);
                }

                int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT
                        : allCount % MAX_PER_ROW_COUNT;//每行的列数
                if (rowCursor != rowCount - 1) {
                    columnCount = MAX_PER_ROW_COUNT;
                }
                addView(rowLayout);

                int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// 行偏移
                /*设置每一列中imageview的数量*/
                for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
                    int position = columnCursor + rowOffset;
                    ImageView im = createImageView(position, true);
                    imageViewList.add(im);
                    rowLayout.addView(im);

                }
            }
        }
    }

    /**
     * 创建imageview放到每一行中
     *
     * @param position
     * @param isMultiImage
     * @return
     */
    private ImageView createImageView(int position, final boolean isMultiImage) {
        String url = imagesList.get(position);
        ImageView imageView = new ColorFilterImageView(getContext());
        if (isMultiImage) {
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
        } else {
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ScaleType.FIT_START);
            imageView.setMaxHeight(pxOneMaxWandH);
            imageView.setLayoutParams(onePicPara);
        }

        imageView.setTag(position);
        imageView.setId(url.hashCode());
        imageView.setOnClickListener(mImageViewOnClickListener);
        imageView.setOnLongClickListener(mImageViewOnLongClickListener);
        /*context*/
        if (RegisterForContextMenu) {
            ((Activity) context).registerForContextMenu(imageView);
            imageView.setOnCreateContextMenuListener(new OnContextMenuCreat());
        }
        ImageLoader.getInstance().displayImage(url, imageView);
        return imageView;
    }

    // 图片点击事件
    private OnClickListener mImageViewOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
            }
        }
    };

    /**
     * 长按事件
     */
    private OnLongClickListener mImageViewOnLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(view, (Integer) view.getTag());
            }

            return false;
        }
    };

    /**
     * context menu 创建
     */
    private class OnContextMenuCreat implements OnCreateContextMenuListener {

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            ((Activity) context).getMenuInflater().inflate(R.menu.menu_context_image_option, contextMenu);
            if (imagesList.size() == 1) {
                contextMenu.removeItem(2);
            }
        }
    }

    /**
     * 获取全部的imageView 控件
     *
     * @return
     */
    public List<ImageView> getImageViewList() {
        return imageViewList;
    }

    /**
     * 获取其中的url
     *
     * @return
     */
    public List<String> getImagesList() {
        return imagesList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 长按
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}