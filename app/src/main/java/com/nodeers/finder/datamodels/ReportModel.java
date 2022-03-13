package com.nodeers.finder.datamodels;

public class ReportModel {

    private String userId,reportText;

    public ReportModel() {
    }

    public ReportModel(String userId, String reportText) {
        this.userId = userId;
        this.reportText = reportText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }
}
