package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by Chick on 8/7/2017.
 */

public class ShiftList {
    public int total;
    public int current_page;
    public String status;
    public String messase;
    public List<data> data;
    public class data
    {
        public int shift_id;
        public String shift_code;
        public String shift_name;
    }
}
