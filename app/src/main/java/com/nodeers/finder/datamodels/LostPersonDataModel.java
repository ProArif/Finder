package com.nodeers.finder.datamodels;

import java.util.Date;

public class LostPersonDataModel {

    private String name,father_name,mother_name,grandf_name,body_color,dob,imgUrl,case_num,formattedDate;

    private long date;


    public LostPersonDataModel() {
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getCase_num() {
        return case_num;
    }

    public void setCase_num(String case_num) {
        this.case_num = case_num;
    }

    public LostPersonDataModel(String name, String father_name,String grandf_name, String mother_name,
                               String body_color, String dob, String imgUrl, String case_no,long date,String formattedDate) {

        this.name = name;
        this.father_name = father_name;
        this.grandf_name = grandf_name;
        this.mother_name = mother_name;
        this.body_color = body_color;
        this.dob = dob;
        this.imgUrl = imgUrl;
        this.case_num = case_no;
        this.date = date;
        this.formattedDate = formattedDate;

    }

    public LostPersonDataModel(String name, String father_name, String dob) {
        this.name = name;
        this.father_name = father_name;
        this.dob = dob;
    }

    public long getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getMother_name() {
        return mother_name;
    }

    public void setMother_name(String mother_name) {
        this.mother_name = mother_name;
    }

    public String getGrandf_name() {
        return grandf_name;
    }

    public void setGrandf_name(String grandf_name) {
        this.grandf_name = grandf_name;
    }

    public String getBody_color() {
        return body_color;
    }

    public void setBody_color(String body_color) {
        this.body_color = body_color;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
