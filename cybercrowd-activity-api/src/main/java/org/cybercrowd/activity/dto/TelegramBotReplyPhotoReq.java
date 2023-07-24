package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TelegramBotReplyPhotoReq implements Serializable {

     private long chat_id;
     private String photo;
     private String reply_to_message_id;
}
