package com.hzu.zao.model;

import android.content.Context;
import cn.bmob.v3.BmobInstallation;

public class MyInstallationId extends BmobInstallation {

	public MyInstallationId(Context context) {
		super(context);
	}
	
	private String uid;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
