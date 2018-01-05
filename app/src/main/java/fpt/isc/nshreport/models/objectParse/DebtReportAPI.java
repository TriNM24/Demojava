package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by Chick on 12/19/2017.
 */

public class DebtReportAPI {
    public int total;
    public String per_page;
    public int current_page;
    public int next_page;
    public int last_page;
    public String status;
    public String messase;
    public List<data> data;

    public class data{
        public String dateReport;
        public String totalMoney;
        public List<detail> detail;

        public class detail
        {
            public boolean approved;
            public String time;
            public long money;
            public String note;
            public String image_bill;
        }
    }
}
