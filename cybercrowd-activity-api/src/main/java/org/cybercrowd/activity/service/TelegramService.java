package org.cybercrowd.activity.service;

import org.cybercrowd.activity.dto.TelegramBotReplyPhotoReq;
import org.cybercrowd.activity.request.TelegramBotMessageReplyReq;
import org.cybercrowd.activity.request.TelegramBotMessageReq;
import org.cybercrowd.activity.response.TelegramBotMessageRes;

public interface TelegramService {

    TelegramBotMessageRes botWebHook(TelegramBotMessageReq telegramBotMessageReq);

    void botMessageReply(TelegramBotMessageReplyReq telegramBotMessageReplyReq);

    void botPrivateMessage(TelegramBotMessageReplyReq telegramBotMessageReplyReq);

    void botMessagePhotoReply(TelegramBotReplyPhotoReq telegramBotReplyPhotoReq);

    boolean getTelegramChatMember(long chatId,long userId);

}
