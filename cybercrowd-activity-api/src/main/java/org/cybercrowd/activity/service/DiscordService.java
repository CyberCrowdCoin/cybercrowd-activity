package org.cybercrowd.activity.service;

public interface DiscordService {

    /**
     * 检查Disacord 成员是否存在
     * @param memberId
     * @return
     */
    boolean checkDiscordMemberExist(String memberId);
}
