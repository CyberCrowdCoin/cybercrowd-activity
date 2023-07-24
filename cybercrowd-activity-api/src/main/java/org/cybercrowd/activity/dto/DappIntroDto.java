package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DappIntroDto implements Serializable {

    private Long dappId;
    private String dappName;
    private String dappType;
    private String dappLogo;
    private String dappIntro;
    private String dappLink;
    private String dappOfficialWebsite;
    private String dappTwitter;
    private Long dappHot;
    private String dappHotStatus;
    private String dappStatus;
    private String dappOfflineTime;

}
