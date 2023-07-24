package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SSEJobStatusDto implements Serializable {

    private boolean twitterJobStatus;

    private boolean discordJobStatus;

    private boolean telegramJobStatus;

    private boolean jobStatus;


}
