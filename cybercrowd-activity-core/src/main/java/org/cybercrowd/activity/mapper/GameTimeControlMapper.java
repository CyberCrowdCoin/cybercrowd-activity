package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.GameTimeControl;
import org.springframework.stereotype.Repository;

/**
 * GameTimeControlMapper继承基类
 */
@Repository
public interface GameTimeControlMapper extends MyBatisBaseDao<GameTimeControl, Long> {

    GameTimeControl latestGameTimeControl();
}