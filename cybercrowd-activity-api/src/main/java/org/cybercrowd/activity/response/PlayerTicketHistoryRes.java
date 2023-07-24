package org.cybercrowd.activity.response;

import lombok.Data;
import org.cybercrowd.activity.dto.BaseResponse;
import org.cybercrowd.activity.dto.TicketHistoryDto;

import java.io.Serializable;
import java.util.List;

@Data
public class PlayerTicketHistoryRes extends BaseResponse implements Serializable {

    private List<TicketHistoryDto> ticketHistorys;

}
