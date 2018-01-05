package fpt.isc.nshreport.services;

/**
 * Created by PhamTruong on 29/05/2017.
 */

public class NSHServer {
    public static final String BASE_URL = "http://api.nshpetro.com.vn/";
    public static final String BASE_URL2 = "http://api.eprocon.us";

    public static NSHInterface getServer(){
        return NSHSide.getClient(BASE_URL).create(NSHInterface.class);
    }
}
