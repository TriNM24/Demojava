package fpt.isc.nshreport.models;

import java.io.Serializable;

/**
 * Created by PhamTruong on 30/05/2017.
 */

public class SalesReport implements Serializable {
    private static final long SerialVersionUID = 1L;
    private int id;
    private String salesDate;
    private long salesLitre;
    private long salesAmount;
    private String salesStatus;
    private String checkedMan;
    private String salesNote;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSalesDate() {
        return salesDate;
    }

    public void setSalesDate(String salesDate) {
        this.salesDate = salesDate;
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

    public String getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(String salesStatus) {
        this.salesStatus = salesStatus;
    }

    public String getCheckedMan() {
        return checkedMan;
    }

    public void setCheckedMan(String checkedMan) {
        this.checkedMan = checkedMan;
    }

    public String getSalesNote() {
        return salesNote;
    }

    public void setSalesNote(String salesNote) {
        this.salesNote = salesNote;
    }
}
