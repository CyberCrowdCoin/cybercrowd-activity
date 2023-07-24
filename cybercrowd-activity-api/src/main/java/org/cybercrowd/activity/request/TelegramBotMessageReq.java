package org.cybercrowd.activity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TelegramBotMessageReq implements Serializable {


    /**
     * update_id : 419084474
     * message : {"message_id":5,"from":{"id":890727749,"is_bot":false,"first_name":"Shawn","username":"shawnACH","language_code":"zh-hans"},"chat":{"id":-1001586969305,"title":"bot test","username":"testbotgroupwww","type":"supergroup"},"date":1630670487,"text":"@bitstore_group_bot yyyyyooooppp","entities":[{"offset":0,"length":19,"type":"mention"}]}
     */
    private long update_id;
    private MessageBean message;

    @Data
    public static class MessageBean implements Serializable {
        /**
         * message_id : 5
         * from : {"id":890727749,"is_bot":false,"first_name":"Shawn","username":"shawnACH","language_code":"zh-hans"}
         * chat : {"id":-1001586969305,"title":"bot test","username":"testbotgroupwww","type":"supergroup"}
         * date : 1630670487
         * text : @bitstore_group_bot yyyyyooooppp
         * entities : [{"offset":0,"length":19,"type":"mention"}]
         */

        private long message_id;
        private FromBean from;
        private ChatBean chat;
        private long date;
        private String text;
        private List<EntitiesBean> entities;
        //动画
        private AnimationBean animation;
        //语音
        private AudioBean audio;
        //文件
        private DocumentBean document;
        //图片
        private List<PhotoBean> photo;

        @Data
        public static class FromBean implements Serializable {
            /**
             * id : 890727749
             * is_bot : false
             * first_name : Shawn
             * username : shawnACH
             * language_code : zh-hans
             */

            private long id;
            private boolean is_bot;
            private String first_name;
            private String username;
            private String language_code;
        }

        @Data
        public static class ChatBean implements Serializable {
            /**
             * id : -1001586969305
             * title : bot test
             * username : testbotgroupwww
             * type : supergroup
             */

            private long id;
            private String title;
            private String username;
            private String type;
        }

        @Data
        public static class EntitiesBean implements Serializable {
            /**
             * offset : 0
             * length : 19
             * type : mention
             */

            private long offset;
            private long length;
            private String type;

        }

        @Data
        public static class AnimationBean {

            private String file_id;
            private String file_unique_id;
            private long width;
            private long height;
            private long duration;
            private Object thumb;
            private String file_name;
            private String mime_type;
            private long file_size;
        }

        @Data
        public static class AudioBean{
            private String file_id;
            private String file_unique_id;
            private long duration;
            private String performer;
            private String title;
            private String file_name;
            private String mime_type;
            private long file_size;
            private Object thumb;
        }

        @Data
        public static class DocumentBean{
            private String file_id;
            private String file_unique_id;
            private Object thumb;
            private String file_name;
            private String mime_type;
            private long file_size;

        }

        @Data
        public static class PhotoBean{
            private String file_id;
            private String file_unique_id;
            private long width;
            private long height;
            private long file_size;
        }
    }
}
