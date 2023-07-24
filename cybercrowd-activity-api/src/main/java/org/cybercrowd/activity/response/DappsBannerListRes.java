package org.cybercrowd.activity.response;

import lombok.Data;
import org.cybercrowd.activity.dto.BaseResponse;
import org.cybercrowd.activity.dto.DappsBannerDto;

import java.io.Serializable;
import java.util.List;

@Data
public class DappsBannerListRes extends BaseResponse implements Serializable {

    private List<DappsBannerDto> dappsBannerList;
}
