package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.PlayerGameControl;
import org.springframework.stereotype.Repository;

/**
 * PlayerGameControlMapper继承基类
 */
public interface PlayerGameControlMapper extends MyBatisBaseDao<PlayerGameControl, Long> {

    PlayerGameControl selectLatestPlayerGameControl();

    String selectPreviousRoundNo();

}