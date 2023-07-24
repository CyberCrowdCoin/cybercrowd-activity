package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TicketHistoryDto implements Serializable {

    //游戏轮数
    private String round;
    //购买次数
    private int buyNumber;
    //花费金额
    private BigDecimal spendAmount;
    //奖金金额
    private BigDecimal bonusAmount;
    //游戏状态 1未开奖 2已开奖
    private String gameStatus;

}
