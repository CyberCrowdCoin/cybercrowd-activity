package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.VoteInteractiveRecord;

import java.math.BigDecimal;

/**
 * VoteInteractiveRecordMapper继承基类
 */
public interface VoteInteractiveRecordMapper extends MyBatisBaseDao<VoteInteractiveRecord, Long> {

    VoteInteractiveRecord findCommunityBotInteractiveVoteRecord(String interactiveGroupId,
                                                                String userId, String accountType);

    BigDecimal countTicketQuantity(String walletAddr);
}