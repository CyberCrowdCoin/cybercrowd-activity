package org.cybercrowd.activity.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.constant.RedisCacheKeyConstants;
import org.cybercrowd.activity.dto.*;
import org.cybercrowd.activity.enums.ReturnCodeEnum;
import org.cybercrowd.activity.mapper.TwitterFollowersMapper;
import org.cybercrowd.activity.mapper.UserActivityJobMapper;
import org.cybercrowd.activity.model.NewYear2022JobData;
import org.cybercrowd.activity.model.TwitterFollowers;
import org.cybercrowd.activity.model.UserActivityJob;
import org.cybercrowd.activity.request.ActivityNewYear2022DiscrodLoginReq;
import org.cybercrowd.activity.request.ActivityNewYear2022Req;
import org.cybercrowd.activity.request.ActivityNewYear2022TwitterLoginReq;
import org.cybercrowd.activity.response.ActivityNewYear2022Res;
import org.cybercrowd.activity.service.ActivityService;
import org.cybercrowd.activity.service.OAuthLoginService;
import org.cybercrowd.activity.utils.HttpClientUtil;
import org.cybercrowd.activity.utils.HttpRequestMethod;
import org.cybercrowd.activity.utils.InvitationCodeUtil;
import org.cybercrowd.activity.utils.RedissonLockUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    private Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Value("${activity.new_year_2022_share_url}")
    private String activityNewYear2022ShareUrl;

    @Value("${discord.member_query}")
    private String discrodMemberQueryUrl;

    @Value("${discord.token}")
    private String discordToken;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    OAuthLoginService oauthLoginService;

    @Autowired
    TwitterFollowersMapper twitterFollowersMapper;
    @Autowired
    UserActivityJobMapper userActivityJobMapper;

    @Override
    public ActivityNewYear2022Res activity2022NewYearSumbit(ActivityNewYear2022Req activityNewYear2022Req) {
        String jobName = "2022NewYear";
        ActivityNewYear2022Res activityNewYear2022Res = new ActivityNewYear2022Res();
        String ownerId = activityNewYear2022Req.getOwnerId();
        String content = activityNewYear2022Req.getContent();

        RedissonLockUtils redissonLockUtils = null;
        try{
            logger.info("2022新年活动业务接口,提交活动资料业务,ownerId:{} content:{}",ownerId,content);
            //检查指定活动用户是否已满足提交资料的条件
            //查询用户活动任务信息
            UserActivityJob userActivityJob =
                    userActivityJobMapper.findUserActivityJob(jobName,ownerId);
            if(null == userActivityJob){
                logger.info("2022新年活动业务接口,提交活动资料业务,ownerId:{} content:{} 未找到相关活动数据",ownerId,content);
                activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.ACTIVITY_NOT_FOUND_ERROR);
            }else{
                String lockKey = MessageFormat.format(
                        RedisCacheKeyConstants.USER_ACTIVITY_JOB_UPDATE_LOCK_KEY,userActivityJob.getId());
                redissonLockUtils = new RedissonLockUtils(redissonClient,lockKey,10000l);
                if(redissonLockUtils.lock()){
                    String jobStatus = userActivityJob.getJobStatus();
                    String twitterJobStatus = userActivityJob.getTwitterJobStatus();
                    String discordJobStatus = userActivityJob.getDiscordJobStatus();
                    if("1".equals(jobStatus)){
                        //已经提交过了，不再修改数据
                        //处理响应数据
                        return activityNewYear2022ResponseHandle(userActivityJob,activityNewYear2022Res);
                    }else if ("0".equals(jobStatus) && ("1".equals(discordJobStatus) || "1".equals(twitterJobStatus)) ){
                        //更新content
                        userActivityJob.setContent(content);
                        //更新任务状态
                        userActivityJob = updateActivityNewYear2022JobStatus(userActivityJob);
                        //处理响应数据
                        return activityNewYear2022ResponseHandle(userActivityJob,activityNewYear2022Res);
                    }else{
                        //未完成任务,不能提交数据
                        logger.info("2022新年活动业务接口,活动任务未完成,不能提交钱包地址数据");
                        activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.ACTIVITY_TASK_NOT_COMPLETED_ERROR);
                    }
                }else {
                    logger.info("2022新年活动业务接口,获取活动更新Redis锁异常");
                    activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.FAIL);
                }
            }
        }catch (Exception e){
            logger.error("2022新年活动业务接口,系统异常:{}",e.getMessage(),e);
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.FAIL);
        }finally {
            if(null != redissonLockUtils){
                redissonLockUtils.unlock();
            }
        }
        return activityNewYear2022Res;
    }

    private UserActivityJob updateActivityNewYear2022JobStatus(UserActivityJob userActivityJob) {
        boolean update = false;
        if("0".equals(userActivityJob.getTwitterJobStatus())
                && !StringUtils.isEmpty(userActivityJob.getTwitterId())) {
            if(twitterFollowStatus(userActivityJob.getTwitterId())){
                userActivityJob.setTwitterJobStatus("1");
                update = true;
            }
        }

        //查询Discard群是否已进入
        if("0".equals(userActivityJob.getDiscordJobStatus())
                && !StringUtils.isEmpty(userActivityJob.getDiscordId())) {
            if(discrordJoinStatus(userActivityJob.getDiscordId())){
                userActivityJob.setDiscordJobStatus("1");
                update = true;
            }
        }

        if("0".equals(userActivityJob.getJobStatus())
                && !StringUtils.isEmpty(userActivityJob.getContent())
                && ("1".equals(userActivityJob.getDiscordJobStatus())
                || "1".equals(userActivityJob.getTwitterJobStatus()))){
            userActivityJob.setJobStatus("1");
            userActivityJob.setJobSuccessTime(new Date());
            update = true;
        }

        if(update){
            userActivityJobMapper.updateByPrimaryKeySelective(userActivityJob);
        }
        return userActivityJob;
    }

    @Override
    public ActivityNewYear2022Res activity2022NewYerTwitterLogin(ActivityNewYear2022TwitterLoginReq activityNewYear2022TwitterLoginReq) {
        String jobName = "2022NewYear";
        ActivityNewYear2022Res activityNewYear2022Res = new ActivityNewYear2022Res();
        String oAuthCode = activityNewYear2022TwitterLoginReq.getOauthCode();
        String redirectUri = activityNewYear2022TwitterLoginReq.getRedirectUri();
        String parentInviteCode = activityNewYear2022TwitterLoginReq.getParentInviteCode();
        String oAuthVerifier = activityNewYear2022TwitterLoginReq.getOauthVerifier();
        String oAuthVersion = activityNewYear2022TwitterLoginReq.getOauthVersion();

        RedissonLockUtils redissonLockUtils = null;
        try {
            //检查邀请码是否正确
            if (!StringUtils.isEmpty(parentInviteCode)
                    && !checkActivityInviteCode(jobName, parentInviteCode)) {
                //邀请码错误
                logger.info("2022新年活动Twitter授权登录接口,邀请码错误...");
                activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.ACTIVITY_INVITE_CODE_ERROR);
                return activityNewYear2022Res;
            }
            //2022新年活动,授权登录获取用户信息
            TwitterAccountDto twitterAccountDto = loginTwitter(oAuthCode, oAuthVersion, oAuthVerifier, redirectUri);
            if (null == twitterAccountDto) {
                logger.info("2022新年活动Twitter授权登录接口,Twitter授权登录失败...");
                activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.AUTHORIZED_LOGIN_EXCEPTION);
            } else {
                String lockKey = MessageFormat.format(
                        RedisCacheKeyConstants.OAUTH_LOGIN_LOCK_KEY,twitterAccountDto.getData().getId());
                redissonLockUtils = new RedissonLockUtils(redissonClient,lockKey,10000l);
                if(redissonLockUtils.lock()){
                    String ownerId = twitterAccountDto.getData().getId();
                    //查询用户活动任务信息
                    UserActivityJob userActivityJob = userActivityJobMapper.findUserActivityJob(jobName, ownerId);
                    //更新或者保存活动任务数据状态
                    userActivityJob = updateOrSave2022NewYearUserActivityJob(userActivityJob, twitterAccountDto,null, parentInviteCode);
                    //更新任务状态
                    userActivityJob = updateActivityNewYear2022JobStatus(userActivityJob);
                    return activityNewYear2022ResponseHandle(userActivityJob, activityNewYear2022Res);
                }else {
                    logger.info("2022新年活动Twitter授权登录接口,获取授权登录Redis锁失败");
                    activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.FAIL);
                }
            }
        }catch (Exception e){
            logger.info("2022新年活动Twitter授权登录接口,异常:{} {}",e.getMessage(),e);
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.FAIL);
        }finally {
            if(null != redissonLockUtils){
                redissonLockUtils.unlock();
            }
        }
        return activityNewYear2022Res;
    }

    @Override
    public ActivityNewYear2022Res activity2022NewYerDiscordLogin(ActivityNewYear2022DiscrodLoginReq activityNewYear2022DiscrodLoginReq) {
        String jobName = "2022NewYear";
        ActivityNewYear2022Res activityNewYear2022Res = new ActivityNewYear2022Res();
        String oAuthCode = activityNewYear2022DiscrodLoginReq.getOauthCode();
        String redirectUri = activityNewYear2022DiscrodLoginReq.getRedirectUri();
        String parentInviteCode = activityNewYear2022DiscrodLoginReq.getParentInviteCode();

        RedissonLockUtils redissonLockUtils = null;
        try{
            //检查邀请码是否正确
            if (!StringUtils.isEmpty(parentInviteCode)
                    && !checkActivityInviteCode(jobName, parentInviteCode)) {
                //邀请码错误
                logger.info("2022新年活动Discord授权登录接口,邀请码错误...");
                activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.ACTIVITY_INVITE_CODE_ERROR);
                return activityNewYear2022Res;
            }

            DiscordAccountDto discordAccountDto = oauthLoginService.loginDiscord(oAuthCode,redirectUri);
            if (null == discordAccountDto) {
                logger.info("2022新年活动Discord授权登录接口,Discord授权登录失败...");
                activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.AUTHORIZED_LOGIN_EXCEPTION);
            } else {
                String ownerId = discordAccountDto.getId();
                String lockKey = MessageFormat.format(
                        RedisCacheKeyConstants.OAUTH_LOGIN_LOCK_KEY,ownerId);
                redissonLockUtils = new RedissonLockUtils(redissonClient,lockKey,10000l);
                if(redissonLockUtils.lock()){
                    //查询用户活动任务信息
                    UserActivityJob userActivityJob = userActivityJobMapper.findUserActivityJob(jobName, ownerId);
                    //更新或者保存活动任务数据状态
                    userActivityJob = updateOrSave2022NewYearUserActivityJob(userActivityJob, null,discordAccountDto, parentInviteCode);
                    //更新任务状态
                    userActivityJob = updateActivityNewYear2022JobStatus(userActivityJob);
                    return activityNewYear2022ResponseHandle(userActivityJob, activityNewYear2022Res);
                }else {
                    logger.info("2022新年活动Twitter授权登录接口,获取授权登录Redis锁失败");
                    activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.FAIL);
                }
            }


//            //查询用户活动任务信息
//            UserActivityJob userActivityJob =
//                    userActivityJobMapper.findUserActivityJob(jobName,ownerId);
//            if(null == userActivityJob){
//                //提示用户请先授权Twitter登录
//                activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.TWITTER_NOT_OAUTH_LOGIN_EXCEPTION);
//            }else{
//
//                if("0".equals(userActivityJob.getTwitterJobStatus())){
//                    logger.info("2022新年活动业务接口, Twitter关注任务未完成,不能登录Discord");
//                    activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.TWITTER_FOLLOW_TASK_NOT_COMPLETED_ERROR);
//                    return activityNewYear2022Res;
//                }
//
//                String jobStatus = userActivityJob.getJobStatus();
//                String discordJobStatus = userActivityJob.getDiscordJobStatus();
//                if("1".equals(jobStatus) || "1".equals(discordJobStatus)){
//                    //处理响应数据
//                    return activityNewYear2022ResponseHandle(userActivityJob, activityNewYear2022Res);
//                }else{
//                    DiscordAccountDto discordAccountDto = oauthLoginService.loginDiscord(oAuthCode,redirectUri);
//                    if(null == discordAccountDto){
//                        logger.info("2022新年活动业务接口,Discord授权登录失败...");
//                        activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.AUTHORIZED_LOGIN_EXCEPTION);
//                    }else if(!StringUtils.isEmpty(userActivityJob.getDiscordId())){
//                        //处理响应数据
//                        return activityNewYear2022ResponseHandle(userActivityJob, activityNewYear2022Res);
//                    }else {
//                        String lockKey = MessageFormat.format(
//                                RedisCacheKeyConstants.OAUTH_LOGIN_LOCK_KEY,discordAccountDto.getId());
//                        redissonLock = new RedissonLock(redissonClient,lockKey,10000l);
//                        if(redissonLock.lock()){
//                            //没有绑定discord账户
//                            //检查账户是否已被绑定了
//                            //更新job数据
//                            activityNewYear2022Res = bindDiscord2022NewYearUserActivityJob(discordAccountDto, userActivityJob, activityNewYear2022Res);
//                            //更新任务状态
//                            userActivityJob = updateActivityNewYear2022JobStatus(userActivityJob);
//                            //处理响应数据
//                            return activityNewYear2022ResponseHandle(userActivityJob, activityNewYear2022Res);
//                        }else {
//                            logger.info("2022新年活动Discord登录接口,获取授权登录Redis锁失败");
//                            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.FAIL);
//                        }
//                    }
//                }
//            }
        }catch (Exception e){
            logger.info("2022新年活动Discord登录接口,异常:{} \n{}",e.getMessage(),e);
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.FAIL);
        }finally {
            if(null != redissonLockUtils){
                redissonLockUtils.unlock();
            }
        }

        return activityNewYear2022Res;
    }

    @Override
    public ActivityNewYear2022Res activity2022NewYearPull(String ownerId) {
        String jobName = "2022NewYear";
        ActivityNewYear2022Res activityNewYear2022Res = new ActivityNewYear2022Res();
        UserActivityJob userActivityJob =
                userActivityJobMapper.findUserActivityJob(jobName,ownerId);
        if(null == userActivityJob){
            logger.info("2022新年活动数据拉取接口,当前ownerId 没有查询到指定数据");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.PARAMETER_ERROR);
            return activityNewYear2022Res;
        }else {
            //更新任务状态
            userActivityJob = updateActivityNewYear2022JobStatus(userActivityJob);
            return activityNewYear2022ResponseHandle(userActivityJob,activityNewYear2022Res);
        }
    }

    @Override
    public void updateActivity2022NewYearDiscordJobStatus(String discordId) {
        UserActivityJob bindDiscordJob = userActivityJobMapper.findBindDiscordJob("2022NewYear", discordId);
        if(null != bindDiscordJob && "0".equals(bindDiscordJob.getDiscordJobStatus())){
            bindDiscordJob.setDiscordJobStatus("1");
            userActivityJobMapper.updateByPrimaryKeySelective(bindDiscordJob);
            //2022新年活动 SSE推送关注状态到前端
            String ownerId = bindDiscordJob.getOwnerId();
            SSEJobStatusDto sseJobStatusDto = new SSEJobStatusDto();
            sseJobStatusDto.setJobStatus("1".equals(bindDiscordJob.getJobStatus()));
            sseJobStatusDto.setDiscordJobStatus("1".equals(bindDiscordJob.getDiscordJobStatus()));
            sseJobStatusDto.setTwitterJobStatus("1".equals(bindDiscordJob.getTwitterJobStatus()));
            sseJobStatusDto.setTelegramJobStatus("1".equals(bindDiscordJob.getTelegramJobStatus()));
        }
    }

    @Override
    public void updateActivity2022NewYearTwitterJobStatus(String twitterId) {
        UserActivityJob bindTwiiterJob = userActivityJobMapper.findBindTwitterJob("2022NewYear", twitterId);
        if(null != bindTwiiterJob && "0".equals(bindTwiiterJob.getTwitterJobStatus())){
            bindTwiiterJob.setTwitterJobStatus("1");
            userActivityJobMapper.updateByPrimaryKeySelective(bindTwiiterJob);
            //SSE推送关注状态到前端
            String ownerId = bindTwiiterJob.getOwnerId();
            SSEJobStatusDto sseJobStatusDto = new SSEJobStatusDto();
            sseJobStatusDto.setJobStatus("1".equals(bindTwiiterJob.getJobStatus()));
            sseJobStatusDto.setDiscordJobStatus("1".equals(bindTwiiterJob.getDiscordJobStatus()));
            sseJobStatusDto.setTwitterJobStatus("1".equals(bindTwiiterJob.getTwitterJobStatus()));
            sseJobStatusDto.setTelegramJobStatus("1".equals(bindTwiiterJob.getTelegramJobStatus()));
        }

    }

    @Override
    public void userJobDataExcel() {

        String json = "['1011562311839076352',\n" +
                "'1024257292939579423',\n" +
                "'1032194804957839360',\n" +
                "'1032479453881827428',\n" +
                "'1033605312080515132',\n" +
                "'1038784626442895410',\n" +
                "'1041203113345957889',\n" +
                "'1042116153230774315',\n" +
                "'1045991618018295808',\n" +
                "'1053603593648873512',\n" +
                "'1055739983002087436',\n" +
                "'1056257162889941134',\n" +
                "'1216033329394225152',\n" +
                "'1216648953531252736',\n" +
                "'1217750650081050628',\n" +
                "'1231702403268763648',\n" +
                "'1264944233229676545',\n" +
                "'1329455013106737153',\n" +
                "'1366931580674007043',\n" +
                "'1413329989907320832',\n" +
                "'1454434295288762370',\n" +
                "'1460834170779815937',\n" +
                "'1462386356538290186',\n" +
                "'1471795954802069504',\n" +
                "'1476779218272804864',\n" +
                "'1479523954045902944',\n" +
                "'1496557614708277254',\n" +
                "'1497093252927094784',\n" +
                "'1503641093274931201',\n" +
                "'1504742895886434304',\n" +
                "'1515881078027796481',\n" +
                "'1517150493201805314',\n" +
                "'1535798657307447296',\n" +
                "'1538179612127432705',\n" +
                "'1562282220907216896',\n" +
                "'1567716193464877056',\n" +
                "'1571574199134654465',\n" +
                "'1576143433072115712',\n" +
                "'1576788206930268160',\n" +
                "'1581621533323190272',\n" +
                "'1584424490452529152',\n" +
                "'1590510162946981890',\n" +
                "'1590673243278610432',\n" +
                "'1594947713476743168',\n" +
                "'1595687300306018311',\n" +
                "'1596122608000450560',\n" +
                "'1601102192131289088',\n" +
                "'1602996212747100163',\n" +
                "'1604721438224797701',\n" +
                "'1604925811605250059',\n" +
                "'1605333688581251073',\n" +
                "'1608122967392923648',\n" +
                "'537968782',\n" +
                "'538250172333948949',\n" +
                "'814936871130251264',\n" +
                "'830639099287044097',\n" +
                "'870281784275902505',\n" +
                "'877501494415486996',\n" +
                "'902145237722480692',\n" +
                "'917760263910297600',\n" +
                "'935947115316777030',\n" +
                "'937381553036754965',\n" +
                "'957948845487185960',\n" +
                "'981727902']";

        List<String> list = JSON.parseArray(json,String.class);

        UserActivityJob userActivityJobReq = new UserActivityJob();
        userActivityJobReq.setJobStatus("1");
        userActivityJobReq.setJobName("2022NewYear");
        List<UserActivityJob> userActivityJobs = userActivityJobMapper.selectList(userActivityJobReq);

        List<NewYear2022JobData> newYear2022JobDataList = new ArrayList<>();
        List<NewYear2022JobData> newYear2022JobDataList2 = new ArrayList<>();
        for(UserActivityJob userActivityJob:userActivityJobs){
            NewYear2022JobData newYear2022JobData = new NewYear2022JobData();
            String ownerAccountType = userActivityJob.getOwnerAccountType();
            if("twitter".equals(ownerAccountType)){
                TwitterInfoDto twitterInfoDto = JSON.parseObject(userActivityJob.getTwitterInfo(),TwitterInfoDto.class);
                String userNickName = twitterInfoDto.getUserNickName();
//                newYear2022JobData.setUserName(nickNameHandle(userNickName));

                newYear2022JobData.setUserName(userNickName);
                newYear2022JobData.setUserId(userActivityJob.getTwitterId());
            }else{
                DiscordInfoDto discordInfoDto = JSON.parseObject(userActivityJob.getDiscordInfo(),DiscordInfoDto.class);
                String userNickName = discordInfoDto.getUserNickName();
//                newYear2022JobData.setUserName(nickNameHandle(userNickName));

                newYear2022JobData.setUserName(userNickName);
                newYear2022JobData.setUserId(userActivityJob.getDiscordId());
            }
            newYear2022JobData.setAccountType(ownerAccountType);
            newYear2022JobData.setContent(userActivityJob.getContent().trim());
            newYear2022JobData.setInviterNumber(userActivityJob.getInviteUsersNumber().intValue());

            if(list.contains(newYear2022JobData.getUserId())){
                newYear2022JobDataList.add(newYear2022JobData);
            }
//            if(!WalletUtils.isValidAddress(userActivityJob.getContent().trim())){
//                newYear2022JobDataList2.add(newYear2022JobData);
//            }else{
//                newYear2022JobDataList.add(newYear2022JobData);
//            }

        }

        String filePath = "/Users/gengchaonan/Downloads/2022NewYear活动空投用户列表.xlsx";
        String sheetName = "空投用户列表";
        writeExcelFile(filePath,sheetName,NewYear2022JobData.class,newYear2022JobDataList);

        if(newYear2022JobDataList2.size() >0) {
            filePath = "/Users/gengchaonan/Downloads/2022NewYear活动空投用户列表-地址错误.xlsx";
            sheetName = "用户列表";
            writeExcelFile(filePath, sheetName, NewYear2022JobData.class, newYear2022JobDataList2);
        }
    }

    private void writeExcelFile(String filePath, String sheetName, Class obejctClass,Collection<?> data) {
        File excelFile = new File(filePath);
        if(!excelFile.exists()){
            try {
                excelFile.createNewFile();//创建一个新的文件
            } catch (IOException e) {
                logger.info("创建文件异常:{}",e.getMessage(),e);
            }
        }
        EasyExcel.write(excelFile,obejctClass)
                .excelType(ExcelTypeEnum.XLSX).sheet(sheetName).doWrite(data);
    }

    private String nickNameHandle(String userNickName) {
        if(userNickName.length() > 4){
            return MessageFormat.format("{0}{1}{2}",
                    userNickName.substring(0,2),"***",userNickName.substring(userNickName.length()-2));
        }else if(userNickName.length() <= 4 && userNickName.length() > 2){
            return MessageFormat.format("{0}{1}",
                    userNickName.substring(0,2),"***");
        }else{
            return MessageFormat.format("{0}{1}",
                    userNickName,"***");
        }
    }

    @Override
    public void updateUserJobStatus(int start) {
        List<UserActivityJob> userActivityJobs = userActivityJobMapper.selectList(null);
        for(int i = start;i<userActivityJobs.size();i++){
            UserActivityJob userActivityJob = userActivityJobs.get(i);
            String ownerAccountType = userActivityJob.getOwnerAccountType();
            if("twitter".equalsIgnoreCase(ownerAccountType)){
                if(twitterFollowStatus(userActivityJob.getOwnerId())){
                    userActivityJob.setTwitterJobStatus("1");
                }else {
                    userActivityJob.setTwitterJobStatus("0");
                }
            }else {
                if(discrordJoinStatus(userActivityJob.getOwnerId())){
                    userActivityJob.setDiscordJobStatus("1");
                }else {
                    userActivityJob.setDiscordJobStatus("0");
                }
            }

            if(!StringUtils.isEmpty(userActivityJob.getContent())
                    && ("1".equals(userActivityJob.getTwitterJobStatus())
                    || "1".equals(userActivityJob.getDiscordJobStatus()))) {
                userActivityJob.setJobStatus("1");
            }else {
                userActivityJob.setJobStatus("0");
            }
            userActivityJob.setUpdateTime(new Date());
            userActivityJobMapper.updateByPrimaryKeySelective(userActivityJob);
            logger.info("更新用户任务状态,第 {} 条数据",i + 1);
        }
    }

    private ActivityNewYear2022Res activityNewYear2022ResponseHandle(UserActivityJob userActivityJob,
                                                                     ActivityNewYear2022Res activityNewYear2022Res) {

        if(ReturnCodeEnum.SUCCESS.getCode().equals(activityNewYear2022Res.getCode())){

            activityNewYear2022Res.setTwitterFollowStatus("1".equals(userActivityJob.getTwitterJobStatus()));
            activityNewYear2022Res.setDiscordStatus("1".equals(userActivityJob.getDiscordJobStatus()));

            if("1".equals(userActivityJob.getJobStatus())){
                //已提交数据,创建分享链接
                String inviteCode = userActivityJob.getInviteCode();
                activityNewYear2022Res.setShareUrl(MessageFormat.format(activityNewYear2022ShareUrl,inviteCode));
                activityNewYear2022Res.setContent(userActivityJob.getContent());
                activityNewYear2022Res.setJobStatus(true);
            }

            activityNewYear2022Res.setOwnerId(userActivityJob.getOwnerId());

            activityNewYear2022Res.setLoginToken(userActivityJob.getLoginToken() + "/" + userActivityJob.getOwnerId() );
            activityNewYear2022Res.setDiscordInfoDto(
                    !StringUtils.isEmpty(userActivityJob.getDiscordInfo())
                            ? JSON.parseObject(userActivityJob.getDiscordInfo(),DiscordInfoDto.class):null);
            activityNewYear2022Res.setTwitterInfoDto(
                    !StringUtils.isEmpty(userActivityJob.getTwitterInfo())
                            ? JSON.parseObject(userActivityJob.getTwitterInfo(), TwitterInfoDto.class):null);
        }else{
            //失败
        }
        return activityNewYear2022Res;
    }

    private boolean checkActivityInviteCode(String jobName, String parentInviteCode) {
        UserActivityJob parentUserActivityJob =
                userActivityJobMapper.findUserActivityJobByInviteCode(jobName, parentInviteCode);
        if(null != parentUserActivityJob){
            return true;
        }
        return false;
    }

    private ActivityNewYear2022Res bindDiscord2022NewYearUserActivityJob(DiscordAccountDto discordAccountDto,
                                                       UserActivityJob userActivityJob,ActivityNewYear2022Res activityNewYear2022Res) {
        String discordId = discordAccountDto.getId();
        //检查任务是否已经绑定了这个discordId
        if(discordId.equals(userActivityJob.getDiscordId())){
            //已绑定就不需要处理了
            DiscordInfoDto discordInfoDto = new DiscordInfoDto();
            discordInfoDto.setUserAvatar(discordAccountDto.getAvatar());
            discordInfoDto.setUserNickName(discordAccountDto.getUsername());
            userActivityJob.setDiscordInfo(JSON.toJSONString(discordInfoDto));
        }else{
            //检查是否discord账户已经被其他账户绑定
            UserActivityJob bindDiscordJob = userActivityJobMapper.findBindDiscordJob(userActivityJob.getJobName(), discordId);
            if(null != bindDiscordJob && !bindDiscordJob.getOwnerId().equals(userActivityJob.getOwnerId())){
                //提示已被其他用户绑定
                logger.info("2022新年活动业务接口,discord id:{} 已被OwnerId:{} 绑定",discordId,bindDiscordJob.getOwnerId());
                activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.DISCORE_ALREADY_BOUND_USER_ERROR);
            }else{
                userActivityJob.setDiscordId(discordId);
                DiscordInfoDto discordInfoDto = new DiscordInfoDto();
                discordInfoDto.setUserAvatar(discordAccountDto.getAvatar());
                discordInfoDto.setUserNickName(discordAccountDto.getUsername());
                userActivityJob.setDiscordInfo(JSON.toJSONString(discordInfoDto));
                userActivityJobMapper.updateByPrimaryKeySelective(userActivityJob);
            }
        }
        return activityNewYear2022Res;
    }

    private UserActivityJob updateOrSave2022NewYearUserActivityJob(UserActivityJob userActivityJob,
                                                        TwitterAccountDto twitterAccountDto,DiscordAccountDto discordAccountDto,String parentInviteCode) {

        //是否已存在数据
        if(null == userActivityJob){
            userActivityJob = new UserActivityJob();
            userActivityJob.setJobName("2022NewYear");

            //自己任务的父邀请码是null的并且自己的邀请码不能是父邀请码
            if(StringUtils.isEmpty(userActivityJob.getParentInviteCode())){
                userActivityJob.setParentInviteCode(parentInviteCode);
            }

            userActivityJob.setCreateTime(new Date());
            userActivityJob.setUpdateTime(new Date());

            userActivityJob.setDiscordJobStatus("0");
            userActivityJob.setTwitterJobStatus("0");
            userActivityJob.setTwitterJobStatus("0");
            userActivityJob.setJobStatus("0");

            if(null != twitterAccountDto) {
                String userNickName = twitterAccountDto.getData().getUsername();
                String userAvatar = twitterAccountDto.getData().getProfile_image_url();
                String ownerId = twitterAccountDto.getData().getId();

                userActivityJob.setOwnerAccountType("twitter");
                userActivityJob.setOwnerId(ownerId);
                userActivityJob.setTwitterId(ownerId);
                TwitterInfoDto twitterInfoDto = new TwitterInfoDto();
                twitterInfoDto.setUserAvatar(userAvatar);
                twitterInfoDto.setUserNickName(userNickName);
                userActivityJob.setTwitterInfo(JSON.toJSONString(twitterInfoDto));
                //生成邀请码
                userActivityJob.setInviteCode(InvitationCodeUtil.generate(
                        System.currentTimeMillis()+Long.valueOf(ownerId)));
            }

            if(null != discordAccountDto){

                String userNickName = discordAccountDto.getUsername();
                String userAvatar = discordAccountDto.getAvatar();
                String ownerId = discordAccountDto.getId();
                        userActivityJob.setOwnerAccountType("discord");
                userActivityJob.setOwnerId(ownerId);
                userActivityJob.setDiscordId(ownerId);
                DiscordInfoDto discordInfoDto = new DiscordInfoDto();
                discordInfoDto.setUserAvatar(userAvatar);
                discordInfoDto.setUserNickName(userNickName);

                //生成邀请码
                userActivityJob.setInviteCode(InvitationCodeUtil.generate(
                        System.currentTimeMillis()+Long.valueOf(ownerId)));

                userActivityJob.setDiscordInfo(JSON.toJSONString(discordInfoDto));
            }

            //登录Token
            userActivityJob.setLoginToken(generateLoginToken(userActivityJob.getOwnerId()));
            logger.info("创建 用户Job数据:{}",JSON.toJSONString(userActivityJob));
            userActivityJobMapper.insertSelective(userActivityJob);
        }else{
            if(null != twitterAccountDto) {
                String userNickName = twitterAccountDto.getData().getUsername();
                String userAvatar = twitterAccountDto.getData().getProfile_image_url();

                TwitterInfoDto twitterInfoDto = new TwitterInfoDto();
                twitterInfoDto.setUserAvatar(userAvatar);
                twitterInfoDto.setUserNickName(userNickName);
                userActivityJob.setTwitterInfo(JSON.toJSONString(twitterInfoDto));
            }

            if(null != discordAccountDto){
                String userNickName = discordAccountDto.getUsername();
                String userAvatar = discordAccountDto.getAvatar();

                DiscordInfoDto discordInfoDto = new DiscordInfoDto();
                discordInfoDto.setUserAvatar(userAvatar);
                discordInfoDto.setUserNickName(userNickName);
                userActivityJob.setDiscordInfo(JSON.toJSONString(discordInfoDto));
            }
            //登录Token
            userActivityJob.setLoginToken(generateLoginToken(userActivityJob.getOwnerId()));
            userActivityJobMapper.updateByPrimaryKeySelective(userActivityJob);
        }
        return userActivityJob;
    }

    private TwitterAccountDto loginTwitter(String oauthCode,
                                           String version,String verifier, String redirectUri) {
        if(StringUtils.isEmpty(version) || "2.0".equals(version)){
            return oauthLoginService.loginTwitterV2(oauthCode, redirectUri);
        }else {
            //1.0版本
            return oauthLoginService.loginTwitterV1(oauthCode,verifier);
        }
    }

    private boolean twitterFollowStatus(String twiiterId){
        //查询Twitter 是不是已经关注了
        TwitterFollowers twitterFollowersReq = new TwitterFollowers();
        twitterFollowersReq.setTwitterId(twiiterId);
        twitterFollowersReq.setFollowStatus("1");
        TwitterFollowers twitterFollowers = twitterFollowersMapper.selectOne(twitterFollowersReq);
        if (null != twitterFollowers) {
            return true;
        }
        return false;
    }

    private boolean discrordJoinStatus(String discordId){
        String url = MessageFormat.format(discrodMemberQueryUrl,discordId);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization","Bot "+ discordToken);
        String result = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet, url, null, header);
        if(!StringUtils.isEmpty(result)){
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object codeObject = jsonObject.get("code");
            if(null == codeObject){
                return true;
            }
        }
        return false;
    }

    private String generateLoginToken(String ownerId){
        //生成登录token
        return RedisCacheKeyConstants.LOGIN_USER_SESSION_ID + ownerId + ":" + IdUtil.fastSimpleUUID();
    }
}
