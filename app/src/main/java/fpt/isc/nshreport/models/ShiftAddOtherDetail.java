package fpt.isc.nshreport.models;

import java.io.Serializable;

/**
 * Created by ADMIN on 8/5/2017.
 */

public class ShiftAddOtherDetail implements Serializable{

    private int id;
    private String title;
    private String content;
    private String num;
    private String image;
    private String unit;

    public ShiftAddOtherDetail() {
    }

    public ShiftAddOtherDetail(String title, String content, String num, String image, String unit) {
        this.title = title;
        this.content = content;
        this.num = num;
        this.image = image;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
