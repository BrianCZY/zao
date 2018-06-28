package com.hzu.zao.model;

import cn.bmob.v3.BmobObject;

public class Reply extends BmobObject {
	private String comm_id;
	private String comContent;
	private MyUser user_id;
	private MyUser toWho;
	private Boolean isRead;
	
	public String getComm_id() {
		return comm_id;
	}
	public void setComm_id(String com_id) {
		this.comm_id = com_id;
	}
	public String getComContent() {
		return comContent;
	}
	public void setComContent(String comContent) {
		this.comContent = comContent;
	}
	public MyUser getUser_id() {
		return user_id;
	}
	public void setUser_id(MyUser user_id) {
		this.user_id = user_id;
	}
	public MyUser getToWho() {
		return toWho;
	}
	public void setToWho(MyUser toWho) {
		this.toWho = toWho;
	}
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	

}
