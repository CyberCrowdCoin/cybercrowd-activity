package org.cybercrowd.activity.mapper;

import org.cybercrowd.activity.model.DappsInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DappsInfoMapper继承基类
 */
public interface DappsInfoMapper extends MyBatisBaseDao<DappsInfo, Long> {

    List<DappsInfo> selectDappsList(String dappType,String dappHotStatus);

}