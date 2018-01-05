package fpt.isc.nshreport.models.objectParse;

/**
 * Created by Chick on 7/26/2017.
 */

public class CheckVersion {
    public String status;
    public String message;
    public data data;
    public class data
    {
        public String platform;
        public String version;
        public String release_date;
        public String link_update;
        public int flag;
        public String updated_date;
    }
}
