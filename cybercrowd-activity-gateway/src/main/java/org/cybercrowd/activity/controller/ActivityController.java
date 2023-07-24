package org.cybercrowd.activity.controller;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.constant.HttpCookieConstants;
import org.cybercrowd.activity.enums.ReturnCodeEnum;
import org.cybercrowd.activity.request.ActivityNewYear2022DiscrodLoginReq;
import org.cybercrowd.activity.request.ActivityNewYear2022Req;
import org.cybercrowd.activity.request.ActivityNewYear2022TwitterLoginReq;
import org.cybercrowd.activity.response.ActivityNewYear2022Res;
import org.cybercrowd.activity.service.ActivityService;
import org.cybercrowd.activity.service.VoteInteractiveRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin("*")
public class ActivityController extends BaseController{

    private Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    ActivityService activityService;
    @Autowired
    VoteInteractiveRecordService voteInteractiveRecordService;
    @Autowired
    StringRedisTemplate redisTemplate;


    @RequestMapping(value = "/2022/new-year/twitter-login")
    @ResponseBody
    public ActivityNewYear2022Res newYear2022TwitterLogin(@RequestBody ActivityNewYear2022TwitterLoginReq activityNewYear2022TwitterLoginReq,
                                                          HttpServletResponse response){
        ActivityNewYear2022Res activityNewYear2022Res = new ActivityNewYear2022Res();
        //2023-01-08 16:00:00
        long activityEndTime = 1673352000000l;
        long nowTime = System.currentTimeMillis();

        if(nowTime >= activityEndTime){
            logger.info("2022新年活动Twitter登录接口,活动已结束");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.ACITVITY_ENDED_ERROR);
            return activityNewYear2022Res;
        }

        logger.info("2022新年活动Twitter登录接口,请求入参:{}",JSON.toJSONString(activityNewYear2022TwitterLoginReq));
        String oAuthCode = activityNewYear2022TwitterLoginReq.getOauthCode();
        String redirectUri = activityNewYear2022TwitterLoginReq.getRedirectUri();
        String oAuthVerifier = activityNewYear2022TwitterLoginReq.getOauthVerifier();
        String oAuthVersion = activityNewYear2022TwitterLoginReq.getOauthVersion();

        if(StringUtils.isEmpty(oAuthCode)
                || StringUtils.isEmpty(oAuthVersion)
                || (!"1.0".equals(oAuthVersion) && !"2.0".equals(oAuthVersion))){
            logger.info("2022新年活动Twitter登录接口,OAUthCode或者OAuthVersion不能为空...");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.PARAMETER_ERROR);
            return activityNewYear2022Res;
        }else if("1.0".equals(oAuthVersion) && StringUtils.isEmpty(oAuthVerifier)){
            logger.info("2022新年活动Twitter登录接口,1.0认证,oAuthVerifier不能为空...");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.PARAMETER_ERROR);
            return activityNewYear2022Res;
        }else if("2.0".equals(oAuthVersion) && StringUtils.isEmpty(redirectUri)){
            logger.info("2022新年活动Twitter登录接口,2.0认证,redirectUri不能为空...");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.PARAMETER_ERROR);
            return activityNewYear2022Res;
        }

        activityNewYear2022Res = activityService.activity2022NewYerTwitterLogin(activityNewYear2022TwitterLoginReq);

        if(ReturnCodeEnum.SUCCESS.getCode().equals(activityNewYear2022Res.getCode())){
            //设置cookie和redis缓存
            String loginToken = activityNewYear2022Res.getLoginToken();
            String [] loginTokens = loginToken.split("/");
            activityNewYear2022Res.setLoginToken(loginTokens[0]);

            //设置Cookie 有效时间 30分钟
            Cookie cookie = new Cookie(HttpCookieConstants.LOGIN_SESSION_ID, loginTokens[0]);
//            cookie.setMaxAge(60 * 30);
            cookie.setMaxAge(60 * 60 * 24);
            cookie.setPath("/");
            response.addCookie(cookie);
            // 设置redis token有效期有效时间 30分钟
//            redisTemplate.opsForValue().set(loginTokens[0],loginTokens[1], 30, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(loginTokens[0],loginTokens[1], 1, TimeUnit.DAYS);
        }
        logger.info("2022新年活动Twitter登录接口,响应结果:{}",JSON.toJSONString(activityNewYear2022Res));
        return activityNewYear2022Res;
    }


    @RequestMapping(value = "/2022/new-year/discord-login")
    @ResponseBody
    public ActivityNewYear2022Res newYear2022DiscordLogin(@RequestBody ActivityNewYear2022DiscrodLoginReq activityNewYear2022DiscrodLoginReq,
                                                          HttpServletResponse response){
        ActivityNewYear2022Res activityNewYear2022Res = new ActivityNewYear2022Res();
        //2023-01-08 16:00:00
        long activityEndTime = 1673352000000l;
        long nowTime = System.currentTimeMillis();

        if(nowTime >= activityEndTime){
            logger.info("2022新年活动Discord登录接口,活动已结束");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.ACITVITY_ENDED_ERROR);
            return activityNewYear2022Res;
        }

        logger.info("2022新年活动Discord登录接口,请求入参:{}",JSON.toJSONString(activityNewYear2022DiscrodLoginReq));

        String oAuthCode = activityNewYear2022DiscrodLoginReq.getOauthCode();
        String redirectUri = activityNewYear2022DiscrodLoginReq.getRedirectUri();

       if(StringUtils.isEmpty(oAuthCode) || StringUtils.isEmpty(redirectUri)){
            logger.info("2022新年活动Discord登录接口,OAUthCode或者redirectUri不能为空...");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.PARAMETER_ERROR);
            return activityNewYear2022Res;
        }else{
            activityNewYear2022Res = activityService.activity2022NewYerDiscordLogin(activityNewYear2022DiscrodLoginReq);
           if(ReturnCodeEnum.SUCCESS.getCode().equals(activityNewYear2022Res.getCode())){
               //设置cookie和redis缓存
               String loginToken = activityNewYear2022Res.getLoginToken();
               String [] loginTokens = loginToken.split("/");
               activityNewYear2022Res.setLoginToken(loginTokens[0]);

               //设置Cookie 有效时间 30分钟
               Cookie cookie = new Cookie(HttpCookieConstants.LOGIN_SESSION_ID, loginTokens[0]);
//            cookie.setMaxAge(60 * 30);
               cookie.setMaxAge(60 * 60 * 24);
               cookie.setPath("/");
               response.addCookie(cookie);
               // 设置redis token有效期有效时间 30分钟
//            redisTemplate.opsForValue().set(loginTokens[0],loginTokens[1], 30, TimeUnit.MINUTES);
               redisTemplate.opsForValue().set(loginTokens[0],loginTokens[1], 1, TimeUnit.DAYS);
           }
           logger.info("2022新年活动Discord登录接口,响应结果:{}",JSON.toJSONString(activityNewYear2022Res));
           return activityNewYear2022Res;
        }
    }


    @RequestMapping(value = "/2022/new-year/pull")
    @ResponseBody
    public ActivityNewYear2022Res newYear2022Pull(HttpServletRequest request){
        ActivityNewYear2022Res activityNewYear2022Res = new ActivityNewYear2022Res();
        //2023-01-08 16:00:00
        long activityEndTime = 1673352000000l;
        long nowTime = System.currentTimeMillis();

        if(nowTime >= activityEndTime){
            logger.info("2022新年活动数据拉取接口,活动已结束");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.ACITVITY_ENDED_ERROR);
            return activityNewYear2022Res;
        }

        String ownerId = getLoginOwnerId(request);
        if(StringUtils.isEmpty(ownerId)){
            logger.info("2022新年活动数据拉取接口, 用户未登录,拉取失败");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.PLEASE_LOGIN);
            return activityNewYear2022Res;
        }
        activityNewYear2022Res = activityService.activity2022NewYearPull(ownerId);
        if(ReturnCodeEnum.SUCCESS.getCode().equals(activityNewYear2022Res.getCode())){
            String loginToken = activityNewYear2022Res.getLoginToken();
            String [] loginTokens = loginToken.split("/");
            activityNewYear2022Res.setLoginToken(loginTokens[0]);
        }
        logger.info("2022新年活动数据拉取接口,响应结果:{}",JSON.toJSONString(activityNewYear2022Res));
        return activityNewYear2022Res;
    }

    @RequestMapping(value = "/2022/new-year/submit")
    @ResponseBody
    public ActivityNewYear2022Res newYear2022Submit(@RequestBody ActivityNewYear2022Req activityNewYear2022Req,
                                                    HttpServletRequest request){
        ActivityNewYear2022Res activityNewYear2022Res = new ActivityNewYear2022Res();
        //2023-01-08 16:00:00
        long activityEndTime = 1673352000000l;
        long nowTime = System.currentTimeMillis();

        if(nowTime >= activityEndTime){
            logger.info("2022新年活动提交接口,活动已结束");
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.ACITVITY_ENDED_ERROR);
            return activityNewYear2022Res;
        }
        logger.info("2022新年活动提交接口,请求入参:{}", JSON.toJSONString(activityNewYear2022Req));

        String ownerId = getLoginOwnerId(request);
        if(StringUtils.isEmpty(ownerId)){
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.PLEASE_LOGIN);
            logger.info("2022新年活动提交接口,未登录不能提交活动数据...");
            return activityNewYear2022Res;
        }

        String content = activityNewYear2022Req.getContent();
        if(StringUtils.isEmpty(content)){
            activityNewYear2022Res.setReturnCodeEnum(ReturnCodeEnum.PARAMETER_ERROR);
            logger.info("2022新年活动提交接口,活动提交数据不能为空...");
            return activityNewYear2022Res;
        }

        activityNewYear2022Req.setOwnerId(ownerId);
        activityNewYear2022Res = activityService.activity2022NewYearSumbit(activityNewYear2022Req);
        if(ReturnCodeEnum.SUCCESS.getCode().equals(activityNewYear2022Res.getCode())){
            String loginToken = activityNewYear2022Res.getLoginToken();
            String [] loginTokens = loginToken.split("/");
            activityNewYear2022Res.setLoginToken(loginTokens[0]);
        }
        logger.info("2022新年活动提交接口,响应结果:{}", JSON.toJSONString(activityNewYear2022Res));
        return activityNewYear2022Res;
    }

    @RequestMapping(value = "/job/data-excel")
    public void userJobDataExcel(){
        activityService.userJobDataExcel();
    }

    @RequestMapping(value = "/job/update")
    public void updateUserJobStatus(int start){
        activityService.updateUserJobStatus(start);
    }


    @RequestMapping(value = "/ticket/query")
    @ResponseBody
    public BigDecimal ticketQuery(String walletAddr){
        logger.info("活动奖券查询接口,请求入参:{}", walletAddr);
        if(!StringUtils.isEmpty(walletAddr)) {
            BigDecimal ticket = voteInteractiveRecordService.ticketQuery(walletAddr);
            logger.info("活动奖券查询接口,响应结果:{}",ticket);
            if(null != ticket){
                return ticket;
            }
        }
        return new BigDecimal(0);
    }

}
