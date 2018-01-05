package fpt.isc.nshreport.models;

/**
 * Created by PhamTruong on 31/05/2017.
 */

public class PumpLines {
    private int id;
    private String lineName;
    private long salesLitre;
    private long salesAmount;
    private long checkedLitre;
    private String photos;
    private int pumpId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
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

    public long getCheckedLitre() {
        return checkedLitre;
    }

    public void setCheckedLitre(long checkedLitre) {
        this.checkedLitre = checkedLitre;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public int getPumpId() {
        return pumpId;
    }

    public void setPumpId(int pumpId) {
        this.pumpId = pumpId;
    }
}
