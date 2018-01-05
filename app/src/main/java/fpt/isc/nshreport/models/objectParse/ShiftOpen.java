package fpt.isc.nshreport.models.objectParse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Chick on 8/7/2017.
 */

public class ShiftOpen implements Serializable {
    public String status;
    public String message;
    public data data;

    public class data implements Serializable
    {
        public int daily_report_header_id;
        public String report_date;
        public int user_report;
        public int shift_id;
        public int salse_id;
        public String status;

        public daily_report_details daily_report_details;

        public class daily_report_details implements Serializable
        {
            public List<fuel_filling_columns> fuel_filling_columns;
            public List<products> products;

            public class products implements Serializable{
                public int daily_report_detail_id;
                public int daily_report_header_id;
                public long number_shift_open;
                public long number_report;
                public long number_image;
                public long number_approve;
                public long number_defference;
                public long current_price;
                public long sell_number;
                public String note;
                public String status;
                public int product_id;
                public String product_name;
                public String product_image;
                public String unit;
            }

            public class fuel_filling_columns implements Serializable
            {
                public int fuel_filling_column_id;
                public String fuel_filling_column_code;
                public List<pumps> pumps;

                public class pumps implements Serializable
                {
                    public int daily_report_detail_id;
                    public int pump_id;
                    public int product_id;
                    public long number_shift_open;
                    public long number_report;
                    public long number_approve;
                    public int current_price;
                    public String pump_name;
                    public String status;
                    public String location;
                    public List<images> images;
                    public class images implements Serializable
                    {
                        public String daily_report_name;
                        public int daily_report_image_id;
                        public String type;
                    }
                }
            }
        }
    }
}
