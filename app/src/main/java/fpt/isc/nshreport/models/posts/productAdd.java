package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Chick on 11/30/2017.
 */

public class productAdd {
    @SerializedName("product_id")
    @Expose
    private Integer id;
    @SerializedName("report_num")
    @Expose
    private Long reportNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getReportNum() {
        return reportNum;
    }

    public void setReportNum(Long reportNum) {
        this.reportNum = reportNum;
    }
}
