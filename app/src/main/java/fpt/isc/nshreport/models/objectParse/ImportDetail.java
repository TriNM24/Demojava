package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by Chick on 8/16/2017.
 */

public class ImportDetail {
    public String status;
    public String message;
    public data data;

    public class data
    {
        public String voucher_code;
        public int filling_station_id;
        public String date_created;
        public String work_flow_type;
        public String status;
        public String date_recived;
        public long total;
        public long total_quantity;
        public long total_money;
        public List<inventory_workflow_detail> inventory_workflow_detail;

        public class inventory_workflow_detail
        {
            public long quantity_recived;
            public long total;
            public String product_name;
            public String product_code;
            public String unit;
            public String tank_name;
            public String tank_code;
        }

    }


}
