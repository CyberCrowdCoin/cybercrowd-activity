package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.UserActivityJob;

import java.util.List;

/**
 * UserActivityJobMapper继承基类
 */
public interface UserActivityJobMapper extends MyBatisBaseDao<UserActivityJob, Long> {

    UserActivityJob findUserActivityJob(String jobName,String ownerId);

    UserActivityJob findUserActivityJobByInviteCode(String jobName,String inviteCode);

    UserActivityJob findBindDiscordJob(String jobName,String discordId);

    UserActivityJob findBindTwitterJob(String jobName,String twitterId);

    List<UserActivityJob> selectList(UserActivityJob userActivityJob);

}