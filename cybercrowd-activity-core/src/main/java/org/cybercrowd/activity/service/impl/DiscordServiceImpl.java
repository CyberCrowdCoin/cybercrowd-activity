package org.cybercrowd.activity.service.impl;

import org.cybercrowd.activity.service.DiscordService;
import org.springframework.stereotype.Service;

@Service
public class DiscordServiceImpl implements DiscordService {


    @Override
    public boolean checkDiscordMemberExist(String memberId) {
        return false;
    }
}
