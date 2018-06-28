package com.hzu.zao.utils;

import android.content.Context;
import android.graphics.Rect;
import android.widget.ImageView;

import com.hzu.zao.model.PictureInfo;

import java.util.List;

public class EvaluateUtil {

    /**
     * 批量计算
     * 计算每个图片的坐标、大图、缩略图地址
     *
     * @param iv_image
     * @param pictureInfoList
     * @param position
     */
    public static void setupCoords(Context context, ImageView iv_image, List<PictureInfo> pictureInfoList, int position) {
//        x方向的第几个
        int xn = position % 3 + 1;
//        y方向的第几个
        int yn = position / 3 + 1;
//        x方向的总间距
        int h = (xn - 1) * DisplayUtils.dip2px(context, 4);
//        y方向的总间距
        //        图片宽高
        int height = iv_image.getHeight();
        int width = iv_image.getWidth();
//        获取当前点击图片在屏幕上的坐标
        int[] points = new int[2];
        iv_image.getLocationInWindow(points);
//        获取第一张图片的坐标
        int x0 = points[0] - (width + h) * (xn - 1);
        int y0 = points[1] - (height + h) * (yn - 1);
//        给所有图片添加坐标信息
        for (int i = 0; i < pictureInfoList.size(); i++) {
            PictureInfo pictureInfo = pictureInfoList.get(i);
            pictureInfo.width = width;
            pictureInfo.height = height;
            pictureInfo.x = x0 + (i % 3) * (width + h);
            pictureInfo.y = y0 + (i / 3) * (height + h) - DisplayUtils.getStatusBarHeight(iv_image);
        }
    }

    /**
     * Integer 估值器
     * 
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public static Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /**
     * Float 估值器
     * 
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public static Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * Argb 估值器
     * 
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public static Object evaluateArgb(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
                | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
                | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
                | (int) ((startB + (int) (fraction * (endB - startB))));
    }
    
    /**
     * Rect 估值器
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public static Rect evaluateRect(float fraction, Rect startValue, Rect endValue) {
        return new Rect(startValue.left + (int)((endValue.left - startValue.left) * fraction),
                startValue.top + (int)((endValue.top - startValue.top) * fraction),
                startValue.right + (int)((endValue.right - startValue.right) * fraction),
                startValue.bottom + (int)((endValue.bottom - startValue.bottom) * fraction));
    }
}
