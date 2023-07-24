package org.cybercrowd.activity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TelegramBotMessageReplyReq implements Serializable {

    private long chat_id;
    private String reply_to_message_id;
    private String text;
}
