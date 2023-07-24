package org.cybercrowd.activity.controller;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.annotation.TwitterRequsetParam;
import org.cybercrowd.activity.constant.RedisCacheKeyConstants;
import org.cybercrowd.activity.dto.TwitterFollowEeventsDto;
import org.cybercrowd.activity.dto.TwitterFollowMessageDto;
import org.cybercrowd.activity.enums.ReturnCodeEnum;
import org.cybercrowd.activity.response.TwitterOAuthV1UrlGetRes;
import org.cybercrowd.activity.service.OAuthLoginService;
import org.cybercrowd.activity.service.TwitterService;
import org.cybercrowd.activity.service.activemq.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin("*")
public class TwitterController {

    private Logger logger = LoggerFactory.getLogger(TwitterController.class);

    @Value("${twitter.official_account_id}")
    private String twitterOfficialAccountId;
    @Value("${twitter.signature}")
    private String twitterSignature;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    TwitterService twitterService;
    @Autowired
    ProducerService producerService;
    @Autowired
    OAuthLoginService oAuthLoginService;


    @RequestMapping(value = "/twitter/v1/follow-pull")
    @ResponseBody
    public String pullFollow(int fileNumber){
        String switchVaule = redisTemplate.opsForValue().get(RedisCacheKeyConstants.TWITTER_FOLLOW_PULL_SWITCH);
        if(!StringUtils.isEmpty(switchVaule)){
            twitterService.pullTwitterFollowUser(fileNumber);
            return "Pulling";
        }
        return "Permission not opened";
    }

    @RequestMapping(value = "/twitter/v1/follow-pull-list")
    @ResponseBody
    public String pullFollow(String bearerToken){
        twitterService.pullTwitterFollowUserList(bearerToken,null,null,1);
        return "Pulling";
    }

    @RequestMapping(value = "/twitter/webhook")
    @ResponseBody
    public Object twitterWebhook(@TwitterRequsetParam Object message, HttpServletRequest request){
        String messageStr = JSON.toJSONString(message);
        String crc_token = request.getParameter("crc_token");
        if(!StringUtils.isEmpty(crc_token)) {
            logger.info("twitter webhook token验证请求入参,crc_token:{}",crc_token);
            try {
                String signature = getSignature(twitterSignature, crc_token);
                Map<String,Object> responseMap = new HashMap<>();
                responseMap.put("response_token","sha256=" + signature);
                logger.info("twitter webhook token验证,响应数据:{}",JSON.toJSONString(responseMap));
                return responseMap;
            }catch (Exception e){
                logger.info("twitter webhook token验证,处理异常:{}",e.getMessage(),e);
            }
        }else {
            logger.info("twitter webhook通知消息接收接口,请求入参:{}",messageStr);
            JSONObject jsonObject = JSONObject.parseObject(messageStr);
            //关注
            Object follow_events = jsonObject.get("follow_events");
            if(null != follow_events){
                TwitterFollowEeventsDto twitterFollowEeventsDto =
                        JSON.parseObject(messageStr,TwitterFollowEeventsDto.class);
                TwitterFollowEeventsDto.FollowEventsBean
                        followEventsBean = twitterFollowEeventsDto.getFollow_events().get(0);
                TwitterFollowEeventsDto.FollowEventsBean.TargetBean
                        target = followEventsBean.getTarget();
                TwitterFollowEeventsDto.FollowEventsBean.SourceBean
                        source = followEventsBean.getSource();
                //确认是否关注的我们官方twitter账号
                if(target.getId().equals(twitterOfficialAccountId)){
                    logger.info("twitter webhook通知消息接收接口,是关注官方Twitter的消息");
                    sendTwitterFollowMessage(target,source,new Date());
                }
            }
//        //@消息
//        JSONObject tweetCreateEventsObject = jsonObject.getJSONObject("tweet_create_events");
//        //私信
//        JSONObject directMessageEventsObject = jsonObject.getJSONObject("direct_message_events");
//        //私信已读
//        JSONObject directMessageIndicateTypingEventsObject = jsonObject.getJSONObject("direct_message_indicate_typing_events");
        }
        return null;
    }

    @RequestMapping(value = "/twitter/oauthv1/authorization-url")
    @ResponseBody
    public TwitterOAuthV1UrlGetRes getTwitterOAuthV1Url(String redirectUri){
        TwitterOAuthV1UrlGetRes twitterOAuthV1UrlGetRes = new TwitterOAuthV1UrlGetRes();

        //2023-01-08 16:00:00
        long activityEndTime = 1673352000000l;
        long nowTime = System.currentTimeMillis();

        if(nowTime >= activityEndTime){
            logger.info("Twitter OAuthV1 认证地址获取接口,活动已结束");
            twitterOAuthV1UrlGetRes.setReturnCodeEnum(ReturnCodeEnum.ACITVITY_ENDED_ERROR);
            return twitterOAuthV1UrlGetRes;
        }
        logger.info("Twitter OAuthV1 认证地址获取接口,请求入参:{}",redirectUri);
        if(StringUtils.isEmpty(redirectUri)){
            logger.info("Twitter OAuthV1 认证地址获取接口,回调地址参数不能为空...");
            twitterOAuthV1UrlGetRes.setReturnCodeEnum(ReturnCodeEnum.PARAMETER_ERROR);
            return twitterOAuthV1UrlGetRes;
        }
        twitterOAuthV1UrlGetRes = oAuthLoginService.getTwitterOAuthV1AuthorizationUrl(redirectUri);
        logger.info("Twitter OAuthV1 认证地址获取接口,响应结果:{}",JSON.toJSONString(twitterOAuthV1UrlGetRes));
        return twitterOAuthV1UrlGetRes;
    }

    @Async
    protected void sendTwitterFollowMessage(TwitterFollowEeventsDto.FollowEventsBean.TargetBean target,
                                          TwitterFollowEeventsDto.FollowEventsBean.SourceBean source, Date followTime) {
        String tragetUserId = target.getId();
        String sourceUserId = source.getId();
        String sourceUserName = source.getName();

        TwitterFollowMessageDto twitterFollowMessageDto = new TwitterFollowMessageDto();
        twitterFollowMessageDto.setFollowTime(followTime);
        twitterFollowMessageDto.setTragetTwitterUserId(tragetUserId);
        twitterFollowMessageDto.setSourceTwitterUserId(sourceUserId);
        twitterFollowMessageDto.setSourceTwitterUserName(sourceUserName);
        producerService.twitterFollowMessageQueue(JSON.toJSONString(twitterFollowMessageDto));
    }

    public String getSignature(String secret,String crc_token) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] source = crc_token.getBytes("UTF-8");
        return Base64.encodeBase64String(mac.doFinal(source));
    }

}
