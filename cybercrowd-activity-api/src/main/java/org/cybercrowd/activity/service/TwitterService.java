package org.cybercrowd.activity.service;

import org.cybercrowd.activity.dto.TwitterFollowMessageDto;

import java.util.List;
import java.util.Map;

public interface TwitterService {

    /**
     * 拉取Twitter关注用户
     */
    void pullTwitterFollowUser(int fileNumber);

    void pullTwitterFollowUserList(String bearerToken,String paginationToken,Map<String, List<String>> headers, int i);

    /**
     * Twitter关注处理
     * @return
     */
    boolean twitterFollowHandle(TwitterFollowMessageDto twitterFollowMessageDto);
}
