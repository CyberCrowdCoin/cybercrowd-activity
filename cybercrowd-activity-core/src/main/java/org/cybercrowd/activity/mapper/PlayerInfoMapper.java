package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.dto.TicketHistoryDto;
import org.cybercrowd.activity.model.PlayerInfo;

import java.util.List;

/**
 * PlayerInfoMapper继承基类
 */
public interface PlayerInfoMapper extends MyBatisBaseDao<PlayerInfo, Long> {

    //查询指定回合号下游戏玩家信息
    PlayerInfo selectPlayer(String roundNo,String playerAddress);

    List<PlayerInfo> findPlayerWinningListByRoundNo(String roundNo);

    List<TicketHistoryDto> findPlayerTicketHistoryList(String playerAddress);

    PlayerInfo selectOnePlayer(PlayerInfo playerReq);


}