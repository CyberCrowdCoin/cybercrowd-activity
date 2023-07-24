package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.UserTopicContent;

/**
 * UserTopicContentMapper继承基类
 */
public interface UserTopicContentMapper extends MyBatisBaseDao<UserTopicContent, Long> {

    UserTopicContent findUserTopicContent(String topic,String groupId, String userId, String source);

}