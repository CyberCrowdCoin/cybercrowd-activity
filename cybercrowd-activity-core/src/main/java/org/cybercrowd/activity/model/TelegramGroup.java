package org.cybercrowd.activity.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * TG群信息
 */
public class TelegramGroup implements Serializable {
    private Long id;

    /**
     * 群ID
     */
    private String telegramGroupId;

    /**
     * 群名称
     */
    private String telegramGroupName;

    /**
     * 机器人名称
     */
    private String telegramBotName;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelegramGroupId() {
        return telegramGroupId;
    }

    public void setTelegramGroupId(String telegramGroupId) {
        this.telegramGroupId = telegramGroupId;
    }

    public String getTelegramGroupName() {
        return telegramGroupName;
    }

    public void setTelegramGroupName(String telegramGroupName) {
        this.telegramGroupName = telegramGroupName;
    }

    public String getTelegramBotName() {
        return telegramBotName;
    }

    public void setTelegramBotName(String telegramBotName) {
        this.telegramBotName = telegramBotName;
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