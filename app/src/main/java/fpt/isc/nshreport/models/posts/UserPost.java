package fpt.isc.nshreport.models.posts;

/**
 * Created by PhamTruong on 29/05/2017.
 */

public class UserPost {
    private String username;
    private String pass;
    private String devicetoken;
    private String platform;
    private String imei;

    public UserPost(String username, String pass, String devicetoken, String platform,String imei) {
        this.username = username;
        this.pass = pass;
        this.devicetoken = devicetoken;
        this.platform = platform;
        this.imei = imei;
    }
}
