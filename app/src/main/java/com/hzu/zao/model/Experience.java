package com.hzu.zao.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Experience extends BmobObject {

	private MyUser user_id;
	private String shareEx;
	private BmobFile shareEx_img;
	private int good;
	private int bad;
	private List<String> images;

	public MyUser getUser_id() {
		return user_id;
	}

	public void setUser_id(MyUser user_id) {
		this.user_id = user_id;
	}

	public String getShareEx() {
		return shareEx;
	}

	public void setShareEx(String shareEx) {
		this.shareEx = shareEx;
	}

	public BmobFile getShareEx_img() {
		return shareEx_img;
	}

	public void setShareEx_img(BmobFile shareEx_img) {
		this.shareEx_img = shareEx_img;
	}

	public int getGood() {
		return good;
	}

	public void setGood(int good) {
		this.good = good;
	}

	public int getBad() {
		return bad;
	}

	public void setBad(int bad) {
		this.bad = bad;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
}
