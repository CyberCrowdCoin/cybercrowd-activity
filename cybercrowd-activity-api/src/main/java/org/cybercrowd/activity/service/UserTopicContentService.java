package org.cybercrowd.activity.service;

import org.cybercrowd.activity.dto.TelegramBotMessageDto;

public interface UserTopicContentService {

    void telegramBotMessageHandle(TelegramBotMessageDto telegramBotMessageDto);

}
