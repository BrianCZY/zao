package com.hzu.zao.model;

import java.util.List;

public class UserRecord {

    // �û��������ļ�¼
    public class MyQuestion {

        public List<QresultsDetail> results;
    }

    // �û��Ļش�
    public class MyAnswer {
        public List<AnswerDetail> comment;
        public List<questDetail> question;
    }

    // �û�����
    public class MyExperience {
        public List<ExDetail> results;
    }

    public class ExDetail {
        public int bad;
        public int good;
        public String createdAt;
        public String updatedAt;
        public String objectId;
        public String shareEx;
        public user_idDetail user_id;
        public List<String> images;
    }

    public class AnswerDetail {
        public String comContent;
        public String createdAt;
        public String updatedAt;
        public String objectId;
        public String quest_id;
        public userT toWho;
        public user_idDetail user_id;

    }

    public class questDetail {
        public String content;
        public String createdAt;
        public String objectId;
        public String updatedAt;
        public user_idDetail user_id;
        public List<String> images;
    }

    // �û�����

    public class QresultsDetail {
        public String content;
        public String createdAt;
        public String objectId;
        public String updatedAt;
        public user_idDetail user_id;
        public List<String> images;
    }

    public class user_idDetail {
        public String address;
        public int age;
        public String createdAt;
        public String email;
        public boolean emailVerified;
        public IconDetail icon;
        public String introduction;
        public String nickName;
        public String objectId;
        public String phone;
        public String proportion;
        public String pwdAnswer;
        public String pwdQuestion;
        public String qq;
        public String sex;
        public String updatedAt;
        public String username;
        public int wealth;

    }

    public class userT {
        public String __typel;
        public String className;
        public String objectId;
    }

    public class IconDetail {
        public String __type;
        public String filename;
        public String group;
        public String url;
    }
}
