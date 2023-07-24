package org.cybercrowd.activity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActivityNewYear2022DiscrodLoginReq implements Serializable {

    private String oauthCode;
    private String redirectUri;

    private String parentInviteCode;
}
