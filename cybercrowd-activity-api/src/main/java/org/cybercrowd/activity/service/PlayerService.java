package org.cybercrowd.activity.service;

import org.cybercrowd.activity.response.PlayerGameTimeCountdownRes;
import org.cybercrowd.activity.response.PlayerRegisterRes;
import org.cybercrowd.activity.response.PlayerTicketHistoryRes;
import org.cybercrowd.activity.response.PreviousPlayerWinningRes;


public interface PlayerService {

    /**
     *  创建玩家信息,返回邀请链接
     * @param playerAddress
     * @return
     */
    PlayerRegisterRes registerPlayer(String playerAddress);

    /**
     * 上期中奖玩家列表
     * @return
     */
    PreviousPlayerWinningRes previousWinningPlayerList();

    /**
     * 玩家Ticket历史记录
     */
    PlayerTicketHistoryRes playerTicketHistoryList(String playerAddress);

    /**
     * 获取玩家游戏开始倒计时时间
     * @return
     */
    PlayerGameTimeCountdownRes playerGameTimeCountdown();

    /**
     * 开奖
     */
    void drawLottery();

    /**
     * 同步玩家信息
     */
    void syncPlayers();

}
