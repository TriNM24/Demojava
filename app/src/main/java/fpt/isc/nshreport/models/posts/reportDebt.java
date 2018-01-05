package fpt.isc.nshreport.models.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Chick on 12/1/2017.
 */

public class reportDebt {
    @SerializedName("filling_station_id")
    @Expose
    private String fillingStationId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("bill_date")
    @Expose
    private String billDate;
    @SerializedName("image_bill")
    @Expose
    private String imageBill;
    @SerializedName("notes")
    @Expose
    private String notes;

    public String getFillingStationId() {
        return fillingStationId;
    }

    public void setFillingStationId(String fillingStationId) {
        this.fillingStationId = fillingStationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getImageBill() {
        return imageBill;
    }

    public void setImageBill(String imageBill) {
        this.imageBill = imageBill;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
