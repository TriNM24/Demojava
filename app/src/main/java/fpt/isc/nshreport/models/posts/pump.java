package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ADMIN on 7/29/2017.
 */

public class pump {
    @SerializedName("fuel_filling_column_id")
    @Expose
    private Integer fuelFillingColumnId;
    @SerializedName("pump_id")
    @Expose
    private Integer pumpId;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("report_num")
    @Expose
    private Long reportNum;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getFuelFillingColumnId() {
        return fuelFillingColumnId;
    }

    public void setFuelFillingColumnId(Integer fuelFillingColumnId) {
        this.fuelFillingColumnId = fuelFillingColumnId;
    }

    public Integer getPumpId() {
        return pumpId;
    }

    public void setPumpId(Integer pumpId) {
        this.pumpId = pumpId;
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
