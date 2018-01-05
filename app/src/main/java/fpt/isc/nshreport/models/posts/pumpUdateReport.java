package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Chick on 8/2/2017.
 */

public class pumpUdateReport {
    @SerializedName("daily_report_detail_id")
    @Expose
    private Integer dailyReportDetailId;
    @SerializedName("daily_report_image_id")
    @Expose
    private Integer dailyReportImageId;
    @SerializedName("report_num")
    @Expose
    private Long reportNum;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getDailyReportDetailId() {
        return dailyReportDetailId;
    }

    public void setDailyReportDetailId(Integer dailyReportDetailId) {
        this.dailyReportDetailId = dailyReportDetailId;
    }

    public Integer getDailyReportImageId() {
        return dailyReportImageId;
    }

    public void setDailyReportImageId(Integer dailyReportImageId) {
        this.dailyReportImageId = dailyReportImageId;
    }

    public Long getReportNum() {
        return reportNum;
    }

    public void setReportNum(Long reportNum) {
        this.reportNum = reportNum;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
