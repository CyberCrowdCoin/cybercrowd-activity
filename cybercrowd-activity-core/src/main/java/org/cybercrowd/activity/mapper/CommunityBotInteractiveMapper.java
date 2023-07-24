package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.CommunityBotInteractive;

import java.util.List;

/**
 * CommunityBotInteractiveMapper继承基类
 */
public interface CommunityBotInteractiveMapper extends MyBatisBaseDao<CommunityBotInteractive, Long> {

    List<CommunityBotInteractive> findAvailableVoteInteractiveCommandList();

    List<CommunityBotInteractive> findExpireVoteInteractiveCommandList();
}