package fpt.isc.nshreport.models;

/**
 * Created by Chick on 8/3/2017.
 */

public class Dashboard {
    private String title;
    private String litre;
    private String money;
    private String color;

    public Dashboard(String title, String litre, String money, String color) {
        this.title = title;
        this.litre = litre;
        this.money = money;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLitre() {
        return litre;
    }

    public void setLitre(String litre) {
        this.litre = litre;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
