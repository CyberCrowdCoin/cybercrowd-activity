package org.cybercrowd.activity.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 
 */
public class CommunityBotInteractive implements Serializable {
    private Long id;

    /**
     * 交互组ID
     */
    private String interactiveGroupId;

    /**
     * 交互命令
     */
    private String command;

    /**
     * 交互标签,关键字
     */
    private String interactiveTag;

    /**
     * 交互类型,VOTE 投票 QA 问答
     */
    private String interactiveType;

    /**
     * 上一个交互命令
     */
    private String previousCommand;

    /**
     * 交互回复内容
     */
    private String replyContent;

    /**
     * 状态, 1 可用 0 不可用
     */
    private String status;

    /**
     * 交互到期时间
     */
    private Date interactiveExpire;

    /**
     * 投票选项
     */
    private String voteOptions;

    /**
     * 服务类
     */
    private String serviceClass;

    private String extend;

    private String extend2;

    private Date createTime;

    private Date updateTime;

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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getInteractiveTag() {
        return interactiveTag;
    }

    public void setInteractiveTag(String interactiveTag) {
        this.interactiveTag = interactiveTag;
    }

    public String getInteractiveType() {
        return interactiveType;
    }

    public void setInteractiveType(String interactiveType) {
        this.interactiveType = interactiveType;
    }

    public String getPreviousCommand() {
        return previousCommand;
    }

    public void setPreviousCommand(String previousCommand) {
        this.previousCommand = previousCommand;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getInteractiveExpire() {
        return interactiveExpire;
    }

    public void setInteractiveExpire(Date interactiveExpire) {
        this.interactiveExpire = interactiveExpire;
    }

    public String getVoteOptions() {
        return voteOptions;
    }

    public void setVoteOptions(String voteOptions) {
        this.voteOptions = voteOptions;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
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
}