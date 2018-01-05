package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by Chick on 8/7/2017.
 */

public class UserList {
    public int total;
    public String status;
    public String messase;
    public List<data> data;
    public class data
    {
        public int user_id;
        public String email;
        public String full_name;
    }
}
