package com.hzu.zao.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * ����
 * 
 * 
 * @author Nearby Yang
 * 
 *         Create at 2015-5-1 ����2:56:28
 */
public class Comment extends BmobObject {
	private String quest_id;
	private String comContent;
	private MyUser user_id;
	private MyUser toWho;
	private List<String> isGood;
	private List<String> isBad;
	private Boolean isHide;
	private Boolean isBest;
	private Boolean isRead=false;
	
	
	
	
	public String getQuest_id() {
		return quest_id;
	}
	public void setQuest_id(String quest_id) {
		this.quest_id = quest_id;
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
	
	public List<String> getIsGood() {
		return isGood;
	}
	public void setIsGood(List<String> isGood) {
		this.isGood = isGood;
	}
	public List<String> getIsBad() {
		return isBad;
	}
	public void setIsBad(List<String> isBad) {
		this.isBad = isBad;
	}
	public Boolean isHide() {
		return isHide;
	}
	public void setHide(Boolean isHide) {
		this.isHide = isHide;
	}
	public Boolean isBest() {
		return isBest;
	}
	public void setBest(Boolean isBest) {
		this.isBest = isBest;
	}
	public Boolean getIsHide() {
		return isHide;
	}
	public void setIsHide(Boolean isHide) {
		this.isHide = isHide;
	}
	public Boolean getIsBest() {
		return isBest;
	}
	public void setIsBest(Boolean isBest) {
		this.isBest = isBest;
	}
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

}
