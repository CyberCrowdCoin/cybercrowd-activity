package org.cybercrowd.activity.service.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.constant.RedisCacheKeyConstants;
import org.cybercrowd.activity.dto.PlayerWinningDto;
import org.cybercrowd.activity.dto.TicketHistoryDto;
import org.cybercrowd.activity.enums.PlayerGameControlStatusEnum;
import org.cybercrowd.activity.enums.PlayerGameStatusEnum;
import org.cybercrowd.activity.enums.PlayerWinningStatusEnum;
import org.cybercrowd.activity.enums.ReturnCodeEnum;
import org.cybercrowd.activity.mapper.GameTimeControlMapper;
import org.cybercrowd.activity.mapper.PlayerGameControlMapper;
import org.cybercrowd.activity.mapper.PlayerInfoMapper;
import org.cybercrowd.activity.model.GameTimeControl;
import org.cybercrowd.activity.model.Player;
import org.cybercrowd.activity.model.PlayerGameControl;
import org.cybercrowd.activity.model.PlayerInfo;
import org.cybercrowd.activity.response.PlayerGameTimeCountdownRes;
import org.cybercrowd.activity.response.PlayerRegisterRes;
import org.cybercrowd.activity.response.PlayerTicketHistoryRes;
import org.cybercrowd.activity.response.PreviousPlayerWinningRes;
import org.cybercrowd.activity.service.ContractPlayerService;
import org.cybercrowd.activity.service.PlayerService;
import org.cybercrowd.activity.utils.InvitationCodeUtil;
import org.cybercrowd.activity.utils.RedissonLockUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    private Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    @Value("${activity.player_invite_link_url}")
    private String playerInviteLinkUrl;

    @Value("${activity.player_contract}")
    private String playerContract;

    @Autowired
    PlayerInfoMapper playerInfoMapper;

    @Autowired
    PlayerGameControlMapper playerGameControlMapper;

    @Autowired
    GameTimeControlMapper gameTimeControlMapper;

    @Autowired
    ContractPlayerService contractPlayerService;

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public PlayerRegisterRes registerPlayer(String playerAddress){
        PlayerRegisterRes playerRegisterRes = new PlayerRegisterRes();
        RedissonLockUtils lock = new RedissonLockUtils(redissonClient,
                MessageFormat.format(RedisCacheKeyConstants.PLAYER_REGISTER_LOCK,playerAddress));
        try {
            if(lock.lock()){
                //当前最新游戏回合号
                PlayerGameControl playerGameControl = latestPlayerGameControl();
                String playerGameStatus = playerGameControl.getGameStatus();

                String roundNo = playerGameControl.getLatestRoundNo();
                logger.info("注册游戏玩家信息接口,当前游戏回合号是:{} 游戏玩家地址:", roundNo, playerAddress);
                //检查用户当前回合用户是否存在
                PlayerInfo playerInfo = playerInfoMapper.selectPlayer(roundNo, playerAddress);
                if (null == playerInfo) {
                    if (PlayerGameControlStatusEnum.GAME_STATUS_1.getCode().equals(playerGameStatus)
                            || PlayerGameControlStatusEnum.GAME_STATUS_2.getCode().equals(playerGameStatus)) {
                        logger.info("注册游戏玩家信息接口,当前游戏回合开奖中或者已结束,暂不允许创建游戏玩家信息...");
                        playerRegisterRes.setReturnCodeEnum(ReturnCodeEnum.PLAYER_REGISTER_ERROR);
                        return playerRegisterRes;
                    }
                    //新增本轮玩家信息
                    playerInfo = new PlayerInfo();
                    playerInfo.setPlayerAddress(playerAddress);
                    playerInfo.setRoundNo(roundNo);
                    playerInfo.setCreateTime(new Date());
                    playerInfo.setUpdateTime(new Date());
                    playerInfo.setWinningStatus(PlayerWinningStatusEnum.WINNING_STATUS_0.getCode());
                    playerInfo.setGameStatus(PlayerGameStatusEnum.GAME_STATUS_0.getCode());
                    //获取邀请码
                    long random = (long) (Math.random() * 10000);
                    playerInfo.setInviteCode(InvitationCodeUtil.generate(System.currentTimeMillis() + random));
                    playerInfoMapper.insertSelective(playerInfo);
                    logger.info("注册游戏玩家信息接口,获取邀请码,ID随机数是:{} 邀请码是:{} ", random, playerInfo.getInviteCode());
                }
                //邀请码
                String inviteCode = playerInfo.getInviteCode();
                playerRegisterRes.setInviteLinkUrl(MessageFormat.format(playerInviteLinkUrl, inviteCode));
                logger.info("注册游戏玩家信息接口,响应结果:{}", JSON.toJSONString(playerRegisterRes));
            }else {
                logger.info("注册游戏玩家信息接口,玩家注册锁获取失败");
                playerRegisterRes.setReturnCodeEnum(ReturnCodeEnum.FAIL);
            }
        }catch (Exception e){
            logger.error("注册游戏玩家信息接口,执行异常:{}",e.getMessage(),e);
            playerRegisterRes.setReturnCodeEnum(ReturnCodeEnum.FAIL);
        }finally {
            lock.unlock();
        }
        return playerRegisterRes;
    }

    @Override
    public PreviousPlayerWinningRes previousWinningPlayerList() {

        PreviousPlayerWinningRes previousPlayerWinningRes = new PreviousPlayerWinningRes();
        String previousRoundNo = playerGameControlMapper.selectPreviousRoundNo();

        if(!StringUtils.isEmpty(previousRoundNo)){

            List<PlayerInfo> playerWinningList =
                    playerInfoMapper.findPlayerWinningListByRoundNo(previousRoundNo);

            if(null != playerWinningList && playerWinningList.size() >0){

                previousPlayerWinningRes.setRoundNo(previousRoundNo);
                List<PlayerWinningDto> playerWinnings = new ArrayList<>();

                for(PlayerInfo playerInfo:playerWinningList){
                    PlayerWinningDto playerWinningDto = new PlayerWinningDto();
                    playerWinningDto.setPlayerAddress(playerInfo.getPlayerAddress());
                    playerWinningDto.setBouns(playerInfo.getBonus());
                    playerWinningDto.setBounsAmount(playerInfo.getBonusAmount());
                    playerWinningDto.setLotteryTime(playerInfo.getLotteryTime());
                    playerWinnings.add(playerWinningDto);
                }
                previousPlayerWinningRes.setPlayerWinnings(playerWinnings);
            }
            logger.info("上一轮中奖玩家列表拉取接口,响应结果:{}",JSON.toJSONString(previousPlayerWinningRes));
        }else {
            logger.info("上一轮中奖玩家列表拉取接口,当前轮次是第一轮,没有上轮中奖用户数据...");
        }
        return previousPlayerWinningRes;
    }

    @Override
    public PlayerTicketHistoryRes playerTicketHistoryList(String playerAddress) {
        PlayerTicketHistoryRes playerTicketHistoryRes = new PlayerTicketHistoryRes();

        TicketHistoryDto contractTicketHistory = new TicketHistoryDto();
        contractTicketHistory.setGameStatus("1");
        contractTicketHistory.setSpendAmount(BigDecimal.ZERO);
        //取合约数据  最新轮次未开奖用户数据
        ArrayList<Player> players = contractPlayerService.getPlayers(playerContract);
        if (null == players || players.size() == 0) {
            logger.info("同步游戏玩家数据接口,拉取合约中玩家数据失败或者没有数据");
            contractTicketHistory = null;
        }else{
            //找到属于指定玩家地址的数据
            for(Player player:players){
                if(player.playerAddress.getValue().equalsIgnoreCase(playerAddress)){
                    contractTicketHistory.setRound(player.round.getValue().toString());
                    contractTicketHistory.setBuyNumber(contractTicketHistory.getBuyNumber() + 1);
                    BigDecimal spendAmount = new BigDecimal(player.amount.getValue()).divide(
                            new BigDecimal(1000000000000000000l)).setScale(6, BigDecimal.ROUND_HALF_UP);
                    contractTicketHistory.setSpendAmount(contractTicketHistory.getSpendAmount().add(spendAmount));
                    contractTicketHistory.setBonusAmount(BigDecimal.ZERO);
                }
            }
            if(contractTicketHistory.getBuyNumber() == 0){
                contractTicketHistory = null;
            }
        }

        List<TicketHistoryDto> playerTicketHistoryList = playerInfoMapper.findPlayerTicketHistoryList(playerAddress);

        if(null != playerTicketHistoryList && playerTicketHistoryList.size() >0){
            for(TicketHistoryDto ticketHistoryDto :playerTicketHistoryList){

                BigDecimal bonusAmount = ticketHistoryDto.getBonusAmount();
                if(bonusAmount.compareTo(BigDecimal.ZERO) == 0){
                    ticketHistoryDto.setBonusAmount(BigDecimal.ZERO);
                }
                if(ticketHistoryDto.getSpendAmount().compareTo(BigDecimal.ZERO) > 0){
                    ticketHistoryDto.setSpendAmount(ticketHistoryDto.
                            getSpendAmount().divide(new BigDecimal(1000000000000000000l),6,BigDecimal.ROUND_HALF_DOWN));
                }
                //如果数据库中的数据与合约里的数据存在重复的，就不需要合约数据了
                if(null != contractTicketHistory
                        && contractTicketHistory.getRound().equals(ticketHistoryDto.getRound())){
                    contractTicketHistory = null;
                }
            }
            if(null != contractTicketHistory){
                playerTicketHistoryList.add(0,contractTicketHistory);
            }
        }else if(null != contractTicketHistory){
            playerTicketHistoryList = new ArrayList<>();
            playerTicketHistoryList.add(contractTicketHistory);
        }else {
        }
        playerTicketHistoryRes.setTicketHistorys(playerTicketHistoryList);
        logger.info("玩家Ticket历史数据查询接口,响应结果:{}",JSON.toJSONString(playerTicketHistoryRes));
        return playerTicketHistoryRes;
    }

    @Override
    public PlayerGameTimeCountdownRes playerGameTimeCountdown() {
        PlayerGameTimeCountdownRes countdownRes = new PlayerGameTimeCountdownRes();
        //查询最新游戏时间控制数据
        GameTimeControl gameTimeControl = gameTimeControlMapper.latestGameTimeControl();
        if(null == gameTimeControl){
            logger.info("游戏开始倒计时查询,当前没有初始化游戏开始时间数据...");
            countdownRes.setReturnCodeEnum(ReturnCodeEnum.FAIL);
            return countdownRes;
        }

        Date gameStartTime = gameTimeControl.getGameStartTime();
        countdownRes.setGameStartTime(gameStartTime);
        LocalDateTime localDateTimeStart = gameStartTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        countdownRes.setNow(Date.from(localDateTimeNow.atZone(ZoneId.systemDefault()).toInstant()));
        countdownRes.setTotalTime(gameTimeControl.getTimeInterval());
        //根据当前时间计算倒计时
        if(localDateTimeNow.compareTo(localDateTimeStart) >= 0){
            //倒计时已结束
            countdownRes.setCountdown(0l);
        }else {
            long endMilli = localDateTimeStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long nowMilli = localDateTimeNow.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            countdownRes.setCountdown(endMilli - nowMilli);
        }
        logger.info("游戏开始倒计时查询,响应结果:{}",JSON.toJSONString(countdownRes));
        return countdownRes;
    }

    @Override
    public void drawLottery() {
        //查询开奖时间缓存
        long openTime = 0;
        String timeCache = redisTemplate.opsForValue().get(RedisCacheKeyConstants.PLAYER_DRAW_LOTTERY_TIME_CACHE);
        if(StringUtils.isEmpty(timeCache)){
            //当前没有缓存开奖时间,查询最新开奖时间数据,加上缓存
            GameTimeControl gameTimeControl = gameTimeControlMapper.latestGameTimeControl();
            openTime = gameTimeControl.getGameStartTime().getTime();
            redisTemplate.opsForValue().set(
                    RedisCacheKeyConstants.PLAYER_DRAW_LOTTERY_TIME_CACHE,
                    String.valueOf(openTime));
        }else{
            openTime = Long.valueOf(timeCache);
        }

        //当前时间
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        long nowTime = nowLocalDateTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();

        //检查是否符合开奖时间条件
        if(openTime <= nowTime){
            //检查当前是不是有参与游戏的用户
            ArrayList<Player> players = contractPlayerService.getPlayers(playerContract);
            if (null == players) {
                //返回异常
                logger.info("彩票开奖业务,拉取合约中玩家数据异常");
                return;
            }else if(players.size() == 0) {
                //没有游戏参与者也需要重置游戏控制时间
                //保存最新游戏开始时间控制信息
                insertLatestGameTimeControl();
            }else{
                //调用合约开奖
                String result = contractPlayerService.drawLottery(playerContract);
                if("OK".equals(result)){
                    //保存最新游戏开始时间控制信息
                    insertLatestGameTimeControl();
                    //添加玩家同步数据标签缓存,标识需要从合约中拉取玩家数据
                    logger.info("彩票开奖业务,缓存待同步游戏玩家信息标签缓存:{}",players.get(0).round.getValue().toString());
                    redisTemplate.opsForValue().set(
                            RedisCacheKeyConstants.PLAYER_SYNC_TAG_CACHE,players.get(0).round.getValue().toString());
                }

            }
        }

    }

    @Override
    public void syncPlayers() {
        try {
            //检查同步标记缓存,是否需要执行
            String tagCache = redisTemplate.opsForValue().get(RedisCacheKeyConstants.PLAYER_SYNC_TAG_CACHE);
            if (!StringUtils.isEmpty(tagCache)) {
                //拉取上一轮参与用户信息
                ArrayList<Player> players = contractPlayerService.getLastRoundPlayers(playerContract);
                if (null == players || players.size() == 0) {
                    //返回异常
                    logger.info("彩票玩家信息同步业务,拉取合约中玩家数据异常");
                    return;
                } else {
                    long rundNo = players.get(0).round.getValue().longValue();
                    long rundNoCache = Long.valueOf(tagCache);
                    if (rundNo != rundNoCache){
                        logger.info("彩票玩家信息同步业务,缓存RundNo:{} 合约RundNo:{} 不一致",rundNoCache, rundNo);
                        return; //同步标记缓存中的轮次号与当前拉取合约数据中的轮次号不一致,不是我们需要的数据
                    }
                    logger.info("彩票玩家信息同步业务,RundNo:{} 待处理玩家数据:{} ", rundNo, players.size());
                    for (Player player : players) {
                        String playerAddress = player.playerAddress.getValue();
                        long playerIndex = player.roundIndex.getValue().longValue();
                        Boolean isWinner = player.isWinner.getValue();
                        String roundNo = player.round.getValue().toString();

                        PlayerInfo playerReq = new PlayerInfo();
                        playerReq.setRoundNo(roundNo);
                        playerReq.setPlayerIndex(playerIndex);
                        playerReq.setPlayerAddress(playerAddress);
                        PlayerInfo playerInfo = playerInfoMapper.selectOnePlayer(playerReq);

                        if (null == playerInfo) {
                            playerInfo = createNewPlayerInfo(player, roundNo);
                            if (isWinner) {
                                //中奖用户
                                Long boun = player.bonus.getValue().longValue();
                                BigDecimal bounBigDecimal = new BigDecimal(boun).divide(
                                        new BigDecimal(1000000000000000000l)).setScale(8, BigDecimal.ROUND_HALF_UP);
                                playerInfo.setBonusAmount(boun);
                                playerInfo.setBonus(bounBigDecimal);
                                playerInfo.setWinningStatus(PlayerWinningStatusEnum.WINNING_STATUS_1.getCode());
                                playerInfo.setLotteryTime(new Date());
                            }
                            playerInfo.setGameStatus(PlayerGameStatusEnum.GAME_STATUS_2.getCode());
                            playerInfoMapper.insertSelective(playerInfo);
                        }
                    }
                    //删除标记
                    redisTemplate.delete(RedisCacheKeyConstants.PLAYER_SYNC_TAG_CACHE);
                }
            } else {
                logger.info("彩票玩家信息同步业务,暂不需要处理...");
            }
        }catch (Exception e){
            logger.error("彩票玩家信息同步业务,执行异常:{}",e.getMessage(),e);
        }
    }

    private PlayerInfo createNewPlayerInfo(Player player,String latestRoundNo){
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setPlayerIndex(player.roundIndex.getValue().longValue());
        playerInfo.setPlayerAddress(player.playerAddress.getValue());
        playerInfo.setInvitePlayer(null == player.inviterAddress ? null:player.inviterAddress.getValue());
        playerInfo.setPayAmount(player.amount.getValue().longValue());
        playerInfo.setRoundNo(latestRoundNo);
        playerInfo.setUpdateTime(new Date());
        playerInfo.setCreateTime(new Date(player.timestamp.getValue().longValue()*1000));
        playerInfo.setWinningStatus(PlayerWinningStatusEnum.WINNING_STATUS_0.getCode());
        playerInfo.setGameStatus(PlayerGameStatusEnum.GAME_STATUS_1.getCode());
        return playerInfo;
    }

    private void insertLatestGameTimeControl() {
        GameTimeControl gameTimeControl = gameTimeControlMapper.latestGameTimeControl();
        if(null != gameTimeControl){
            Date gameStartTime = gameTimeControl.getGameStartTime();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(gameStartTime.toInstant(), ZoneId.systemDefault());
            LocalDateTime newLocalDateTime = localDateTime.plusHours(gameTimeControl.getTimeInterval());
            Date newGameStartTime = Date.from(newLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            gameTimeControl.setGameStartTime(newGameStartTime);
            gameTimeControl.setCreateTime(new Date());
            gameTimeControl.setUpdateTime(new Date());
            gameTimeControl.setId(null);
            gameTimeControlMapper.insertSelective(gameTimeControl);
            //添加开奖时间缓存
            redisTemplate.opsForValue().set(
                    RedisCacheKeyConstants.PLAYER_DRAW_LOTTERY_TIME_CACHE, String.valueOf(newGameStartTime.getTime()));
        }
    }

    private PlayerGameControl latestPlayerGameControl() {
        //最新玩家游戏控制信息
        PlayerGameControl playerGameControl = playerGameControlMapper.selectLatestPlayerGameControl();
        return playerGameControl;
    }
}
