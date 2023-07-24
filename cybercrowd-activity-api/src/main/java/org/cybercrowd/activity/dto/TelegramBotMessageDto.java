package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TelegramBotMessageDto implements Serializable {

    private String userId;
    private String userName;
    private String groupId;
    private String topic;
    private String content;
}
