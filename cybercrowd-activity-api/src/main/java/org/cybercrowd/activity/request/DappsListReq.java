package org.cybercrowd.activity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DappsListReq implements Serializable {

    private int pageNum = 1;
    private int pageSize = 5;

    private boolean hot;
    private String dappType;

}
