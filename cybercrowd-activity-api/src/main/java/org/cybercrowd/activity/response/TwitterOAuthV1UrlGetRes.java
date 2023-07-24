package org.cybercrowd.activity.response;

import lombok.Data;
import org.cybercrowd.activity.dto.BaseResponse;

import java.io.Serializable;

@Data
public class TwitterOAuthV1UrlGetRes extends BaseResponse implements Serializable {

    private String authorizedUrl;

}
