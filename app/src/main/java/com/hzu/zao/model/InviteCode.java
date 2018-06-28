package com.hzu.zao.model;

import cn.bmob.v3.BmobObject;

public class InviteCode extends BmobObject {
	private MyUser creator;
	private MyUser invitees;
	private String inCode;

	public MyUser getCreator() {
		return creator;
	}

	public void setCreator(MyUser creator) {
		this.creator = creator;
	}

	public MyUser getInvitees() {
		return invitees;
	}

	public void setInvitees(MyUser invitees) {
		this.invitees = invitees;
	}

	public String getInCode() {
		return inCode;
	}

	public void setInCode(String inCode) {
		this.inCode = inCode;
	}

	

}
