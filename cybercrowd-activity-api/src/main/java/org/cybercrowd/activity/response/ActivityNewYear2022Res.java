package org.cybercrowd.activity.response;

import lombok.Data;
import org.cybercrowd.activity.dto.BaseResponse;
import org.cybercrowd.activity.dto.DiscordInfoDto;
import org.cybercrowd.activity.dto.TwitterInfoDto;

import java.io.Serializable;

@Data
public class ActivityNewYear2022Res extends BaseResponse implements Serializable {


    private TwitterInfoDto twitterInfoDto;
    private DiscordInfoDto discordInfoDto;

    private String loginToken;

    private String content;
    private boolean twitterFollowStatus;
    private boolean discordStatus;
    private boolean jobStatus;

    private String shareUrl;

    private String ownerId;
}
