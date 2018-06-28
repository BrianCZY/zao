package com.hzu.zao.interfaces;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

/**
 * 选择照片回调函数
 *
 * Created by Nearby Yang on 2015-10-24.
 */
public interface GoToUploadImages {

    void Result(List<String> urls, List<BmobFile> files);
    void onError(int statuscode, String errormsg) ;
}
