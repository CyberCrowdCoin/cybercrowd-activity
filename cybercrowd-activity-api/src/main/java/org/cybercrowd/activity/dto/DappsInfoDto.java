package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * Dapp信息
 */
@Data
public class DappsInfoDto implements Serializable {

    /**
     * 应用类型
     */
    private String dappType;

    /**
     * 应用名称
     */
    private String dappName;

    /**
     * 应用logo
     */
    private String dappLogo;

    /**
     * 应用背景图片
     */
    private String dappBgImage;

    /**
     * 应用简介
     */
    private String dappIntro;

    /**
     * 应用详细介绍
     */
    private String dappDetailsIntro;

    /**
     * 应用链接
     */
    private String dappLink;

    /**
     * 应用官网
     */
    private String dappOfficialWebsite;

    private String dappTwitter;

    private String dappDiscrod;

    private String dappTelegram;

    /**
     * 应用热度
     */
    private Long dappHot;

    /**
     * 应用热度状态 0 非热门应用 1 热门应用
     */
    private String dappHotStatus;

    /**
     * 应用状态  1 已上线 2 待下线 3 已下线
     */
    private String dappStatus;

    /**
     * dapp下线时间
     */
    private Date dappOfflineTime;

    private String extend;

    private String extend2;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}