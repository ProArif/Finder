package com.nodeers.finder.datamodels;

public class UsersData {
    private String name;
    private String imgUrl;
    private String phn;
    private String mUserId;
    private String userType;
    private String thana;
    private String dist;

    public UsersData(String name, String imgUrl, String phn, String mUserId, String userType, String thana, String dist) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.phn = phn;
        this.mUserId = mUserId;
        this.userType = userType;
        this.thana = thana;
        this.dist = dist;
    }

    public String getThana() {
        return thana;
    }

    public void setThana(String thana) {
        this.thana = thana;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public UsersData() {
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public UsersData(String name, String imgUrl, String phn, String mUserId, String userType) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.phn = phn;
        this.mUserId = mUserId;
        this.userType = userType;
    }

//    public UsersData(String phn, String mUserId, String userType) {
//        this.phn = phn;
//        this.mUserId = mUserId;
//        this.userType = userType;
//    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPhn() {
        return phn;
    }

    public void setPhn(String phn) {
        this.phn = phn;
    }
}
