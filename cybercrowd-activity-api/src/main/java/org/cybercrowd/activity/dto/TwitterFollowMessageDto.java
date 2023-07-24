package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TwitterFollowMessageDto implements Serializable {

    private String tragetTwitterUserId;
    private String sourceTwitterUserId;
    private String sourceTwitterUserName;
    private Date followTime;
}
