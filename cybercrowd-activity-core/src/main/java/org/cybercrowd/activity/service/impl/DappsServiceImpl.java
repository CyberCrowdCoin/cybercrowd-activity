package org.cybercrowd.activity.service.impl;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.cybercrowd.activity.dto.DappsBannerDto;
import org.cybercrowd.activity.dto.DappsInfoDto;
import org.cybercrowd.activity.enums.DappsBannerStatusEnum;
import org.cybercrowd.activity.enums.ReturnCodeEnum;
import org.cybercrowd.activity.mapper.DappsBannerMapper;
import org.cybercrowd.activity.mapper.DappsInfoMapper;
import org.cybercrowd.activity.model.DappsBanner;
import org.cybercrowd.activity.model.DappsInfo;
import org.cybercrowd.activity.request.DappsListReq;
import org.cybercrowd.activity.response.DappsBannerListRes;
import org.cybercrowd.activity.response.DappsListRes;
import org.cybercrowd.activity.service.DappsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("dappsService")
public class DappsServiceImpl implements DappsService {

    private Logger logger = LoggerFactory.getLogger(DappsServiceImpl.class);

    @Autowired
    DappsBannerMapper dappsBannerMapper;
    @Autowired
    DappsInfoMapper dappsInfoMapper;

    @Override
    public DappsBannerListRes dappsBannerList() {
        DappsBannerListRes dappsBannerListRes = new DappsBannerListRes();
        logger.info("进入Dapp Banner列表查询接口");
        try {
            DappsBanner params = new DappsBanner();
            params.setBannerStatus(DappsBannerStatusEnum.STATUS_1.getCode());
            List<DappsBanner> banners = dappsBannerMapper.selectDappsBannerList(params);
            if (null != banners && banners.size() > 0) {
                List<DappsBannerDto> dappsBannerList = new ArrayList<>();
                for (DappsBanner dappsBanner : banners) {
                    DappsBannerDto dappsBannerDto = new DappsBannerDto();
                    dappsBannerDto.setBannerIntro(dappsBanner.getBannerIntro());
                    dappsBannerDto.setBannerImageUrl(dappsBanner.getBannerImageUrl());
                    dappsBannerDto.setBannerMobileUrl(dappsBanner.getBannerMobileUrl());
                    dappsBannerDto.setJumpUrl(dappsBanner.getJumpUrl());
                    dappsBannerList.add(dappsBannerDto);
                }
                dappsBannerListRes.setDappsBannerList(dappsBannerList);
            }
            logger.info("Dapp Banner列表查询接口,响应结果:{}",JSON.toJSONString(dappsBannerListRes));
        }catch (Exception e){
            dappsBannerListRes.setReturnCodeEnum(ReturnCodeEnum.FAIL);
            logger.error("Dapp Banner列表查询接口,执行异常:{}",e.getMessage(),e);
        }
        return dappsBannerListRes;
    }

    @Override
    public DappsListRes dappsList(DappsListReq dappsListReq) {
        logger.info("Dapp列表查询接口,请求入参:{}",JSON.toJSONString(dappsListReq));
        DappsListRes dappsListRes = new DappsListRes();
        String dappType = dappsListReq.getDappType();
        boolean hot = dappsListReq.isHot();
        int pageNum = dappsListReq.getPageNum();
        int pageSize = dappsListReq.getPageSize();
        try {
            dappsListRes.setPageNum(pageNum);
            dappsListRes.setPageSize(pageSize);
            PageHelper.startPage(pageNum, pageSize);
            String dappHotStatus = null;
            if(hot == true){
                PageHelper.orderBy("dapp_hot desc");
                dappHotStatus = "1";
            }else {
                PageHelper.orderBy("create_time desc");
            }

            List<DappsInfo> dappsInfos = dappsInfoMapper.selectDappsList(dappType, dappHotStatus);
            if (null != dappsInfos && dappsInfos.size() > 0) {
                PageInfo pageInfo = new PageInfo(dappsInfos);
                dappsListRes.setTotalPage(pageInfo.getPages());
                List<DappsInfoDto> dappsInfoDtos = new ArrayList<>();
                for (DappsInfo dappsInfo : dappsInfos) {
                    DappsInfoDto dappsInfoDto = new DappsInfoDto();
                    BeanUtils.copyProperties(dappsInfo, dappsInfoDto);
                    dappsInfoDtos.add(dappsInfoDto);
                }
                dappsListRes.setDappsList(dappsInfoDtos);
            }else {
                dappsListRes.setDappsList(null);
            }
        }catch (Exception e){
            logger.error("Dapp列表查询接口,执行异常:{}",e.getMessage(),e);
            dappsListRes.setReturnCodeEnum(ReturnCodeEnum.FAIL);
        }
        logger.info("Dapp列表查询接口,响应结果:{}", JSON.toJSONString(dappsListRes));
        return dappsListRes;
    }
}
