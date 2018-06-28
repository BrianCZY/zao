package com.hzu.zao.model;

import cn.bmob.v3.BmobObject;

public class Assessment extends BmobObject {

	private MyUser user_id;
	private Comment comment_id;

	public MyUser getUser_id() {
		return user_id;
	}

	public void setUser_id(MyUser user_id) {
		this.user_id = user_id;
	}

	public Comment getComment_id() {
		return comment_id;
	}

	public void setComment_id(Comment comment_id) {
		this.comment_id = comment_id;
	}
}
