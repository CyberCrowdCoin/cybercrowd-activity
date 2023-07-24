package org.cybercrowd.activity.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 投票交互记录
 */
public class VoteInteractiveRecord implements Serializable {
    private Long id;

    /**
     * 交互组ID
     */
    private String interactiveGroupId;

    private String userId;

    /**
     * 用户账号类型, telegram twitter discord
     */
    private String userAccountType;

    /**
     * 投票选项
     */
    private String voteOption;

    /**
     * 投票留言
     */
    private String voteMessage;

    /**
     * 投票状态,0 未完成 1 已完成
     */
    private String voteStatus;

    private String extend;

    private String extend2;

    private Date createTime;

    private Date updateTime;

    /**
     * 空投数量
     */
    private BigDecimal airdropAmount;

    /**
     * 钱包地址
     */
    private String walletAddress;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInteractiveGroupId() {
        return interactiveGroupId;
    }

    public void setInteractiveGroupId(String interactiveGroupId) {
        this.interactiveGroupId = interactiveGroupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAccountType() {
        return userAccountType;
    }

    public void setUserAccountType(String userAccountType) {
        this.userAccountType = userAccountType;
    }

    public String getVoteOption() {
        return voteOption;
    }

    public void setVoteOption(String voteOption) {
        this.voteOption = voteOption;
    }

    public String getVoteMessage() {
        return voteMessage;
    }

    public void setVoteMessage(String voteMessage) {
        this.voteMessage = voteMessage;
    }

    public String getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(String voteStatus) {
        this.voteStatus = voteStatus;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
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

    public BigDecimal getAirdropAmount() {
        return airdropAmount;
    }

    public void setAirdropAmount(BigDecimal airdropAmount) {
        this.airdropAmount = airdropAmount;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}