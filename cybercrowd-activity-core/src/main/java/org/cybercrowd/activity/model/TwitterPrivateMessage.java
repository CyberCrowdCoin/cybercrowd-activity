package org.cybercrowd.activity.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 
 */
public class TwitterPrivateMessage implements Serializable {
    private Long id;

    /**
     * twitter用户ID
     */
    private String twitterUserId;

    /**
     * twitter用户名称
     */
    private String twitterUserName;

    /**
     * 消息主题
     */
    private String messageTopic;

    /**
     * 消息内容
     */
    private String mesage;

    /**
     * 发送状态 0 未发送 1 已发送
     */
    private String sendStatus;

    /**
     * 错误信息
     */
    private String error;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(String twitterUserId) {
        this.twitterUserId = twitterUserId;
    }

    public String getTwitterUserName() {
        return twitterUserName;
    }

    public void setTwitterUserName(String twitterUserName) {
        this.twitterUserName = twitterUserName;
    }

    public String getMessageTopic() {
        return messageTopic;
    }

    public void setMessageTopic(String messageTopic) {
        this.messageTopic = messageTopic;
    }

    public String getMesage() {
        return mesage;
    }

    public void setMesage(String mesage) {
        this.mesage = mesage;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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