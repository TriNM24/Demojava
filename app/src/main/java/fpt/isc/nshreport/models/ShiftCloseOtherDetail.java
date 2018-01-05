package fpt.isc.nshreport.models;

import java.io.Serializable;

/**
 * Created by ADMIN on 8/5/2017.
 */

public class ShiftCloseOtherDetail implements Serializable{

    private long id;
    private String title;
    private String content;
    private String oldLitre;
    private String newLitre;
    private String num;
    private String image;
    private String unit;

    public ShiftCloseOtherDetail() {
        this.newLitre = "0";
    }

    public ShiftCloseOtherDetail(String title, String content, String oldLitre, String newLitre, String num, String image) {
        this.title = title;
        this.content = content;
        this.oldLitre = oldLitre;
        this.newLitre = newLitre;
        this.num = num;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOldLitre() {
        return oldLitre;
    }

    public void setOldLitre(String oldLitre) {
        this.oldLitre = oldLitre;
    }

    public String getNewLitre() {
        return newLitre;
    }

    public void setNewLitre(String newLitre) {
        this.newLitre = newLitre;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
