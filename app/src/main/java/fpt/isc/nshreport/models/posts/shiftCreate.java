package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chick on 8/7/2017.
 */

public class shiftCreate {
    @SerializedName("selldate")
    @Expose
    private String selldate;
    @SerializedName("shift_id")
    @Expose
    private Integer shiftId;
    @SerializedName("salse_id")
    @Expose
    private Integer salseId;
    @SerializedName("filling_station_id")
    @Expose
    private String fillingStationId;
    @SerializedName("pumps")
    @Expose
    private List<pump> pumps = null;

    @SerializedName("products")
    @Expose
    private List<productAdd> products = null;

    public List<productAdd> getProducts() {
        return products;
    }

    public void setProducts(List<productAdd> products) {
        this.products = products;
    }

    public String getSelldate() {
        return selldate;
    }

    public void setSelldate(String selldate) {
        this.selldate = selldate;
    }

    public Integer getShiftId() {
        return shiftId;
    }

    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
    }

    public Integer getSalseId() {
        return salseId;
    }

    public void setSalseId(Integer salseId) {
        this.salseId = salseId;
    }

    public String getFillingStationId() {
        return fillingStationId;
    }

    public void setFillingStationId(String fillingStationId) {
        this.fillingStationId = fillingStationId;
    }

    public List<pump> getPumps() {
        return pumps;
    }

    public void setPumps(List<pump> pumps) {
        this.pumps = pumps;
    }
}

