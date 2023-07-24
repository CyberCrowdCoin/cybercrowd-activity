package org.cybercrowd.activity.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 用户活动任务
 */
public class UserActivityJob implements Serializable {
    private Long id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 所有者账户类型,twitter  discrod telegram
     */
    private String ownerAccountType;

    /**
     * 所有者账户ID类型
     */
    private String ownerId;

    /**
     * 用户提交内容
     */
    private String content;

    /**
     * twitter账户ID
     */
    private String twitterId;

    /**
     * twitter个人信息
     */
    private String twitterInfo;

    /**
     * twitter任务完成状态 0 未完成 1 已完成
     */
    private String twitterJobStatus;

    /**
     * discord账户ID
     */
    private String discordId;

    /**
     * discord个人信息
     */
    private String discordInfo;

    /**
     * discord任务完成状态 0 未完成 1 已完成
     */
    private String discordJobStatus;

    /**
     * telegram账户ID
     */
    private String telegramId;

    /**
     * telegram个人信息
     */
    private String telegramInfo;

    /**
     * telegram任务完成状态 0 未完成 1 已完成
     */
    private String telegramJobStatus;

    /**
     * 任务完成状态 0 未完成 1 已完成
     */
    private String jobStatus;

    /**
     * 任务完成时间
     */
    private Date jobSuccessTime;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 父邀请码
     */
    private String parentInviteCode;

    /**
     * 邀请用户数量
     */
    private Long inviteUsersNumber;

    /**
     * 登录token
     */
    private String loginToken;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getOwnerAccountType() {
        return ownerAccountType;
    }

    public void setOwnerAccountType(String ownerAccountType) {
        this.ownerAccountType = ownerAccountType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public String getTwitterInfo() {
        return twitterInfo;
    }

    public void setTwitterInfo(String twitterInfo) {
        this.twitterInfo = twitterInfo;
    }

    public String getTwitterJobStatus() {
        return twitterJobStatus;
    }

    public void setTwitterJobStatus(String twitterJobStatus) {
        this.twitterJobStatus = twitterJobStatus;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getDiscordInfo() {
        return discordInfo;
    }

    public void setDiscordInfo(String discordInfo) {
        this.discordInfo = discordInfo;
    }

    public String getDiscordJobStatus() {
        return discordJobStatus;
    }

    public void setDiscordJobStatus(String discordJobStatus) {
        this.discordJobStatus = discordJobStatus;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public String getTelegramInfo() {
        return telegramInfo;
    }

    public void setTelegramInfo(String telegramInfo) {
        this.telegramInfo = telegramInfo;
    }

    public String getTelegramJobStatus() {
        return telegramJobStatus;
    }

    public void setTelegramJobStatus(String telegramJobStatus) {
        this.telegramJobStatus = telegramJobStatus;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Date getJobSuccessTime() {
        return jobSuccessTime;
    }

    public void setJobSuccessTime(Date jobSuccessTime) {
        this.jobSuccessTime = jobSuccessTime;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getParentInviteCode() {
        return parentInviteCode;
    }

    public void setParentInviteCode(String parentInviteCode) {
        this.parentInviteCode = parentInviteCode;
    }

    public Long getInviteUsersNumber() {
        return inviteUsersNumber;
    }

    public void setInviteUsersNumber(Long inviteUsersNumber) {
        this.inviteUsersNumber = inviteUsersNumber;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
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
}