package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DappsBannerDto implements Serializable {

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
}
