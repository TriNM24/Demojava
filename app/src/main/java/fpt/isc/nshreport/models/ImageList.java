package fpt.isc.nshreport.models;

import java.io.Serializable;

/**
 * Created by Chick on 12/4/2017.
 */

public class ImageList implements Serializable{
    private String name;
    private String link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
