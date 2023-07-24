package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TwitterFollowEeventsDto implements Serializable {

    /**
     * for_user_id : 1421074155156512769
     * follow_events : [{"type":"follow","created_timestamp":"1670220121403","target":{"id":"1421074155156512769","default_profile_image":false,"profile_background_image_url":"","friends_count":4,"favourites_count":0,"profile_link_color":-1,"profile_background_image_url_https":"","utc_offset":0,"screen_name":"CyberCrowdDev","is_translator":false,"followers_count":1,"name":"CyberCrowdDevelop","lang":"","profile_use_background_image":false,"created_at":"Fri Jul 30 11:44:17 +0000 2021","profile_text_color":-1,"notifications":false,"protected":false,"statuses_count":0,"url":"","contributors_enabled":false,"default_profile":true,"profile_sidebar_border_color":-1,"time_zone":"","geo_enabled":false,"verified":false,"profile_image_url":"http://pbs.twimg.com/profile_images/1597496851913396224/jEwQMhtA_normal.jpg","following":false,"profile_image_url_https":"https://pbs.twimg.com/profile_images/1597496851913396224/jEwQMhtA_normal.jpg","profile_background_tile":false,"listed_count":1,"profile_sidebar_fill_color":-1,"location":"","follow_request_sent":false,"description":"CyberCrowd Develop","profile_background_color":-1},"source":{"id":"633004278","default_profile_image":false,"profile_background_image_url":"","friends_count":39,"favourites_count":0,"profile_link_color":-1,"profile_background_image_url_https":"","utc_offset":0,"screen_name":"DawntonChen","is_translator":false,"followers_count":3,"name":"DawntonChen","lang":"","profile_use_background_image":false,"created_at":"Wed Jul 11 14:06:13 +0000 2012","profile_text_color":-1,"notifications":false,"protected":false,"statuses_count":5,"url":"","contributors_enabled":false,"default_profile":true,"profile_sidebar_border_color":-1,"time_zone":"","geo_enabled":false,"verified":false,"profile_image_url":"http://pbs.twimg.com/profile_images/666476552419303425/K-hmwuwM_normal.jpg","following":false,"profile_image_url_https":"https://pbs.twimg.com/profile_images/666476552419303425/K-hmwuwM_normal.jpg","profile_background_tile":false,"listed_count":0,"profile_sidebar_fill_color":-1,"location":"","follow_request_sent":false,"description":"","profile_background_color":-1}}]
     */

    private Long for_user_id;
    private List<FollowEventsBean> follow_events;

    @Data
    public static class FollowEventsBean implements Serializable {
        /**
         * type : follow
         * created_timestamp : 1670220121403
         * target : {"id":"1421074155156512769","default_profile_image":false,"profile_background_image_url":"","friends_count":4,"favourites_count":0,"profile_link_color":-1,"profile_background_image_url_https":"","utc_offset":0,"screen_name":"CyberCrowdDev","is_translator":false,"followers_count":1,"name":"CyberCrowdDevelop","lang":"","profile_use_background_image":false,"created_at":"Fri Jul 30 11:44:17 +0000 2021","profile_text_color":-1,"notifications":false,"protected":false,"statuses_count":0,"url":"","contributors_enabled":false,"default_profile":true,"profile_sidebar_border_color":-1,"time_zone":"","geo_enabled":false,"verified":false,"profile_image_url":"http://pbs.twimg.com/profile_images/1597496851913396224/jEwQMhtA_normal.jpg","following":false,"profile_image_url_https":"https://pbs.twimg.com/profile_images/1597496851913396224/jEwQMhtA_normal.jpg","profile_background_tile":false,"listed_count":1,"profile_sidebar_fill_color":-1,"location":"","follow_request_sent":false,"description":"CyberCrowd Develop","profile_background_color":-1}
         * source : {"id":"633004278","default_profile_image":false,"profile_background_image_url":"","friends_count":39,"favourites_count":0,"profile_link_color":-1,"profile_background_image_url_https":"","utc_offset":0,"screen_name":"DawntonChen","is_translator":false,"followers_count":3,"name":"DawntonChen","lang":"","profile_use_background_image":false,"created_at":"Wed Jul 11 14:06:13 +0000 2012","profile_text_color":-1,"notifications":false,"protected":false,"statuses_count":5,"url":"","contributors_enabled":false,"default_profile":true,"profile_sidebar_border_color":-1,"time_zone":"","geo_enabled":false,"verified":false,"profile_image_url":"http://pbs.twimg.com/profile_images/666476552419303425/K-hmwuwM_normal.jpg","following":false,"profile_image_url_https":"https://pbs.twimg.com/profile_images/666476552419303425/K-hmwuwM_normal.jpg","profile_background_tile":false,"listed_count":0,"profile_sidebar_fill_color":-1,"location":"","follow_request_sent":false,"description":"","profile_background_color":-1}
         */

        private String type;
        private Long created_timestamp;
        private TargetBean target;
        private SourceBean source;

        @Data
        public static class TargetBean implements Serializable {
            /**
             * id : 1421074155156512769
             * default_profile_image : false
             * profile_background_image_url :
             * friends_count : 4
             * favourites_count : 0
             * profile_link_color : -1
             * profile_background_image_url_https :
             * utc_offset : 0
             * screen_name : CyberCrowdDev
             * is_translator : false
             * followers_count : 1
             * name : CyberCrowdDevelop
             * lang :
             * profile_use_background_image : false
             * created_at : Fri Jul 30 11:44:17 +0000 2021
             * profile_text_color : -1
             * notifications : false
             * protected : false
             * statuses_count : 0
             * url :
             * contributors_enabled : false
             * default_profile : true
             * profile_sidebar_border_color : -1
             * time_zone :
             * geo_enabled : false
             * verified : false
             * profile_image_url : http://pbs.twimg.com/profile_images/1597496851913396224/jEwQMhtA_normal.jpg
             * following : false
             * profile_image_url_https : https://pbs.twimg.com/profile_images/1597496851913396224/jEwQMhtA_normal.jpg
             * profile_background_tile : false
             * listed_count : 1
             * profile_sidebar_fill_color : -1
             * location :
             * follow_request_sent : false
             * description : CyberCrowd Develop
             * profile_background_color : -1
             */

            private String id;
            private boolean default_profile_image;
            private String profile_background_image_url;
            private int friends_count;
            private int favourites_count;
            private int profile_link_color;
            private String profile_background_image_url_https;
            private int utc_offset;
            private String screen_name;
            private boolean is_translator;
            private int followers_count;
            private String name;
            private String lang;
            private boolean profile_use_background_image;
            private String created_at;
            private int profile_text_color;
            private boolean notifications;
            private int statuses_count;
            private String url;
            private boolean contributors_enabled;
            private boolean default_profile;
            private int profile_sidebar_border_color;
            private String time_zone;
            private boolean geo_enabled;
            private boolean verified;
            private String profile_image_url;
            private boolean following;
            private String profile_image_url_https;
            private boolean profile_background_tile;
            private int listed_count;
            private int profile_sidebar_fill_color;
            private String location;
            private boolean follow_request_sent;
            private String description;
            private int profile_background_color;
        }

        @Data
        public static class SourceBean implements Serializable {
            /**
             * id : 633004278
             * default_profile_image : false
             * profile_background_image_url :
             * friends_count : 39
             * favourites_count : 0
             * profile_link_color : -1
             * profile_background_image_url_https :
             * utc_offset : 0
             * screen_name : DawntonChen
             * is_translator : false
             * followers_count : 3
             * name : DawntonChen
             * lang :
             * profile_use_background_image : false
             * created_at : Wed Jul 11 14:06:13 +0000 2012
             * profile_text_color : -1
             * notifications : false
             * protected : false
             * statuses_count : 5
             * url :
             * contributors_enabled : false
             * default_profile : true
             * profile_sidebar_border_color : -1
             * time_zone :
             * geo_enabled : false
             * verified : false
             * profile_image_url : http://pbs.twimg.com/profile_images/666476552419303425/K-hmwuwM_normal.jpg
             * following : false
             * profile_image_url_https : https://pbs.twimg.com/profile_images/666476552419303425/K-hmwuwM_normal.jpg
             * profile_background_tile : false
             * listed_count : 0
             * profile_sidebar_fill_color : -1
             * location :
             * follow_request_sent : false
             * description :
             * profile_background_color : -1
             */

            private String id;
            private boolean default_profile_image;
            private String profile_background_image_url;
            private int friends_count;
            private int favourites_count;
            private int profile_link_color;
            private String profile_background_image_url_https;
            private int utc_offset;
            private String screen_name;
            private boolean is_translator;
            private int followers_count;
            private String name;
            private String lang;
            private boolean profile_use_background_image;
            private String created_at;
            private int profile_text_color;
            private boolean notifications;
            private int statuses_count;
            private String url;
            private boolean contributors_enabled;
            private boolean default_profile;
            private int profile_sidebar_border_color;
            private String time_zone;
            private boolean geo_enabled;
            private boolean verified;
            private String profile_image_url;
            private boolean following;
            private String profile_image_url_https;
            private boolean profile_background_tile;
            private int listed_count;
            private int profile_sidebar_fill_color;
            private String location;
            private boolean follow_request_sent;
            private String description;
            private int profile_background_color;
        }
    }
}
