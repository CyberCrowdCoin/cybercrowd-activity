package org.cybercrowd.activity.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class TelegramBotMessageRes implements Serializable {

    private int status = 200;
}
