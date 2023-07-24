package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DiscordOAuthTokenDto implements Serializable {

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String scope;
    private String token_type;
}
