package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TwitterInfoDto implements Serializable {

    private String userAvatar;
    private String userNickName;

}
