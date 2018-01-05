package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chick on 8/2/2017.
 */

public class reportUpdate {
    @SerializedName("filling_station_id")
    @Expose
    private Integer fillingStationId;
    @SerializedName("pumps")
    @Expose
    private List<pumpUdateReport> pumps = null;

    public Integer getFillingStationId() {
        return fillingStationId;
    }

    public void setFillingStationId(Integer fillingStationId) {
        this.fillingStationId = fillingStationId;
    }

    public List<pumpUdateReport> getPumps() {
        return pumps;
    }

    public void setPumps(List<pumpUdateReport> pumps) {
        this.pumps = pumps;
    }
}
