package org.cybercrowd.activity.controller;

import org.cybercrowd.activity.request.DappsListReq;
import org.cybercrowd.activity.response.DappsBannerListRes;
import org.cybercrowd.activity.response.DappsListRes;
import org.cybercrowd.activity.service.DappsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class DappsController {

    @Autowired
    DappsService dappsService;

    @RequestMapping(value = "/dapps/v1/dapps-list")
    @ResponseBody
    public DappsListRes dappsList(@RequestBody DappsListReq dappsListReq){
        return dappsService.dappsList(dappsListReq);
    }

    @RequestMapping(value = "/dapps/v1/banner-list")
    @ResponseBody
    public DappsBannerListRes dappsBannerList(){
       return dappsService.dappsBannerList();
    }
}
