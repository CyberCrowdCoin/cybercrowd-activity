package org.cybercrowd.activity.controller;

import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.constant.HttpCookieConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private StringRedisTemplate redisTemplate;


    public String getLoginOwnerId(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String activityTokenKey = request.getHeader(HttpCookieConstants.LOGIN_SESSION_ID);
        if (cookies == null && StringUtils.isEmpty(activityTokenKey)) {
            logger.info("获取登录OwnerId,Cookis或者头信息 null");
            return null;
        }
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (HttpCookieConstants.LOGIN_SESSION_ID.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    String ownerId = redisTemplate.opsForValue().get(value);
                    if (StringUtils.isEmpty(ownerId)) {
                        logger.info("获取登录OwnerId, Redis 已过期,OwnerId获取失败");
                        return null;
                    }
                    return ownerId;
                }
            }
        }

        if(!StringUtils.isEmpty(activityTokenKey)) {
            String ownerId = redisTemplate.opsForValue().get(activityTokenKey);
            if (!StringUtils.isEmpty(ownerId)) {
                return ownerId;
            }
        }
        return null;
    }


}
