package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.TelegramGroup;

/**
 * TelegramGroupMapper继承基类
 */
public interface TelegramGroupMapper extends MyBatisBaseDao<TelegramGroup, Long> {

    TelegramGroup findTelegramGroupByGroupId(String groupId);
}