package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by Chick on 8/16/2017.
 */

public class ImportList {
    public int total;
    public int current_page;
    public int last_page;
    public List<data> data;

    public class data{
        public int filling_station_inventory_workflow_header_id;
        public String voucher_code;
        public String status;
        public String user_created_name;
        public long total;
        public long total_money;
        public String date_created;
    }
}
