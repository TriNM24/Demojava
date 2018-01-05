package fpt.isc.nshreport.models;

import java.io.Serializable;

/**
 * Created by PhamTruong on 30/05/2017.
 */

public class SalesReportDetails implements Serializable {
    private static final long SerialVersionUID = 1L;
    private int id;
    private int salesReportId;
    private int pumpLineId;
    private long salesLitre;
    private long salesAmount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalesReportId() {
        return salesReportId;
    }

    public void setSalesReportId(int salesReportId) {
        this.salesReportId = salesReportId;
    }

    public int getPumpLineId() {
        return pumpLineId;
    }

    public void setPumpLineId(int pumpLineId) {
        this.pumpLineId = pumpLineId;
    }

    public long getSalesLitre() {
        return salesLitre;
    }

    public void setSalesLitre(long salesLitre) {
        this.salesLitre = salesLitre;
    }

    public long getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(long salesAmount) {
        this.salesAmount = salesAmount;
    }
}
