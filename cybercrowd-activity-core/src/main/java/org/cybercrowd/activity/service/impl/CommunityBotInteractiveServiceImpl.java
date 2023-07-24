package org.cybercrowd.activity.service.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.constant.RedisCacheKeyConstants;
import org.cybercrowd.activity.mapper.CommunityBotInteractiveMapper;
import org.cybercrowd.activity.model.CommunityBotInteractive;
import org.cybercrowd.activity.service.CommunityBotInteractiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service("communityBotInteractiveService")
public class CommunityBotInteractiveServiceImpl implements CommunityBotInteractiveService {

    private Logger logger = LoggerFactory.getLogger(CommunityBotInteractiveServiceImpl.class);

    @Autowired
    CommunityBotInteractiveMapper communityBotInteractiveMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void communityBotInteractiveCommandCache() {
        logger.info("社群机器人可用交互命令缓存任务,开始....");
        List<CommunityBotInteractive> availableVoteInteractiveCommandList =
                communityBotInteractiveMapper.findAvailableVoteInteractiveCommandList();

        if(null != availableVoteInteractiveCommandList
                && availableVoteInteractiveCommandList.size() > 0){
            logger.info("社群机器人可用交互命令缓存任务,待缓存数据量:{}",availableVoteInteractiveCommandList.size());

            //保存到redis
            redisTemplate.opsForValue()
                    .set(RedisCacheKeyConstants.COMMUNITY_BOT_INTERACTIVE_COMMAND_CACHE,
                            JSON.toJSONString(availableVoteInteractiveCommandList));
//            for(CommunityBotInteractive communityBotInteractive:availableVoteInteractiveCommandList){
//                String cacheKey = MessageFormat.format(
//                        RedisCacheKeyConstants.COMMUNITY_BOT_INTERACTIVE_COMMAND_CACHE,
//                        communityBotInteractive.getCommand());
//                //保存到redis
//                redisTemplate.opsForValue()
//                        .set(cacheKey, JSON.toJSONString(communityBotInteractive));
//            }
        }
    }

    @Override
    public void updateCommunityBotInteractiveCommandCache() {
        logger.info("社群机器人过期交互命令状态更新任务,开始....");
        List<CommunityBotInteractive> expireVoteInteractiveCommandList =
                communityBotInteractiveMapper.findExpireVoteInteractiveCommandList();

        if(null != expireVoteInteractiveCommandList
                && expireVoteInteractiveCommandList.size() > 0){
            logger.info("社群机器人过期交互命令状态更新任务,待更新数据量:{}",expireVoteInteractiveCommandList.size());
            for(CommunityBotInteractive communityBotInteractive:expireVoteInteractiveCommandList){
                //更新状态
                communityBotInteractive.setStatus("0");
                communityBotInteractive.setUpdateTime(new Date());
                communityBotInteractiveMapper.updateByPrimaryKeySelective(communityBotInteractive);
            }
        }
    }
}
