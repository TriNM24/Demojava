package fpt.isc.nshreport.models;

/**
 * Created by PhamTruong on 05/06/2017.
 */

public class ReportPhotos {
    private int id;
    private String photoName;
    private String updateTime;
    private String photoPath;
    private int salesReportId;
    private int pumpLineId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
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
}
