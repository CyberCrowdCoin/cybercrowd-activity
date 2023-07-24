package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DiscordInfoDto implements Serializable {

    private String userAvatar;
    private String userNickName;

}
