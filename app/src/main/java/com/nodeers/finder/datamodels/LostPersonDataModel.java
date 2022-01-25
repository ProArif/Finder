package com.nodeers.finder.datamodels;

public class LostPersonDataModel {

    private String choice_l_f_w,name,father_name,mother_name,grandf_name,body_color,dob,imgUrl;

    public LostPersonDataModel() {
    }

    public LostPersonDataModel(String choice_l_f_w, String name, String father_name, String mother_name,
                               String grandf_name, String body_color, String dob, String imgUrl) {
        this.choice_l_f_w = choice_l_f_w;
        this.name = name;
        this.father_name = father_name;
        this.mother_name = mother_name;
        this.grandf_name = grandf_name;
        this.body_color = body_color;
        this.dob = dob;
        this.imgUrl = imgUrl;
    }

    public String getChoice_l_f_w() {
        return choice_l_f_w;
    }

    public void setChoice_l_f_w(String choice_l_f_w) {
        this.choice_l_f_w = choice_l_f_w;
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
