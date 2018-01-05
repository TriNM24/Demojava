package fpt.isc.nshreport.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by PhamTruong on 31/05/2017.
 */

public class PumpLinesAdd implements Serializable{
    private int id;
    private int productId;
    private String lineName;
    private long salesLitre;
    private long salesPrice;
    private long salesAmount;
    private String photos;
    private int pumpId;
    private String pumpStatus;
    private int imageID;
    private long approveNum;
    private long reportNum;
    private long reportNumPre;

    private int isGetImage;

    private List<ImageList> imageList;

    public List<ImageList> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageList> imageList) {
        this.imageList = imageList;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getIsGetImage() {
        return isGetImage;
    }

    public void setIsGetImage(int isGetImage) {
        this.isGetImage = isGetImage;
    }

    public long getReportNumPre() {
        return reportNumPre;
    }

    public void setReportNumPre(long reportNumPre) {
        this.reportNumPre = reportNumPre;
    }

    public long getReportNum() {
        return reportNum;
    }

    public void setReportNum(int reportNum) {
        this.reportNum = reportNum;
    }

    public long getApproveNum() {
        return approveNum;
    }

    public void setApproveNum(int approveNum) {
        this.approveNum = approveNum;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

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

    public long getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(int salesPrice) {
        this.salesPrice = salesPrice;
    }

    public long getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(long salesAmount) {
        this.salesAmount = salesAmount;
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

    public String getPumpStatus() {
        return pumpStatus;
    }

    public void setPumpStatus(String pumpStatus) {
        this.pumpStatus = pumpStatus;
    }
}
