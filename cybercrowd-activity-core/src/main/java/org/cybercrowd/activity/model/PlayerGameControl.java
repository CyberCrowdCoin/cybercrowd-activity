package org.cybercrowd.activity.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 玩家游戏控制
 */
@Data
public class PlayerGameControl implements Serializable {
    private Long id;

    /**
     * 最新游戏回合号
     */
    private String latestRoundNo;

    /**
     * 游戏上一回合编号
     */
    private String previousRoundNo;

    /**
     * 游戏状态，已开始 0  开奖中 1 已开奖 2
     */
    private String gameStatus;

    /**
     * 下一次游戏开始时间
     */
    private Date nextGameTime;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

}