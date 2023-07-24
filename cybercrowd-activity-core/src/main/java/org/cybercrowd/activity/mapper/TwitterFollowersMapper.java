package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.TwitterFollowers;

/**
 * TwitterFollowersMapper继承基类
 */
public interface TwitterFollowersMapper extends MyBatisBaseDao<TwitterFollowers, Long> {

    TwitterFollowers selectOne(TwitterFollowers twitterFollowersReq);
}