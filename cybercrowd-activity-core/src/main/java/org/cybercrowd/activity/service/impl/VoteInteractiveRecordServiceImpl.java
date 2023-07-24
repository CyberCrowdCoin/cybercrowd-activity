package org.cybercrowd.activity.service.impl;

import org.cybercrowd.activity.mapper.VoteInteractiveRecordMapper;
import org.cybercrowd.activity.service.VoteInteractiveRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("voteInteractiveRecordService")
public class VoteInteractiveRecordServiceImpl implements VoteInteractiveRecordService {

    @Autowired
    VoteInteractiveRecordMapper voteInteractiveRecordMapper;

    @Override
    public BigDecimal ticketQuery(String walletAddr) {
        return voteInteractiveRecordMapper.countTicketQuantity(walletAddr);
    }
}
