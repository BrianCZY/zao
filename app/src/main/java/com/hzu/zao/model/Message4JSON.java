package com.hzu.zao.model;

import java.util.ArrayList;
import java.util.List;

public class Message4JSON {

	
	public List<commentDetail> comment;
	public List<replyDetail> reply;
	public List<questionDetail> question;
	public List<comm4replyDetail> comm_reply;
	
	
	public class commentDetail{
		
		public String comContent;
		public String createdAt;
		public ArrayList<String> isBad;
		public ArrayList<String> isGood;
		public boolean isHide;
		public String objectId;
		public String quest_id;
		public MyUser toWho;
		public MyUser user_id;
		public String updatedAt;
		public boolean isRead;
		
		
	}

	
	public class replyDetail{
		public String comContent;
		public String comm_id;
		public String createdAt;
		public String objectId;
		public MyUser toWho;
		public String updatedAt;
		public MyUser user_id;
		public boolean isRead;
		
	}
	
	public class questionDetail{
		
		public String content;
		public String createdAt;
		public String objectId;
		public String updatedAt;
		public MyUser user_id;
	}
	
	

	
	public class comm4replyDetail{
		public String objectId;
		public String quest_id;
	}
	
}
