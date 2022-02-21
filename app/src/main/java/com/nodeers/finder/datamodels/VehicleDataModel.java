package com.nodeers.finder.datamodels;

public class VehicleDataModel {

    private String regNo;
    private String name;
    private String imgUrl;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public VehicleDataModel() {
    }

    public VehicleDataModel(String regNo, String name, String imgUrl,String date) {
        this.regNo = regNo;
        this.name = name;
        this.imgUrl = imgUrl;
        this.date = date;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
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
}
