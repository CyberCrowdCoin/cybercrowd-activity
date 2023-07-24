package org.cybercrowd.activity.service.activemq;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private Logger logger = LoggerFactory.getLogger(ProducerService.class);

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    public void telegramBotMessageQueue(String message){
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue("telegramBotMessageQueue"), message);
        logger.info("telegram bot message,发送消息到mq:{}",message);
    }

    public void twitterFollowMessageQueue(String message){
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue("twitterFollowMessageQueue"), message);
        logger.info("twitter follow message,发送消息到mq:{}",message);
    }

    public void joinDiscord2020NewYearActivityMessageQueue(String message){
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue("discord2020NewYearActivityMessageQueue"), message);
        logger.info("join discord 2022NewYear Activity Message,发送消息到mq:{}",message);
    }
}
