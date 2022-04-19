package com.nodeers.finder.datamodels;

public class MobileDataModel {

    private String modelName, mobileImei;

    public MobileDataModel() {
    }

    public MobileDataModel(String modelName, String mobileImei) {
        this.modelName = modelName;
        this.mobileImei = mobileImei;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getMobileImei() {
        return mobileImei;
    }

    public void setMobileImei(String mobileImei) {
        this.mobileImei = mobileImei;
    }
}
