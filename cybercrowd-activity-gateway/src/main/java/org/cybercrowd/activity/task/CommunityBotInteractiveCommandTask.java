package org.cybercrowd.activity.task;

import org.cybercrowd.activity.service.CommunityBotInteractiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class CommunityBotInteractiveCommandTask {

    @Autowired
    CommunityBotInteractiveService communityBotInteractiveService;

    @Scheduled(fixedDelay = 60000)
    public void botInteractiveCommandCache(){
        communityBotInteractiveService.communityBotInteractiveCommandCache();
    }

    @Scheduled(fixedDelay = 1200)
    public void updateExpireCommunityBotInteractiveCommand(){
        communityBotInteractiveService.communityBotInteractiveCommandCache();
    }
}
