package com.hzu.zao.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

public class MyUser extends BmobUser {
    private static final long serialVersionUID = 62445252L;
    private String phone;
    private String qq;
    private BmobFile icon;
    private int age;
    private String sex;
    private String address;
    private int wealth;
    private String proportion;
    private String pwdQuestion;
    private String pwdAnswer;
    private String introduction;
    private String nickName;
    private String installationId;
    private boolean root;
    private String type;
    private BmobDate limitDate;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWealth() {
        return wealth;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public BmobFile getIcon() {
        return icon;
    }

    public void setIcon(BmobFile icon) {
        this.icon = icon;
    }

    public String getPwdQuestion() {
        return pwdQuestion;
    }

    public void setPwdQuestion(String pwdQuestion) {
        this.pwdQuestion = pwdQuestion;
    }

    public String getPwdAnswer() {
        return pwdAnswer;
    }

    public void setPwdAnswer(String pwdAnswer) {
        this.pwdAnswer = pwdAnswer;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BmobDate getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(BmobDate limitDate) {
        this.limitDate = limitDate;
    }
}
