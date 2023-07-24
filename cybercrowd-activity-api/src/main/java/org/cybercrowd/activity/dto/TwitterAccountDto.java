package org.cybercrowd.activity.dto;

import java.io.Serializable;

public class TwitterAccountDto implements Serializable {


    /**
     * data : {"profile_image_url":"https://pbs.twimg.com/profile_images/666476552419303425/K-hmwuwM_normal.jpg","username":"DawntonChen","id":"633004278","created_at":"2012-07-11T14:06:13.000Z","name":"DawntonChen"}
     */

    private DataBean data;

    public static class DataBean implements Serializable {
        /**
         * profile_image_url : https://pbs.twimg.com/profile_images/666476552419303425/K-hmwuwM_normal.jpg
         * username : DawntonChen
         * id : 633004278
         * created_at : 2012-07-11T14:06:13.000Z
         * name : DawntonChen
         */

        private String profile_image_url;
        private String username;
        private String id;
        private String created_at;
        private String name;

        public String getProfile_image_url() {
            return profile_image_url;
        }

        public void setProfile_image_url(String profile_image_url) {
            this.profile_image_url = profile_image_url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }
}
