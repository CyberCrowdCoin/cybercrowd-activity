package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.TwitterPrivateMessage;

import java.util.List;

/**
 * TwitterPrivateMessageMapper继承基类
 */
public interface TwitterPrivateMessageMapper extends MyBatisBaseDao<TwitterPrivateMessage, Long> {

    List<TwitterPrivateMessage> findNotSentPrivateMessageList(String topic);
}