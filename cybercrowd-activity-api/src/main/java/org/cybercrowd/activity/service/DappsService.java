package org.cybercrowd.activity.service;

import org.cybercrowd.activity.request.DappsListReq;
import org.cybercrowd.activity.response.DappsBannerListRes;
import org.cybercrowd.activity.response.DappsListRes;

public interface DappsService {


    DappsBannerListRes dappsBannerList();

    DappsListRes dappsList(DappsListReq dappsListReq);


}
