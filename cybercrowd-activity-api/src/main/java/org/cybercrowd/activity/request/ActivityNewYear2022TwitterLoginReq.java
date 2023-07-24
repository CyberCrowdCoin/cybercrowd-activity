package org.cybercrowd.activity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActivityNewYear2022TwitterLoginReq implements Serializable {

    private String parentInviteCode; //父邀请码

    private String oauthCode;
    private String redirectUri;
    private String oauthVersion; //1.0 2.0
    private String oauthVerifier;

    //活动参与者ID
    private String ownerId;
}
