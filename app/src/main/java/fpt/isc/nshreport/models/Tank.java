package fpt.isc.nshreport.models;

/**
 * Created by Chick on 1/3/2018.
 */

public class Tank {
    private String title;
    private String name;
    private long litre;
    private float percent;

    private boolean isTank;
    private String product_name;
    private String unit;
    private String image;
    private long inventory;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLitre() {
        return litre;
    }

    public void setLitre(long litre) {
        this.litre = litre;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public boolean isTank() {
        return isTank;
    }

    public void setTank(boolean tank) {
        isTank = tank;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getInventory() {
        return inventory;
    }

    public void setInventory(long inventory) {
        this.inventory = inventory;
    }
}
