package org.cybercrowd.activity.task;

import org.cybercrowd.activity.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlayerTask {

    @Autowired
    PlayerService playerService;

    /**
     * 定时开奖
     */
    @Scheduled(fixedDelay = 5000)
    public void drawLotteryTask(){
        playerService.drawLottery();
    }

    /**
     * 同步玩家信息
     */
    @Scheduled(fixedDelay = 30000)
    public void syncPlayerTask(){
        playerService.syncPlayers();
    }

}
