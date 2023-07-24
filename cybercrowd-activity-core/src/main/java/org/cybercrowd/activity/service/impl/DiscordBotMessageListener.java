package org.cybercrowd.activity.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.biz.VoteActivity202202BizService;
import org.cybercrowd.activity.constant.RedisCacheKeyConstants;
import org.cybercrowd.activity.service.activemq.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DiscordBotMessageListener extends ListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(DiscordBotMessageListener.class);

    @Value("${discord.token}")
    private String discordToken;

    @Value("${discord.discord_event_switch}")
    private String discordEventSwitch;

    @Value("${discord.bot_id}")
    private String discordBotId;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProducerService producerService;
    @Autowired
    VoteActivity202202BizService voteActivity202202BizService;

    @Bean
    public void discordEventListener() {

        //spring boot 会把 on 识别成 true
        if("true".equals(discordEventSwitch)){
            logger.info("discord message websocket监听开始启动.....");
            JDABuilder.createDefault(discordToken)
                    .setEventManager(new AnnotatedEventManager())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT) // enables explicit access to message.getContentDisplay()
                    .addEventListeners(this)
                    .build();
            logger.info("discord message websocket监听启动完成.....");
        }
    }

    @SubscribeEvent
    public void subscribeEvent(GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
            MessageType messageType = messageReceivedEvent.getMessage().getType();
            Member member = messageReceivedEvent.getMember();
            if(messageReceivedEvent.isFromGuild()){
                //必须要是频道消息
                Guild guild = messageReceivedEvent.getGuild();
                if (MessageType.GUILD_MEMBER_JOIN == messageType) {
                    logger.info("Discord 消息监听,JOIN 群组:{}  用户:{} 用户ID:{}",
                            guild.getName(), member.getEffectiveName(), member.getId());
//                //2022新年活动，更新用户活动任务状态(用户加入Discord)
//                sendJoinDiscord2022NewYearActivityMessage(member);
                    //活动私信
                    String cacheVaule = redisTemplate.opsForValue()
                            .get(RedisCacheKeyConstants.COMMUNITY_BOT_PRIVATE_MESSAGE_DISCORD_CACHE);
                    if(!StringUtils.isEmpty(cacheVaule)) {
                        sendPrivateMessage(member.getUser(), cacheVaule);
                    }
                } else {
                    logger.info("Discord 消息监听,Message 群组:{} 用户:{} 用户ID:{} 消息内容 :{}",
                            guild.getName(), member.getEffectiveName(), member.getId(),
                            messageReceivedEvent.getMessage().getContentDisplay());

                    if(!StringUtils.isEmpty(messageReceivedEvent.getMessage().getContentDisplay())
                            &&  !discordBotId.equals(member.getId())
                            && !"1001850303009788064".equals(member.getId())) {
                        //机器人互动
                        voteActivity202202BizService.botInteractiveVotingBusinessProcessing("discord", messageReceivedEvent, null);
                    }
                }
                return;
            }
        }
    }

    //发送频道消息
    private void sendChannelMessage(MessageReceivedEvent messageReceivedEvent,String content){
        messageReceivedEvent.getChannel().sendMessage(content);
    }

    //发送私信
    private void sendPrivateMessage(User user,String content){
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .queue();
    }

    private void sendJoinDiscord2022NewYearActivityMessage(Member member) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("discordId", member.getId());
        jsonObject.put("discordName", member.getEffectiveName());
        producerService.joinDiscord2020NewYearActivityMessageQueue(JSON.toJSONString(jsonObject));
    }
}
