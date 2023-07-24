package org.cybercrowd.activity.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * dapps_banner信息
 */
public class DappsBanner implements Serializable {
    private Long id;

    /**
     * banner介绍
     */
    private String bannerIntro;

    /**
     * banner图片地址
     */
    private String bannerImageUrl;

    /**
     * 移动端banner
     */
    private String bannerMobileUrl;

    /**
     * 跳转地址
     */
    private String jumpUrl;

    /**
     * banner 状态 0 已下架 1 已上假
     */
    private String bannerStatus;

    /**
     * banner 过期下架时间
     */
    private Date expireTime;

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

    public String getBannerIntro() {
        return bannerIntro;
    }

    public void setBannerIntro(String bannerIntro) {
        this.bannerIntro = bannerIntro;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getBannerStatus() {
        return bannerStatus;
    }

    public void setBannerStatus(String bannerStatus) {
        this.bannerStatus = bannerStatus;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
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

    public String getBannerMobileUrl() {
        return bannerMobileUrl;
    }

    public void setBannerMobileUrl(String bannerMobileUrl) {
        this.bannerMobileUrl = bannerMobileUrl;
    }
}