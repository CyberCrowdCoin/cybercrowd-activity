package org.cybercrowd.activity.response;

import lombok.Data;
import org.cybercrowd.activity.dto.BaseResponse;

import java.io.Serializable;
import java.util.List;

@Data
public class SyncPlayersRes extends BaseResponse implements Serializable {

    private List<String> inviters;
}
