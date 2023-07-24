package org.cybercrowd.activity.service;

import org.cybercrowd.activity.mapper.TelegramGroupMapper;
import org.cybercrowd.activity.model.TelegramGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramGroupService {

    @Autowired
    TelegramGroupMapper telegramGroupMapper;

    public TelegramGroup findTelegramGroupByGroupId(String groupId){
        return telegramGroupMapper.findTelegramGroupByGroupId(groupId);
    }
}
