package com.example.drawableapp;


public class ProjectListData {

    private String imgUrl;
    private String imgName;
    public ProjectListData(String imgUrl, String imgName) {
        this.imgUrl = imgUrl;
        this.imgName = imgName;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getImgName() {
        return imgName;
    }
    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
