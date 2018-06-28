package com.hzu.zao.model;

import java.util.List;

public class UserQuertion4JSON {

    public List<resultsDetail> results;

    public class resultsDetail {

        public String content;
        public String createdAt;
        public String objectId;
        public String updatedAt;
        public List<String> images;
        public MyUser user_id;
    }


}
