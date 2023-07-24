package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.DappsBanner;

import java.util.List;

/**
 * DappsBannerMapper继承基类
 */
public interface DappsBannerMapper extends MyBatisBaseDao<DappsBanner, Long> {

    List<DappsBanner> selectDappsBannerList(DappsBanner params);
}