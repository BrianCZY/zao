package com.hzu.zao.utils;

/**
 * 图片压缩
 *
 * Created by Nearby Yang on 2016-04-15.
 */

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;

import com.hzu.zao.utils.cache.ACache;
import com.hzu.zao.config.Contants;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 压缩图片
 *
 * Tools for handler picture
 */
public final class BitmapCompression {


    public static void compressBitmap(String sourcePath, String targetPath) {


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(sourcePath, options);

        final float originalWidth = options.outWidth;
        final float originalHeight = options.outHeight;

        float convertedWidth;
        float convertedHeight;

        if (originalWidth > originalHeight) {
            convertedWidth = Contants.IAMGE_MAX_SIZE;
            convertedHeight =  Contants.IAMGE_MAX_SIZE / originalWidth * originalHeight;
        } else {
            convertedHeight =  Contants.IAMGE_MAX_SIZE;
            convertedWidth =  Contants.IAMGE_MAX_SIZE / originalHeight * originalWidth;
        }


        final float ratio = originalWidth / convertedWidth;

        options.inSampleSize = (int) ratio;
        options.inJustDecodeBounds = false;

        Bitmap convertedBitmap = BitmapFactory.decodeFile(sourcePath, options);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        convertedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(new File(targetPath));
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据URI获取图片物理路径
     */
    public static String getAbsoluteImagePath(Uri uri, Activity activity) {

        String[] proj = {
                MediaColumns.DATA
        };
        Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     *
     * @param path
     * @param maxSize
     * @return
     */
    public static Bitmap decodeBitmap(String path, int maxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        final int originalWidth = options.outWidth;
        final int originalHeight = options.outHeight;

        int convertedWidth;
        int convertedHeight;

        if (originalWidth > originalHeight) {
            convertedWidth = maxSize;
            convertedHeight = maxSize / originalWidth * originalHeight;
        } else {
            convertedHeight = maxSize;
            convertedWidth = maxSize / originalHeight * originalWidth;
        }

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = computeSampleSize(options, maxSize, convertedWidth * convertedHeight);

        Bitmap convertedBitmap = BitmapFactory.decodeFile(path, options);

        if (convertedBitmap != null) {
            final int realWidth = convertedBitmap.getWidth();
            final int realHeight = convertedBitmap.getHeight();

        }

        return convertedBitmap;
    }

    /**
     *
     * @param path
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap decodeBitmap(String path, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        final int originalWidth = options.outWidth;
        final int originalHeight = options.outHeight;

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = computeSampleSize(options, maxWidth, maxWidth * maxHeight);

        Bitmap convertedBitmap = BitmapFactory.decodeFile(path, options);

        if (convertedBitmap != null) {
            final int realWidth = convertedBitmap.getWidth();
            final int realHeight = convertedBitmap.getHeight();

        }

        return convertedBitmap;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? minSideLength
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }


    /**
     * 生成8位16进制的缓存因子：规则的8位哈希码，不足前面补零
     * @param string
     * @return
     */
    public static String toRegularHashCode(String string) {
        final String hexHashCode = Integer.toHexString(string.hashCode());
        final StringBuilder stringBuilder = new StringBuilder(hexHashCode);
        while(stringBuilder.length() < 8){
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    /**
     * 保存草稿与删除
     * <p/>
     * Created by Nearby Yang on 2015-10-30.
     */
    public static class DarftUtils {
        private static DarftUtils saveDarftUtils;
        private ACache acache;
        private Activity activity;

        private DarftUtils(Activity mactivity) {
            activity = mactivity;
            acache = ACache.get(mactivity);

        }

        public static DarftUtils builder(Activity mactivity) {

            if (saveDarftUtils == null) {
                synchronized (ACache.class) {
                    if (saveDarftUtils == null) {
                        saveDarftUtils = new DarftUtils(mactivity);
                    }
                }
            }
            return saveDarftUtils;
        }

        /**
         * 保存草稿
         * 分享经验
         */
        public void saveDraft( String content, List<String> imageList) {
            JSONArray imagesJsonArray = new JSONArray(imageList);
            acache.put(Contants.DRAFT_CONTENT_EX,content);
            acache.put(Contants.DRAFT_IMAGE_LIST_EX,imagesJsonArray);
        }

        /**
         * 保存草稿
         * 提问
         */
        public void saveDraftUserQuestion( String content, List<String> imageList) {
            JSONArray imagesJsonArray = new JSONArray(imageList);
            acache.put(Contants.DRAFT_CONTENT_USER_QUESTION,content);
            acache.put(Contants.DRAFT_IMAGE_LIST_USER_QUESTION,imagesJsonArray);
        }
    //    /**
    //     * 保存草稿
    //     */
    //
    //    public void saveDraft(String draftType, String content, String videoPath,
    //                          String videoPreview,
    //                          String locationInfo, String tagLable, List<String> list) {
    //        //文字，位置，图片，标签
    //
    //        JSONArray imagesJsonArray = new JSONArray(list);
    //
    //        acache.put(Contants.DRAFT_TYPE, draftType, Contants.DARFT_LIVE_TIME);
    //        acache.put(Contants.DRAFT_CONTENT, content, Contants.DARFT_LIVE_TIME);
    //        acache.put(Contants.DRAFT_LOCATION_INFO, locationInfo, Contants.DARFT_LIVE_TIME);
    //        acache.put(Contants.DRAFT_TAG, tagLable, Contants.DARFT_LIVE_TIME);
    //        acache.put(Contants.DRAFT_IMAGES_LIST, imagesJsonArray, Contants.DARFT_LIVE_TIME);
    //        acache.put(Contants.DRAFT_VIDEO, videoPath, Contants.DARFT_LIVE_TIME);
    //        acache.put(Contants.DRAFT_VIDEO_PREVIEW, videoPreview, Contants.DARFT_LIVE_TIME);
    //
    //        Toast.makeText(activity, R.string.save_draft_success, Toast.LENGTH_SHORT).show();
    //
    //        LogUtils.i("保存草稿");
    //    }

        /**
         * 删除草稿
         */
        public void deleteDraft() {

            acache.remove(Contants.DRAFT_CONTENT);
            acache.remove(Contants.DRAFT_LOCATION_INFO);
            acache.remove(Contants.DRAFT_TAG);
            acache.remove(Contants.DRAFT_IMAGES_LIST);

        }
    }
}