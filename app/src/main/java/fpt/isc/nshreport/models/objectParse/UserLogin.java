package fpt.isc.nshreport.models.objectParse;

/**
 * Created by Chick on 7/25/2017.
 */

public class UserLogin {
    public String status;
    public String api_token;
    public String expire_time;
    public user user;
    public class user
    {
        public String user_id;
        public String email;
        public String full_name;
        public String salt;
        public String avatar;
        public String birthday;
        public String gender;
        public String phone;
        public String skype;
        public String is_active;
        public String date_created;
        public String date_modified;
        public String title;
        public String filling_station_id;
    }
}
