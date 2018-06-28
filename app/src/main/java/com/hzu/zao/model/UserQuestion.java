package com.hzu.zao.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class UserQuestion extends BmobObject {
	private MyUser user_id;
	private String content;
	private BmobFile img;
	private List<String> images;

	public MyUser getUser_id() {
		return user_id;
	}

	public void setUser_id(MyUser user_id) {
		this.user_id = user_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BmobFile getImg() {
		return img;
	}

	public void setImg(BmobFile img) {
		this.img = img;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
}
