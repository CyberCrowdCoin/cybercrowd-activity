package org.cybercrowd.activity.biz;

import com.alibaba.fastjson2.JSON;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.cybercrowd.activity.constant.RedisCacheKeyConstants;
import org.cybercrowd.activity.dto.BotReplyContentDto;
import org.cybercrowd.activity.dto.TelegramBotReplyPhotoReq;
import org.cybercrowd.activity.mapper.VoteInteractiveRecordMapper;
import org.cybercrowd.activity.model.CommunityBotInteractive;
import org.cybercrowd.activity.model.VoteInteractiveRecord;
import org.cybercrowd.activity.request.TelegramBotMessageReplyReq;
import org.cybercrowd.activity.request.TelegramBotMessageReq;
import org.cybercrowd.activity.service.DiscordService;
import org.cybercrowd.activity.service.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("voteActivity202202BizService")
public class VoteActivity202202BizService {

    private Logger logger = LoggerFactory.getLogger(VoteActivity202202BizService.class);


    @Autowired
    VoteInteractiveRecordMapper voteInteractiveRecordMapper;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    DiscordService discordService;
    @Autowired
    TelegramService telegramService;

    public void botInteractiveVotingBusinessProcessing(String bot,
                                MessageReceivedEvent discordMessageEvent,
                                TelegramBotMessageReq.MessageBean telegramMessage){

        if(null != telegramMessage && telegramMessage.getFrom().is_bot()){
            //机器人发送的内容不需要处理
            return;
        }
        //检查当前是否存在可用的机器人命令,没有就不处理了
        String cacheValue = redisTemplate.opsForValue()
                .get(RedisCacheKeyConstants.COMMUNITY_BOT_INTERACTIVE_COMMAND_CACHE);
        if(StringUtils.isEmpty(cacheValue)){
            //没有就不处理了
            logger.info("社区机器人互动投票业务,未查询到可用互动命令,不做任何处理...");
            return;
        }

        List<CommunityBotInteractive> botInteractiveCommands =
                JSON.parseArray(cacheValue,CommunityBotInteractive.class);

        String messageText = "";
        String userId = "";
        String userAccountType = "";

        if("telegram".equalsIgnoreCase(bot)){
            messageText = telegramMessage.getText();
            userAccountType = "telegram";
            userId = String.valueOf(telegramMessage.getFrom().getId());
        }else if("discord".equalsIgnoreCase(bot)){
            messageText = discordMessageEvent.getMessage().getContentDisplay();
            userAccountType = "discord";
            userId = discordMessageEvent.getMember().getId();
        }

        //匹配指令,查询回复内容
        BotReplyContentDto botReplyContentDto =
                matchBotCommand(botInteractiveCommands,messageText,userId,userAccountType);
        if(null == botReplyContentDto){
            //没有匹配到内容,什么都不处理
            logger.info("社区机器人互动投票业务,未匹配到互动命令,不做任何处理...");
            return;
        }else {
            logger.info("社区机器人互动投票业务,回复用户内容:{}",JSON.toJSONString(botReplyContentDto));
            if("telegram".equalsIgnoreCase(bot)){
                sendTelegramBotMessage(telegramMessage,botReplyContentDto);
            }else if("discord".equalsIgnoreCase(bot)){
                //发送交互消息
                sendDiscordChannelMessage(discordMessageEvent,botReplyContentDto);
            }
        }
    }

    private void sendTelegramBotMessage(TelegramBotMessageReq.MessageBean telegramMessage, BotReplyContentDto botReplyContentDto) {

        //发送交互消息
        TelegramBotMessageReplyReq telegramBotMessageReplyReq = new TelegramBotMessageReplyReq();
        telegramBotMessageReplyReq.setReply_to_message_id(String.valueOf(telegramMessage.getMessage_id()));
        telegramBotMessageReplyReq.setChat_id(telegramMessage.getChat().getId());
        telegramBotMessageReplyReq.setText(botReplyContentDto.getMessage());
        telegramService.botMessageReply(telegramBotMessageReplyReq);

        Map<String, String> fileIdMap = botReplyContentDto.getFileIdMap();

        if(null != fileIdMap) {
            String fileId = fileIdMap.get(String.valueOf(telegramMessage.getChat().getId()));
            if(!StringUtils.isEmpty(fileId)) {
                //发送图片消息
                TelegramBotReplyPhotoReq telegramBotReplyPhotoReq = new TelegramBotReplyPhotoReq();
//            telegramBotReplyPhotoReq.setReply_to_message_id(String.valueOf(telegramMessage.getMessage_id()));
                telegramBotReplyPhotoReq.setChat_id(telegramMessage.getChat().getId());
                telegramBotReplyPhotoReq.setPhoto(fileId);
                telegramService.botMessagePhotoReply(telegramBotReplyPhotoReq);
            }
        }
    }

    private BotReplyContentDto matchBotCommand(List<CommunityBotInteractive> botInteractiveCommands,
                                   String messageText,String userId,String userAccountType) {
        //检查聊天内容中是否包含 机器人交互指令
        for(CommunityBotInteractive communityBotInteractive:botInteractiveCommands){
            String command = communityBotInteractive.getCommand();
            String interactiveGroupId = communityBotInteractive.getInteractiveGroupId();
            messageText = messageText.toLowerCase();
            if(messageText.indexOf(command.toLowerCase()) > -1){
                //聊天中找到了命令
                if("Q1".equalsIgnoreCase(command)){
                    VoteInteractiveRecord voteInteractiveRecord = voteInteractiveRecordMapper
                            .findCommunityBotInteractiveVoteRecord(
                                    communityBotInteractive.getInteractiveGroupId(), userId, userAccountType);
                    if(null == voteInteractiveRecord) {
                        //提取内容
                        String voteOption = messageText.replace("q1","").trim();
                        //检查投票选项是否正确
                        List<String> voteOptions =
                                JSON.parseArray(communityBotInteractive.getVoteOptions(),String.class);
                        if(voteOptions.contains(voteOption)
                                || voteOptions.contains(voteOption.toUpperCase())){
                            //选项填写正确,保存记录
                            saveVoteInteractiveRecord(communityBotInteractive,userId,userAccountType,voteOption.toUpperCase());
                            logger.info("社区机器人互动投票业务,保存投票记录成功....");
                        }else{
                            //选择填写错误,提示请按正确格式
                            return setBotReplyContent(getBotInteractiveInteractionExceptionContentCache(interactiveGroupId,"option_error"),false);
                        }
                    }else if("1".equals(voteInteractiveRecord.getVoteStatus())){
                        //提示用户您已经完成了活动
                        return setBotReplyContent(getBotInteractiveInteractionExceptionContentCache(interactiveGroupId,"completed_error"),false);
                    }else if("0".equals(voteInteractiveRecord.getVoteStatus())){
                        //已经处理投过票了，返回提示
                        return setBotReplyContent(getBotInteractiveInteractionExceptionContentCache(interactiveGroupId,"voted"),false);
                    }
                }else if("Q2".equalsIgnoreCase(command)){
                    //检查第一步是否完成,没有完成,返回提示先去完成第一步
                    VoteInteractiveRecord voteInteractiveRecord = voteInteractiveRecordMapper
                            .findCommunityBotInteractiveVoteRecord(
                                    communityBotInteractive.getInteractiveGroupId(), userId, userAccountType);
                    if(null == voteInteractiveRecord) {
                        return setBotReplyContent(getBotInteractiveInteractionExceptionContentCache(interactiveGroupId,"step1_error"),false);
                    }else if("1".equals(voteInteractiveRecord.getVoteStatus())){
                        //提示用户您已经完成了活动
                        return setBotReplyContent(getBotInteractiveInteractionExceptionContentCache(interactiveGroupId,"completed_error"),false);
                    }else if("0".equals(voteInteractiveRecord.getVoteStatus())){
                        String content = messageText.replace("q2","").trim();
                        //确定格式
                        long count = content.chars().filter(ch -> ch == '$').count();
                        if(0 == count || count < 2){
                            //检查数据格式是否正确
                            return setBotReplyContent(getBotInteractiveInteractionExceptionContentCache(interactiveGroupId,"format_error"),true);
                        }else{
                            String [] contents = content.split("\\$");
                            if(contents.length > 3){
                                return setBotReplyContent(getBotInteractiveInteractionExceptionContentCache(interactiveGroupId,"format_error"),true);
                            }
                            logger.info("{}",JSON.toJSONString(contents));
                            //检查钱包地址是否正确
                            String walletAddr = contents[2].trim();
                            String message = contents[1];
                            logger.info("社区机器人互动投票业务,行业:{} 钱包地址:{}",message,walletAddr);
                            if(!WalletUtils.isValidAddress(walletAddr)){
                                //地址错误
                                return setBotReplyContent(getBotInteractiveInteractionExceptionContentCache(interactiveGroupId,"wallet_format_error"),false);
                            }else {
                                //更新数据
                                voteInteractiveRecord.setWalletAddress(walletAddr);
                                voteInteractiveRecord.setVoteMessage(message);
                                voteInteractiveRecord.setVoteStatus("1");
                                voteInteractiveRecord.setUpdateTime(new Date());
                                voteInteractiveRecordMapper.updateByPrimaryKey(voteInteractiveRecord);
                                logger.info("社区机器人互动投票业务,更新投票记录钱包信息成功,用户已完成所有任务....");
                            }
                        }
                    }
                }
                return setBotReplyContent(communityBotInteractive.getReplyContent(),false);
            }
        }
        return null;
    }

    private void saveVoteInteractiveRecord(CommunityBotInteractive communityBotInteractive,
                                           String userId, String userAccountType,String voteOption) {
        VoteInteractiveRecord voteInteractiveRecord = new VoteInteractiveRecord();
        voteInteractiveRecord.setInteractiveGroupId(communityBotInteractive.getInteractiveGroupId());
        voteInteractiveRecord.setUserId(userId);
        voteInteractiveRecord.setUserAccountType(userAccountType);
        voteInteractiveRecord.setVoteStatus("0");
        voteInteractiveRecord.setAirdropAmount(new BigDecimal(200));
        voteInteractiveRecord.setVoteOption(voteOption);
        voteInteractiveRecord.setCreateTime(new Date());
        voteInteractiveRecord.setUpdateTime(new Date());
        voteInteractiveRecordMapper.insertSelective(voteInteractiveRecord);
    }

    //发送频道消息
    private void sendDiscordChannelMessage(MessageReceivedEvent messageReceivedEvent,BotReplyContentDto botReplyContentDto){
        MessageChannelUnion channel = messageReceivedEvent.getChannel();
        Message message = messageReceivedEvent.getMessage();
        Member member = message.getMember();

        String user = MessageFormat.format("<@{0}>",member.getId());
        String content = MessageFormat.format("{0}\n{1}",user,botReplyContentDto.getMessage());
        //发送消息
        channel.sendMessage(content).setMessageReference(message.getId()).queue();

        String filePath = botReplyContentDto.getFilePath();
        if(!StringUtils.isEmpty(filePath)) {
            //发送频道图片消息
            channel.sendFiles(FileUpload.fromData(new File(filePath))).queue();
        }
    }

    //发送频道文件
    private void sendDiscordChannelFile(MessageReceivedEvent messageReceivedEvent,String filePath){
        MessageChannelUnion channel = messageReceivedEvent.getChannel();
        Message message = messageReceivedEvent.getMessage();
        channel.sendFiles(FileUpload.fromData(new File(filePath))).queue();
    }

    private String getBotInteractiveInteractionExceptionContentCache(String interactiveGroupId,String errorCode){
        String cacheKey = MessageFormat.format(RedisCacheKeyConstants.COMMUNITY_BOT_INTERACTIVE_EXCEPTION_CACHE,interactiveGroupId,errorCode);
        return redisTemplate.opsForValue().get(cacheKey);
//        String content = "{\"message\":\"Please complete Q1 vote first.\\nQ1:If CyberCrowd listed on the exchange in 2023, which one would you most like?\\nA: Binance  B:Coinbase  C:UniSwap  D:OKX \\nReply (e.g: Q1 A)\",\"fileIdMap\":{\"-1001652515300\":\"AgACAgUAAx0CYn9h5AAD9WPtqLmiob1I2qy-EjlO9_n-QkamAAL-szEbET5xV6DXjfb8sJy3AQADAgADcwADLgQ\",\"-1001763145503\":\"\",\"-1001759257189\":\"AgACAgUAAx0CaRd3HwADQWPsm0CjXfGIoEWiroHykAVdTtvrAAIdtTEbQBtgVxQ_2yHq7PPBAQADAgADeQADLgQ\",\"-1001762924390\":\"\",\"-1001711054305\":\"\"},\"filePath\":\"/data/community_bot_image/discord/IMG_0997.JPG\"}";
//        return content;
    }

    private BotReplyContentDto setBotReplyContent(String content,boolean isJson){
        BotReplyContentDto  botReplyContentDto = null;
        if(!StringUtils.isEmpty(content)) {
            if (isJson) {
                botReplyContentDto = JSON.parseObject(content, BotReplyContentDto.class);
            } else {
                botReplyContentDto = new BotReplyContentDto();
                botReplyContentDto.setMessage(content);
            }
        }
        return botReplyContentDto;
    }
}
