package org.cybercrowd.activity.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TicketQueryRes implements Serializable {

    private BigDecimal ticketQuantity;


}
