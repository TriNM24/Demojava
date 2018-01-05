package fpt.isc.nshreport.models.objectParse;

import java.util.List;

/**
 * Created by Chick on 8/9/2017.
 */

public class NotificationList {
    public int total;
    public int per_page;
    public int current_page;
    public int last_page;
    public List<data> data;

    public class data {
        public int notification_id;
        public int notification_category_id;
        public String action_name;
        public int actor_id;
        public String actor_display_name;
        public String actor_image;
        public int object_id;
        public String object_display_name;
        public String object_content;
        public String date_created;
        public String notification_category_name;
        public String view_name_app_android;
        public int is_read;
        public int is_sent;
    }
}
