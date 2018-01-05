package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by ADMIN on 7/29/2017.
 */

public class ReportList {
    public int total;
    public String per_page;
    public int current_page;
    public int last_page;
    public String next_page_url;
    public String prev_page_url;
    public int from;
    public int to;
    public List<data> data;

    public class data
    {
        public int daily_report_header_id;
        public String report_date;
        public String status;
        public String user_report_name;
        public String number_report_total="";
        public String amount_report_total="";
    }
}
