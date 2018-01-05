package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ADMIN on 7/29/2017.
 */

public class reportAdd {
    @SerializedName("selldate")
    @Expose
    private String selldate;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("statusid")
    @Expose
    private String statusid;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("filling_station_id")
    @Expose
    private String fillingStationId;
    @SerializedName("pumps")
    @Expose
    private List<pump> pumps = null;

    public String getSelldate() {
        return selldate;
    }

    public void setSelldate(String selldate) {
        this.selldate = selldate;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStatusid() {
        return statusid;
    }

    public void setStatusid(String statusid) {
        this.statusid = statusid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
