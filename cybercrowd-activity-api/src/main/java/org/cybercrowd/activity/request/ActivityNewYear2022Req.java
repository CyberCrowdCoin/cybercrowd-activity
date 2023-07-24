package org.cybercrowd.activity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActivityNewYear2022Req implements Serializable {

    //活动参与者ID
    private String ownerId;
    //提交内容
    private String content;

}
