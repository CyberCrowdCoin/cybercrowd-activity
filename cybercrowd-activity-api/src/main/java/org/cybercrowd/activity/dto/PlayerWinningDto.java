package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PlayerWinningDto implements Serializable {

    private String playerAddress;

    private BigDecimal bouns;

    private Long bounsAmount;

    private Date lotteryTime;
}
