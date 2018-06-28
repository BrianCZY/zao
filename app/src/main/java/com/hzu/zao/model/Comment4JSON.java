package com.hzu.zao.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ����json�����������ش�������б������ݽṹ
 * 
 * @author Nearby Yang
 *
 * Create at 2015-5-2 ����5:16:28
 */
public class Comment4JSON {

	public List<CommentDetail> Comment;
	public List<ReplyDetail> Reply;
	public MyUser quest_user;
	
	public class CommentDetail{
		
		public ArrayList<String> isBad;
		
		public String comContent;
		public String createdAt;
		
		public ArrayList<String> isGood;
		
		public boolean isBest;
		public boolean isHide;
		public String objectId;
		public String quest_id;
		public MyUser toWho;
		public MyUser user_id;
		public String updatedAt;
		
		
		
	}

	
	public class ReplyDetail{
		public String comContent;
		public String comm_id;
		public String createdAt;
		public String objectId;
		public MyUser toWho;
		public String updatedAt;
		public MyUser user_id;
		
	}
	




}
