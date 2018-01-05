package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by Chick on 8/9/2017.
 */

public class TankList {
    public String status;
    public String message;
    public data data;

    public class data
    {
        public List<tanks> tanks;
        public List<product_others> product_others;

        public class product_others
        {
            public int product_id;
            public String product_name;
            public String image;
            public String unit;
            public long inventory;
        }

        public class tanks {
            public int tank_id;
            public String tank_code;
            public int product_id;
            public int filling_station_id;
            public int volume;
            public int volume_warning;
            public int width;
            public int hight;
            public int length;
            public int filling_station_inventory_id;
            public int quantity;
            public String product_code;
            public String product_name;
            public String product_short_name;
            public float percent;
        }
    }

}
