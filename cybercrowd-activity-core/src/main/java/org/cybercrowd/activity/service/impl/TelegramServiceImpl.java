package org.cybercrowd.activity.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.cybercrowd.activity.dto.TelegramBotReplyPhotoReq;
import org.cybercrowd.activity.request.TelegramBotMessageReplyReq;
import org.cybercrowd.activity.request.TelegramBotMessageReq;
import org.cybercrowd.activity.response.TelegramBotMessageRes;
import org.cybercrowd.activity.service.ActivityTopicService;
import org.cybercrowd.activity.service.TelegramGroupService;
import org.cybercrowd.activity.service.TelegramService;
import org.cybercrowd.activity.service.activemq.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@Service("telegramService")
public class TelegramServiceImpl implements TelegramService {

    private Logger logger = LoggerFactory.getLogger(TelegramServiceImpl.class);

    @Value("${telegram.send_msssage_url}")
    private String telegramSendMessageUrl;

    @Value("${telegram.get_chat_member_url}")
    private String telegramChatMemberUrl;

    @Value("${telegram.send_message_photo_url}")
    private String telegramSendMessagePhotoUrl;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProducerService producerService;
    @Autowired
    TelegramGroupService telegramGroupService;
    @Autowired
    ActivityTopicService activityTopicService;

    @Override
    public TelegramBotMessageRes botWebHook(TelegramBotMessageReq telegramBotMessageReq) {
        TelegramBotMessageRes telegramBotMessageRes = new TelegramBotMessageRes();
        logger.info("接收到来自telegram的消息:{}", JSON.toJSONString(telegramBotMessageReq));
        try {
            TelegramBotMessageReq.MessageBean message = telegramBotMessageReq.getMessage();
            if (null != message) {
                TelegramBotMessageReq.MessageBean.ChatBean chatData = message.getChat();
                TelegramBotMessageReq.MessageBean.FromBean fromData = message.getFrom();

                String sendUserId = String.valueOf(fromData.getId());
                String messageText = message.getText();
                if (!StringUtils.hasText(messageText)) {
                    //没有消息内容，直接返回吧
                    return telegramBotMessageRes;
                }
//                String groupId = String.valueOf(chatData.getId());
//                //检查群ID是否正确
//                //验证消息是不是发送给机器人的
//                TelegramGroup telegramGroup = telegramGroupService.findTelegramGroupByGroupId(groupId);
//                if (null != telegramGroup && messageText.indexOf(telegramGroup.getTelegramBotName()) > -1) {
//                    //获取话题
//                    String topic = messageText.substring(messageText.indexOf("#"), messageText.lastIndexOf("#") + 1);
//                    //验证话题是否有效
//                    if (activityTopicService.activityTopicStatus(topic)) {
//                        String content = messageText.substring(messageText.lastIndexOf("#") + 1, messageText.length());
//                        if (StringUtils.hasText(content)) {
//                            //保存到队列中的数据
//                            TelegramBotMessageDto telegramBotMessageDto = new TelegramBotMessageDto();
//                            telegramBotMessageDto.setUserId(sendUserId);
//                            telegramBotMessageDto.setUserName(fromData.getUsername());
//                            telegramBotMessageDto.setGroupId(groupId);
//                            telegramBotMessageDto.setTopic(topic);
//                            telegramBotMessageDto.setContent(content.replace(telegramGroup.getTelegramBotName(), ""));
//                            //发布MQ消息
//                            producerService.telegramBotMessageQueue(JSON.toJSONString(telegramBotMessageDto));
//                        }
//                    }
//                }
            }

        }catch (Exception e){
            logger.info("telegram bot消息 处理异常:{}",e.getMessage(),e);
        }
        telegramBotMessageRes.setStatus(200);
        return telegramBotMessageRes;
    }

    @Override
    public void botMessageReply(TelegramBotMessageReplyReq telegramBotMessageReplyReq) {
        HttpResponse execute = HttpUtil.createPost(telegramSendMessageUrl)
                .contentType("application/json")
                .body(JSON.toJSONString(telegramBotMessageReplyReq)).execute();
        int status = execute.getStatus();
        JSONObject jsonObject = JSON.parseObject(execute.body());
        if(200 != status){
            boolean isOk = (boolean) jsonObject.get("ok");
            Object description = jsonObject.get("description");
            logger.info("发送Telegram消息失败,失败描述:{}",(String)description);
        }else {
            logger.info("发送Telegram消息成功...");
        }
    }

    @Override
    public void botPrivateMessage(TelegramBotMessageReplyReq telegramBotMessageReplyReq) {
        HttpResponse execute = HttpUtil.createPost(telegramSendMessageUrl)
                .contentType("application/json")
                .body(JSON.toJSONString(telegramBotMessageReplyReq)).execute();
        int status = execute.getStatus();
        JSONObject jsonObject = JSON.parseObject(execute.body());
        if(200 != status){
            boolean isOk = (boolean) jsonObject.get("ok");
            Object description = jsonObject.get("description");
            logger.info("发送Telegram私信消息失败,失败描述:{}",(String)description);
        }else {
            logger.info("发送Telegram私信消息成功...");
        }
    }

    @Override
    public void botMessagePhotoReply(TelegramBotReplyPhotoReq telegramBotReplyPhotoReq) {
        HttpResponse execute = HttpUtil.createPost(telegramSendMessagePhotoUrl)
                .contentType("application/json")
                .body(JSON.toJSONString(telegramBotReplyPhotoReq)).execute();
        int status = execute.getStatus();
        JSONObject jsonObject = JSON.parseObject(execute.body());
        if(200 != status){
            boolean isOk = (boolean) jsonObject.get("ok");
            Object description = jsonObject.get("description");
            logger.info("发送Telegram 文件消息失败,失败描述:{}",(String)description);
        }else {
            logger.info("发送Telegram 文件消息成功...");
        }
    }

    @Override
    public boolean getTelegramChatMember(long chatId, long userId) {
        String url = MessageFormat.format(telegramChatMemberUrl,String.valueOf(chatId),String.valueOf(userId));
        HttpResponse execute = HttpUtil.createGet(url).execute();
        int status = execute.getStatus();
        if(200 != status){
            logger.info("查询Telegram聊天群成员是否存在,查询失败,失败描述:{}",execute.body());
        }else {
            logger.info("查询Telegram聊天群成员是否存在,查询成功... {}",execute.body());
            JSONObject jsonObject = JSON.parseObject(execute.body());
            boolean isOk = (boolean) jsonObject.get("ok");
            if(isOk) {
                JSONObject resultJsonObject = JSONObject.parseObject(JSON.toJSONString(jsonObject.get("result")));
                String memberStatus = (String) resultJsonObject.get("status");
                if("member".equals(memberStatus)){
                    return true;
                }
            }
        }
        return false;
    }
}
