package org.cybercrowd.activity.response;

import lombok.Data;
import org.cybercrowd.activity.dto.BaseResponse;

import java.io.Serializable;

@Data
public class PlayerRegisterRes extends BaseResponse implements Serializable {

    private String inviteLinkUrl;
}
