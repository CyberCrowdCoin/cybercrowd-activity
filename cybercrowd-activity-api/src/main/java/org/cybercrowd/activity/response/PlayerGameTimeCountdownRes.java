package org.cybercrowd.activity.response;

import lombok.Data;
import org.cybercrowd.activity.dto.BaseResponse;

import java.io.Serializable;
import java.util.Date;

@Data
public class PlayerGameTimeCountdownRes extends BaseResponse implements Serializable {

    private Long totalTime; //小时
    private Long countdown;
    private Date gameStartTime;
    private Date now;

}
