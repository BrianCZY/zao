package com.hzu.zao.interfaces;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 *ͼƬ�첽������ɺ�ص�
 * @author yanbin
 *
 */
public interface OnImageDownload {
	void onDownloadSucc(Bitmap bitmap,String c_url,ImageView imageView);
}
