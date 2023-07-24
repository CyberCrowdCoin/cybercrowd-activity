package org.cybercrowd.activity.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 游戏者信息
 */
public class PlayerInfo implements Serializable {
    private Long id;

    /**
     * 游戏回合编号, 0000001
     */
    private String roundNo;

    /**
     * 玩家索引
     */
    private Long playerIndex;

    /**
     * 游戏者地址
     */
    private String playerAddress;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 被邀请码,绑定邀请关系
     */
    private String inviteCodeBy;

    /**
     * 邀请玩家
     */
    private String invitePlayer;

    /**
     * 付款金额
     */
    private Long payAmount;

    /**
     * 奖金金额
     */
    private Long bonusAmount;

    /**
     * 奖金
     */
    private BigDecimal bonus;

    /**
     * 获奖状态, 0 未中奖 1 中奖
     */
    private String winningStatus;

    /**
     * 游戏状态, 0 未参与 1 参与中 2 已结束
     */
    private String gameStatus;

    /**
     * 开奖时间
     */
    private Date lotteryTime;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(String roundNo) {
        this.roundNo = roundNo;
    }

    public String getPlayerAddress() {
        return playerAddress;
    }

    public void setPlayerAddress(String playerAddress) {
        this.playerAddress = playerAddress;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getInviteCodeBy() {
        return inviteCodeBy;
    }

    public void setInviteCodeBy(String inviteCodeBy) {
        this.inviteCodeBy = inviteCodeBy;
    }

    public Long getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(Long bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public String getWinningStatus() {
        return winningStatus;
    }

    public void setWinningStatus(String winningStatus) {
        this.winningStatus = winningStatus;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Date getLotteryTime() {
        return lotteryTime;
    }

    public void setLotteryTime(Date lotteryTime) {
        this.lotteryTime = lotteryTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(Long playerIndex) {
        this.playerIndex = playerIndex;
    }

    public String getInvitePlayer() {
        return invitePlayer;
    }

    public void setInvitePlayer(String invitePlayer) {
        this.invitePlayer = invitePlayer;
    }

    public Long getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Long payAmount) {
        this.payAmount = payAmount;
    }
}