package com.nodeers.finder.datamodels;

public class VehicleDataModel {

    private String regNo,formattedDate;
    private String name;
    private String imgUrl;
    private long date;


    public VehicleDataModel() {
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public VehicleDataModel(String regNo, String name, String imgUrl, long date,String formattedDate) {
        this.regNo = regNo;
        this.name = name;
        this.imgUrl = imgUrl;
        this.date = date;
        this.formattedDate = formattedDate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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
