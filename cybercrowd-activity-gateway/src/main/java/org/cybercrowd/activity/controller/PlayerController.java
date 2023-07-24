package org.cybercrowd.activity.controller;

import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.enums.ReturnCodeEnum;
import org.cybercrowd.activity.response.PlayerGameTimeCountdownRes;
import org.cybercrowd.activity.response.PlayerRegisterRes;
import org.cybercrowd.activity.response.PlayerTicketHistoryRes;
import org.cybercrowd.activity.response.PreviousPlayerWinningRes;
import org.cybercrowd.activity.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.WalletUtils;

@RestController
@CrossOrigin("*")
public class PlayerController {

    private Logger logger = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    PlayerService playerService;

    @RequestMapping(value = "/player/v1/register")
    @ResponseBody
    public PlayerRegisterRes registerPlayer(String playerAddress){
        logger.info("注册游戏玩家信息接口,请求入参:{}", playerAddress);
        PlayerRegisterRes playerRegisterRes = new PlayerRegisterRes();
        if(StringUtils.isEmpty(playerAddress) || !WalletUtils.isValidAddress(playerAddress)){
            logger.info("注册游戏玩家信息接口,地址不能为空或者格式错误");
            playerRegisterRes.setReturnCodeEnum(ReturnCodeEnum.PLAYER_ADDRESS_ERROR);
        }
        playerRegisterRes = playerService.registerPlayer(playerAddress);
        return playerRegisterRes;
    }

    @RequestMapping(value = "/player/v1/previous-winnings")
    @ResponseBody
    public PreviousPlayerWinningRes previousPlayerWinningList(){
        logger.info("进入上一轮中奖玩家列表拉取接口....");
        return playerService.previousWinningPlayerList();
    }

    @RequestMapping(value = "/player/v1/ticket-history")
    @ResponseBody
    public PlayerTicketHistoryRes playerTicketHistoryList(String playerAddress){
        return playerService.playerTicketHistoryList(playerAddress);
    }

    @RequestMapping(value = "/player/v1/countdown")
    @ResponseBody
    public PlayerGameTimeCountdownRes gameTimeCountdown(){
        return playerService.playerGameTimeCountdown();
    }
}
