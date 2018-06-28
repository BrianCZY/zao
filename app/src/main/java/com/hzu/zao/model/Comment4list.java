package com.hzu.zao.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nearby Yang on 2015-04-09. 存放评论回复数据结构
 */
public class Comment4list {

	private String userName2;
	private String userName4;// 发出评论/回复者 登陆名
	private String NickName;// 发出评论/回复者昵称
	private String user_id;// 发出评论/回复者id
	private String toWho;// 收到评论者--
	private String comContent;// 评论内容
	private String replyContent;
	private String userIcon;// 评论者头像
	private String userSex;
	private String creatAt;// 评论时间
	private String quest_id;// 问题id
	private String comm_id;// 评论id
	private String toWho_id;// 收到回复者id
	private List<String> images;

	private boolean isGood;// 判断是否点赞
	private boolean isBad;// 判断是否点踩
	private boolean isBest;// 判断是否是最佳答案


	public ArrayList<String> goodArray;// good数组
	public ArrayList<String> badArray;// bad数组

	private int good;// 点赞数量
	private int bad;// 点踩数量
	private int wealth;//金币数量

	private int type;// 区分：0 —— 问题、1 —— 评论、2 —— 回复

	public String getUserName2() {
		return userName2;
	}

	public void setUserName2(String userName2) {
		this.userName2 = userName2;
	}

	public String getCreatAt() {
		return creatAt;
	}

	public void setCreatAt(String creatAt) {
		this.creatAt = creatAt;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public String getComContent() {
		return comContent;
	}

	public void setComContent(String comContent) {
		this.comContent = comContent;
	}

	public String getUserName4() {
		return userName4;
	}

	public void setUserName4(String userName4) {
		this.userName4 = userName4;
	}

	public String getNickName() {
		return NickName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getToWho() {
		return toWho;
	}

	public void setToWho(String toWho) {
		this.toWho = toWho;
	}

	public String getQuest_id() {
		return quest_id;
	}

	public void setQuest_id(String quest_id) {
		this.quest_id = quest_id;
	}

	public ArrayList<String> getGoodArray() {
		return goodArray;
	}

	public void setGoodArray(ArrayList<String> goodArray) {
		this.goodArray = goodArray;
	}

	public ArrayList<String> getBadArray() {
		return badArray;
	}

	public void setBadArray(ArrayList<String> badArray) {
		this.badArray = badArray;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getWealth() {
		return wealth;
	}

	public void setWealth(int wealth) {
		this.wealth = wealth;
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

	public String getComm_id() {
		return comm_id;
	}

	public void setComm_id(String comm_id) {
		this.comm_id = comm_id;
	}

	public String getToWho_id() {
		return toWho_id;
	}

	public void setToWho_id(String toWho_id) {
		this.toWho_id = toWho_id;
	}

	public boolean isGood() {
		return isGood;
	}

	public void setGood(boolean isGood) {
		this.isGood = isGood;
	}

	public boolean isBad() {
		return isBad;
	}

	public void setBad(boolean isBad) {
		this.isBad = isBad;
	}

	public boolean isBest() {
		return isBest;
	}

	public void setBest(boolean isBest) {
		this.isBest = isBest;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
}
