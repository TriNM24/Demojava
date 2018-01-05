package fpt.isc.nshreport.models.objectParse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Chick on 8/21/2017.
 */

public class ProductList implements Serializable{
    public String status;
    public String message;
    public data data;
    public class data implements Serializable{
        public long total_money;
        public List<products> products;

        public class products implements Serializable
        {
            public int product_id;
            public String product_name;
            public String product_short_name;
            public List<report> report;

            public class report implements Serializable
            {
                public String date;
                public long total_liter;
                public String total_money;
            }
        }
    }
}
