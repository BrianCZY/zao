package com.hzu.zao.model;

import java.util.List;

public class Experience4JSON {


    public List<ResultDetail> Experience;
    public List<MyUser> userInfo;


    public class ResultDetail {
        public int bad;
        public String createdAt;
        public int good;

        public String objectId;
        public String shareEx;
        public String updatedAt;
        public UserDetail user_id;
        public List<String> images;


    }

    public class UserDetail {
        public String __type;
        public String className;
        public String objectId;

    }




}
