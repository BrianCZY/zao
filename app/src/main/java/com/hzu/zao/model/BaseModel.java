package com.hzu.zao.model;

import java.io.Serializable;

/**
 * 基本
 * Created by Nearby Yang on 2015-11-16.
 */
public class BaseModel implements Serializable{
    private static final long serialVersionUID = 3266547218925569547L;
    private int code;
    private String data;

    public static final int SUCCESS = 0;
    public static final int FAILE = 1;


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
