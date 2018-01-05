package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by Chick on 8/9/2017.
 */

public class PriceList {
    public String status;
    public String message;
    public List<data> data;

    public class data
    {
        public int table_price_station_id;
        public int product_id;
        public long current_price;
        public long new_price;
        public String date_update_price;
        public int is_update_price;
        public int filling_station_id;
        public String product_code;
        public String product_name;
        public String product_short_name;
        public int status_price;
    }

}
