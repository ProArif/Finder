package com.nodeers.finder.datamodels;

public class VehicleDataModel {

    private String regNo;
    private String engineNo;
    private String imgUrl;

    public VehicleDataModel() {
    }

    public VehicleDataModel(String regNo, String engineNo, String imgUrl) {
        this.regNo = regNo;
        this.engineNo = engineNo;
        this.imgUrl = imgUrl;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
