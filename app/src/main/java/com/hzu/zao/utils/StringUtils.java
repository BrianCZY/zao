package com.hzu.zao.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Toast;

import com.hzu.zao.R;
import com.hzu.zao.view.widget.VerticalImageSpan;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理
 */
public class StringUtils {

    /**
     * 匹配字符串中特定的序列，使用正则表达式
     * 匹配这样的字符串[**]，匹配之后再对比
     *
     * @param context
     * @param source
     * @return
     */
    public static SpannableString getEmotionContent(final Context context, String source) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();

        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利用表情名字获取到对应的图片
            Integer imgRes = EmotionUtils.getImgByName(key);
            if (imgRes != 0) {
                // 压缩表情图片
//                int size = (int) tv.getTextSize();
                int size = DisplayUtils.dip2px(context,18);
                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

                VerticalImageSpan span = new VerticalImageSpan(context,scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }




    /**
     * 手机号验证
     *
     * @param phoneNumberStr
     * @return 验证通过返回true
     */
    public static boolean phoneNumberValid(String phoneNumberStr) {

        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(phoneNumberStr);
        b = m.matches();
        return b;

    }

    /**
     * 将全部的用户id对应的头像都添加在string中
     *
     * @param context
     * @param userIds
     * @param avatarList
     * @return
     */
    public static SpannableStringBuilder idConver2Bitmap(final Context context,
                                                         final List<String> userIds,
                                                         List<Bitmap> avatarList,
                                                         final TextLink textLink) {

        String text = "";
        for (String id : userIds) {
            text += id + " ";
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(text);


        for (int i = 0; i < userIds.size(); i++) {
            Pattern pattern = Pattern.compile(userIds.get(i));
            Matcher matcher = pattern.matcher(text);
            ImageSpan imageSpan = new ImageSpan(context, avatarList.get(i));

            while (matcher.find()) {
                ssb.setSpan(imageSpan
                        , matcher.start(), matcher
                        .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            //SPAN_EXCLUSIVE_EXCLUSIVE
                int start = ssb.getSpanStart(imageSpan);
                int end = ssb.getSpanEnd(imageSpan);

/*设置监听*/
            final int finalI = i;
            ssb.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
//                        TextView tx = (TextView) widget;

                        if (textLink != null) {
                            textLink.onclick(userIds.get(finalI));
                        }
//                        Toast.makeText(context,
//                                "Image Clicked " + widget.getId() + " text " + tx.getText(),
//                                Toast.LENGTH_SHORT).show();

                    }

                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        return ssb;
    }

    /**
     * 评论与回复，用户名点击
     *
     * @param context
     * @param str
     * @param textLink
     * @return
     */
    public static SpannableStringBuilder clickUsername(final Context context, final String str, final TextLink textLink) {
        SpannableString spanStr = new SpannableString(str);

        spanStr.setSpan(null, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);

        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {

                if (textLink != null) {
                    textLink.onclick(str);
                }
                LogUtils.d("text click " + str);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.color_name)); // 设置文本颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }

        }, 0, str.length(), 0);

        return ssb;
    }


    /**
     * 地理信息的点击事件
     *
     * @param context
     * @param locationInfo
     * @param textLink
     * @return
     */
    public static SpannableStringBuilder locatiomInfo(final Context context,
                                                      final String locationInfo,
                                                      final TextLink textLink) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(locationInfo);

        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Toast.makeText(context, "地址 locationInfo ", Toast.LENGTH_LONG).show();
                if (textLink != null) {
                    textLink.onclick(locationInfo);
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.color_name)); // 设置文本颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }

        }, 0, locationInfo.length(), 0);

        return ssb;
    }

    /**
     * 复制文字
     * 将要复制的文字放到粘贴板上
     * @param context
     * @param str
     */
    public static void CopyText(Context context,String str){
        String label = "moment";
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, str);
        clipboard.setPrimaryClip(clip);
    }



    public interface TextLink {
        void onclick(String str);
    }
}
