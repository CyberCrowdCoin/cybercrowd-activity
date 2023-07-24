package org.cybercrowd.activity.service.activemq;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.cybercrowd.activity.dto.TelegramBotMessageDto;
import org.cybercrowd.activity.dto.TwitterFollowMessageDto;
import org.cybercrowd.activity.service.ActivityService;
import org.cybercrowd.activity.service.TwitterService;
import org.cybercrowd.activity.service.UserTopicContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerService {

    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    UserTopicContentService userTopicContentService;
    @Autowired
    TwitterService twitterService;
    @Autowired
    ActivityService activityService;

    @JmsListener(destination = "telegramBotMessageQueue")
    public void subscribeTelegramBotMessageQueue(String message) throws Exception{
        logger.info("telegram bot 消息队列,订阅到:{}",message);
        TelegramBotMessageDto telegramBotMessageDto = JSON.parseObject(message,TelegramBotMessageDto.class);
        userTopicContentService.telegramBotMessageHandle(telegramBotMessageDto);
    }

    @JmsListener(destination = "twitterFollowMessageQueue")
    public void subscribeTwitterFollowMessageQueue(String message) throws Exception{
        logger.info("twitter follow 消息队列,订阅到:{}",message);
        TwitterFollowMessageDto twitterFollowMessageDto = JSON.parseObject(message,TwitterFollowMessageDto.class);
        twitterService.twitterFollowHandle(twitterFollowMessageDto);
    }

    @JmsListener(destination = "discord2020NewYearActivityMessageQueue")
    public void subscribeJoinDiscord2020NewYearActivityMessageQueue(String message) throws Exception{
        logger.info("join discord 2022NewYear Activity Message 消息队列,订阅到:{}",message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        activityService.updateActivity2022NewYearDiscordJobStatus((String)jsonObject.get("discordId"));
    }
}
