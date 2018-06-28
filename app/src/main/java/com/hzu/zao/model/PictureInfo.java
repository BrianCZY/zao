package com.hzu.zao.model;

import java.io.Serializable;

/**
 * 图片的信息：x、y坐标，长宽等
 *
 *
 * Created by Nearby Yang on 2016-02-19.
 */
public class PictureInfo implements Serializable {

    public int height;
    public int width;
    public int x;
    public int y;
    public int attachmentId;
    public String imageId;
    //        原图
    public String imageUrl;
    //        缩略图
    public String smallImageUrl;

    public PictureInfo() {
    }

    public PictureInfo(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public PictureInfo(String imageUrl, String smallImageUrl) {
        this.imageUrl = imageUrl;
        this.smallImageUrl = smallImageUrl;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }
}
