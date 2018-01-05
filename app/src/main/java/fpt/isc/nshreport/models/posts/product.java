package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Chick on 11/30/2017.
 */

public class product {
    @SerializedName("daily_report_detail_id")
    @Expose
    private Long id;
    @SerializedName("report_num")
    @Expose
    private Long reportNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportNum() {
        return reportNum;
    }

    public void setReportNum(Long reportNum) {
        this.reportNum = reportNum;
    }
}
