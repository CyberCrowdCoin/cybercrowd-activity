package org.cybercrowd.activity.controller;

import com.alibaba.fastjson2.JSON;
import org.cybercrowd.activity.biz.VoteActivity202202BizService;
import org.cybercrowd.activity.constant.RedisCacheKeyConstants;
import org.cybercrowd.activity.request.TelegramBotMessageReplyReq;
import org.cybercrowd.activity.request.TelegramBotMessageReq;
import org.cybercrowd.activity.response.TelegramBotMessageRes;
import org.cybercrowd.activity.service.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TelegramController {

    private Logger logger = LoggerFactory.getLogger(TelegramController.class);

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    TelegramService telegramService;
    @Autowired
    VoteActivity202202BizService voteActivity202202BizService;

    @RequestMapping(value = "/telegram/bot-webhook")
    @ResponseBody
    public TelegramBotMessageRes botWebHook(@RequestBody TelegramBotMessageReq telegramBotMessageReq){
        logger.info("telegram bot webhook 消息接收接口,请求入参:{}", JSON.toJSONString(telegramBotMessageReq));

//        return telegramService.botWebHook(telegramBotMessageReq);
        TelegramBotMessageReq.MessageBean message = telegramBotMessageReq.getMessage();
        if (null != message) {
            String chatId = String.valueOf(message.getChat().getId());
            String messageText = message.getText();
            String cacheValue = redisTemplate.opsForValue()
                    .get(RedisCacheKeyConstants.COMMUNITY_BOT_PRIVATE_MESSAGE_TELEGRAM_CACHE);

            String groupListValue = redisTemplate.opsForValue().get(RedisCacheKeyConstants.TELEGRAM_GROUP_ID_LIST_CACHE);
            List<String> telegramGroupIdList = JSON.parseArray(groupListValue,String.class);
            if (!StringUtils.hasText(messageText)) {
                //没有消息内容，直接返回吧

                //活动私信
                //不是机器人发送的消息
                //消息必须是指定群组的
                //当前有可用的交互命令缓存
                //当前消息发送者必须在群组中
                //满足以上条件可以发送私信
                if(!message.getFrom().is_bot()
                        //TODO
//                        && telegramGroupIdList.contains(chatId)
                        && telegramService.getTelegramChatMember(
                                message.getChat().getId(),message.getFrom().getId())) {
                    TelegramBotMessageReplyReq telegramBotMessageReplyReq = new TelegramBotMessageReplyReq();
                    telegramBotMessageReplyReq.setChat_id(message.getFrom().getId());
                    telegramBotMessageReplyReq.setText(cacheValue);
                    //5565050382
                    telegramService.botPrivateMessage(telegramBotMessageReplyReq);
                }
            } else if(!message.getFrom().is_bot()
                    && !"5438314723".equals(String.valueOf(message.getFrom().getId()))
                    //TODO
//                    && telegramGroupIdList.contains(chatId)
                    && StringUtils.hasText(cacheValue)){
                //不是机器人发送的消息
                //消息必须是指定群组的
                //当前有可用的交互命令缓存
                //检查消息内容，开始匹配交互命令
                try {
                    //机器人交互
                    voteActivity202202BizService.botInteractiveVotingBusinessProcessing("telegram", null, message);
                }catch (Exception e){
                    logger.error("telegram bot webhook 消息接收接口, 处理异常:{}",e.getMessage(),e);
                }
            }
        }
        return new TelegramBotMessageRes();
    }
}
