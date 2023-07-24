package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.ActivityTopic;

import java.util.List;

/**
 * ActivityTopicMapper继承基类
 */
public interface ActivityTopicMapper extends MyBatisBaseDao<ActivityTopic, Long> {

    ActivityTopic findActivityTopic(String topic);

    List<ActivityTopic> selectActivityTopicExpireList();

}