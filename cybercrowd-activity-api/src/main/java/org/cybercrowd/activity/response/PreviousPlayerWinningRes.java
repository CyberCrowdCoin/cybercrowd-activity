package org.cybercrowd.activity.response;

import lombok.Data;
import org.cybercrowd.activity.dto.BaseResponse;
import org.cybercrowd.activity.dto.PlayerWinningDto;

import java.io.Serializable;
import java.util.List;

@Data
public class PreviousPlayerWinningRes extends BaseResponse implements Serializable {

    private List<PlayerWinningDto> playerWinnings;

    private String roundNo;
}
