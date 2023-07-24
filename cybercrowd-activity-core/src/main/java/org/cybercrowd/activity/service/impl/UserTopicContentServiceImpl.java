package org.cybercrowd.activity.service.impl;

import org.cybercrowd.activity.dto.TelegramBotMessageDto;
import org.cybercrowd.activity.mapper.UserTopicContentMapper;
import org.cybercrowd.activity.model.UserTopicContent;
import org.cybercrowd.activity.service.UserTopicContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("userTopicContentService")
public class UserTopicContentServiceImpl implements UserTopicContentService {

    @Autowired
    UserTopicContentMapper userTopicContentMapper;

    @Override
    public void telegramBotMessageHandle(TelegramBotMessageDto telegramBotMessageDto) {
        String userId = telegramBotMessageDto.getUserId();
        String userName = telegramBotMessageDto.getUserName();
        String topic = telegramBotMessageDto.getTopic();
        String content = telegramBotMessageDto.getContent();
        String groupId = telegramBotMessageDto.getGroupId();
        UserTopicContent userTopicContent = userTopicContentMapper.findUserTopicContent(topic,groupId,userId,"telegram");
        if(null != userTopicContent){
            //更新
            userTopicContent.setContent(content.trim());
            userTopicContent.setUserName(userName);
            userTopicContent.setUpdateTime(new Date());
            userTopicContentMapper.updateByPrimaryKeySelective(userTopicContent);
        }else {
            //保存
            userTopicContent = new UserTopicContent();
            userTopicContent.setSource("telegram");
            userTopicContent.setUserName(userName);
            userTopicContent.setUserId(userId);
            userTopicContent.setContent(content.trim());
            userTopicContent.setTopic(topic);
            userTopicContent.setGroupId(groupId);
            userTopicContent.setCreateTime(new Date());
            userTopicContent.setUpdateTime(new Date());
            userTopicContentMapper.insertSelective(userTopicContent);
        }
    }
}
