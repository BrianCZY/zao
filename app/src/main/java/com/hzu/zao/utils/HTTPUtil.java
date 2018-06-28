package com.hzu.zao.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * �����첽����ͼƬ �������֮�󻺴浽���ء�ʹ��Cache�����Bitmap��key�Ǹ�ͼƬ��url����ȡ��ʱ�����Ӧ��Url����
 * 
 * @author Nearby Yang
 * 
 *         Create at 2015 ����3:25:44
 */
public class HTTPUtil {

	public Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		URL bitmapUrl = null;
		InputStream inputSream = null;
		try {
			bitmapUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) bitmapUrl
					.openConnection();
			conn.setDoInput(true);
			inputSream = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(inputSream);
			inputSream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmap;

	}

}
