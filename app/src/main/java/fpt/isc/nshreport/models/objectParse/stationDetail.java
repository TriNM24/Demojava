package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by ADMIN on 7/29/2017.
 */

public class stationDetail {
    public int filling_station_id;
    public String filling_station_code;
    public String filling_station_name;


    public List<fuel_filling_columns> fuel_filling_columns;
    public class fuel_filling_columns
    {
        public int fuel_filling_column_id;
        public String fuel_filling_column_code;
        public int filling_station_id;
        public int is_active;
        public List<pumps> pumps;

        public class pumps
        {
            public int pump_id;
            public String pump_name;
            public String pump_type;
            public int is_actived;
            public String location;
        }
    }
}
