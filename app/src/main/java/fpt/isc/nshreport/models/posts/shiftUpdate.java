package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chick on 8/7/2017.
 */

public class shiftUpdate {
    @SerializedName("filling_station_id")
    @Expose
    private Integer fillingStationId;
    @SerializedName("is_update")
    @Expose
    private Integer isUpdate;
    @SerializedName("salse_id")
    @Expose
    private Integer salseId;
    @SerializedName("pumps")
    @Expose
    private List<pumpUdateReport> pumps = null;

    @SerializedName("products")
    @Expose
    private List<product> products;

    public List<product> getProducts() {
        return products;
    }

    public void setProducts(List<product> products) {
        this.products = products;
    }

    public Integer getFillingStationId() {
        return fillingStationId;
    }

    public void setFillingStationId(Integer fillingStationId) {
        this.fillingStationId = fillingStationId;
    }

    public Integer getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Integer isUpdate) {
        this.isUpdate = isUpdate;
    }

    public Integer getSalseId() {
        return salseId;
    }

    public void setSalseId(Integer salseId) {
        this.salseId = salseId;
    }

    public List<pumpUdateReport> getPumps() {
        return pumps;
    }

    public void setPumps(List<pumpUdateReport> pumps) {
        this.pumps = pumps;
    }
}
