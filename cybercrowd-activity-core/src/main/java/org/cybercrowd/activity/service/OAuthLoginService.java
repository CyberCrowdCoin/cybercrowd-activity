package org.cybercrowd.activity.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.dto.DiscordAccountDto;
import org.cybercrowd.activity.dto.DiscordOAuthTokenDto;
import org.cybercrowd.activity.dto.TwitterAccountDto;
import org.cybercrowd.activity.dto.TwitterOauthTokenResDto;
import org.cybercrowd.activity.enums.ReturnCodeEnum;
import org.cybercrowd.activity.response.TwitterOAuthV1UrlGetRes;
import org.cybercrowd.activity.utils.HttpClientUtil;
import org.cybercrowd.activity.utils.HttpRequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthLoginService {

    private Logger logger = LoggerFactory.getLogger(OAuthLoginService.class);

    @Value("${twitter.oauth_v2.token_url}")
    private String twitterOauthV2TokenUrl;
    @Value("${twitter.oauth_v2.client_id}")
    private String twitterOauthV2ClientId;
    @Value("${twitter.oauth_v2.request_user_url}")
    private String twitterOauthV2RequestUserUrl;

    @Value("${twitter.oauth_v1.client_secret}")
    private String twitterOauthV1ClientSecret;
    @Value("${twitter.oauth_v1.client_key}")
    private String twitterOauthV1ClientKey;
    @Value("${twitter.oauth_v1.request_token_url}")
    private String twitterOauthV1ReuestTokenUrl;
    @Value("${twitter.oauth_v1.authorization_url}")
    private String twitterOauthV1AuthorizationUrl;

    @Value("${twitter.oauth_v1.request_access_token_url}")
    private String twitterOauthV1RequestAccessTokenUrl;
    @Value("${twitter.oauth_v1.request_user_url}")
    private String twitterOauthV1RequestUserUrl;


    @Value("${discord.client.id}")
    private String discordClientId;
    @Value("${discord.client.secret}")
    private String discordClientSecret;
    @Value("${discord.oauth_token_url}")
    private String discordOauthTokenUrl;
    @Value("${discord.query_user_url}")
    private String discordOauthUserUrl;
    @Value("${discord.authorization_basic}")
    private String discordAuthorizationBasic;

    @Value("${discord.user_avatar}")
    private String discordUserAvatarUrl;


    public TwitterAccountDto loginTwitterV2(String oauthCode, String redirectUri){
        TwitterAccountDto twitterAccountDto = new TwitterAccountDto();
        //获取oauth token
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("code",oauthCode);
        paramMap.put("grant_type","authorization_code");
        paramMap.put("client_id",twitterOauthV2ClientId);
        paramMap.put("redirect_uri",redirectUri);
        paramMap.put("code_verifier","challenge");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Basic RXV4ZlFRcVZYaUIzVDhuYld2NTlmVVhIZDpLR3ptMjNqVlFQNVk0NkhxRkZjRkhhTmxwME5TcVZ0cWZMZjRrazMzWFFSNmhocE1UMg==");

        String oauthTokenResult = HttpClientUtil.postWithParamsForString(twitterOauthV2TokenUrl,paramMap,headerMap);
        if(StringUtils.isEmpty(oauthTokenResult)){
            logger.info("获取Twitter第三方用户信息,OAUTH Token获取失败");
            return null;
        }

        TwitterOauthTokenResDto twitterOauthTokenResDto = JSON.parseObject(oauthTokenResult, TwitterOauthTokenResDto.class);
        headerMap.put("Authorization","Bearer "+ twitterOauthTokenResDto.getAccess_token());

        String result = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet, twitterOauthV2RequestUserUrl, null, headerMap);
        if(StringUtils.isEmpty(result)){
            logger.info("获取Twitter第三方用户信息失败");
            return null;
        }

        twitterAccountDto = JSON.parseObject(result, TwitterAccountDto.class);
        logger.info("获取Twitter第三方用户信息,响应结果:{}",JSON.toJSONString(twitterAccountDto));
        return twitterAccountDto;
    }

    public DiscordAccountDto loginDiscord(String oauthCode, String redirectUri) {
        //获取access token
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("code",oauthCode);
        paramMap.put("grant_type","authorization_code");
        paramMap.put("client_id",discordClientId);
        paramMap.put("redirect_uri",redirectUri);
        paramMap.put("client_secret",discordClientSecret);
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Basic "+discordAuthorizationBasic);
        try {
            String oauthTokenResult = HttpClientUtil.postWithParamsForString(discordOauthTokenUrl, paramMap, headerMap);
            if (StringUtils.isEmpty(oauthTokenResult)) {
                logger.info("获取Discord第三方用户信息,OAUTH Token获取失败");
                return null;
            }

            DiscordOAuthTokenDto discordOAuthTokenDto = JSON.parseObject(oauthTokenResult, DiscordOAuthTokenDto.class);
            String access_token = discordOAuthTokenDto.getAccess_token();
            if (StringUtils.isEmpty(access_token)) {
                logger.info("获取Discord第三方用户信息,OAUTH Token获取失败");
                return null;
            }

            headerMap.put("Authorization", "Bearer " + access_token);
            String result = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet, discordOauthUserUrl, null, headerMap);
            if (StringUtils.isEmpty(result)) {
                logger.info("获取Discord第三方用户信息失败");
                return null;
            }

            DiscordAccountDto discordAccountDto = JSON.parseObject(result, DiscordAccountDto.class);

            //头像
            if(!StringUtils.isEmpty(discordAccountDto.getAvatar())){
                discordAccountDto.setAvatar(MessageFormat.format(
                        discordUserAvatarUrl,discordAccountDto.getId(),discordAccountDto.getAvatar()));
            }
            logger.info("获取Discord第三方用户信息,响应结果:{}", JSON.toJSONString(discordAccountDto));
            return discordAccountDto;
        }catch (Exception e){
            logger.error("获取Discord第三方用户信息异常:{}",e.getMessage(),e);
            return null;
        }
    }

    public TwitterAccountDto loginTwitterV1(String oauthCode,String verifier){
        try {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("oauth_token", oauthCode);
            paramsMap.put("oauth_verifier", verifier);
            String result = HttpClientUtil.postWithParamsForString(twitterOauthV1RequestAccessTokenUrl, paramsMap, new HashMap<>());
            logger.info("Twitter OAuthV1 AccessToken获取结果: {}", result);
            if (StringUtils.isEmpty(result)) {
                logger.info("获取Twitter OAuthV1 第三方用户信息,OAUTH Token获取失败");
                return null;
            }

            String [] oauths = result.split("&");
            String oauthToken = oauths[0].split("=")[1];
            String oauthTokenSecret = oauths[1].split("=")[1];

            OAuthHmacSigner signer = new OAuthHmacSigner();

            signer.clientSharedSecret = twitterOauthV1ClientSecret;
            signer.tokenSharedSecret = oauthTokenSecret;

            OAuthGetTemporaryToken temporaryToken = new OAuthGetTemporaryToken(twitterOauthV1RequestUserUrl);
            temporaryToken.signer = signer;
            temporaryToken.consumerKey = twitterOauthV1ClientKey;

            OAuthParameters oAuthParameters = temporaryToken.createParameters();
            oAuthParameters.token = oauthToken;
            oAuthParameters.computeNonce();
            oAuthParameters.computeTimestamp();
            oAuthParameters.computeSignature("GET", temporaryToken);

            String authorizationHeader = oAuthParameters.getAuthorizationHeader();
            logger.info("authorizationHeader:{}", authorizationHeader);

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Authorization", authorizationHeader);

            String twitterUserInfoRes = HttpClientUtil.sendHttp(
                    HttpRequestMethod.HttpRequestMethodEnum.HttpGet,
                    twitterOauthV1RequestUserUrl,null, headerMap);
            if(StringUtils.isEmpty(twitterUserInfoRes)){
                logger.info("获取Twitter OAuthV1 登录,获取用户信息失败");
                return null;
            }

            JSONObject jsonObject = JSONObject.parseObject(twitterUserInfoRes);

            TwitterAccountDto twitterAccountDto = new TwitterAccountDto();
            TwitterAccountDto.DataBean dataBean = new TwitterAccountDto.DataBean();
            dataBean.setId(String.valueOf(jsonObject.get("id")));
            dataBean.setName((String) jsonObject.get("name"));
            dataBean.setUsername((String) jsonObject.get("screen_name"));
            dataBean.setProfile_image_url((String) jsonObject.get("profile_image_url_https"));
            twitterAccountDto.setData(dataBean);
            logger.info("Twitter OAuthV1 授权登录,获取用户信息的结果:{}",JSON.toJSONString(twitterAccountDto));
            return twitterAccountDto;
        }catch (Exception e){
            logger.info("Twitter OAuthV1 授权登录,异常:{}", e.getMessage(),e);
        }
        return null;
    }

    public TwitterOAuthV1UrlGetRes getTwitterOAuthV1AuthorizationUrl(String redirectUri){
        TwitterOAuthV1UrlGetRes twitterOAuthV1UrlGetRes = new TwitterOAuthV1UrlGetRes();
        try {
            OAuthHmacSigner signer = new OAuthHmacSigner();
            OAuthGetTemporaryToken temporaryToken = new OAuthGetTemporaryToken(twitterOauthV1ReuestTokenUrl);

            signer.clientSharedSecret = twitterOauthV1ClientSecret;
            temporaryToken.signer = signer;
            temporaryToken.consumerKey = twitterOauthV1ClientKey;
            temporaryToken.callback = redirectUri;

            OAuthParameters oAuthParameters = temporaryToken.createParameters();
            oAuthParameters.computeNonce();
            oAuthParameters.computeTimestamp();
            oAuthParameters.computeSignature("POST", temporaryToken);

            String authorizationHeader = oAuthParameters.getAuthorizationHeader();

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Authorization", authorizationHeader);
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("oauth_callback", redirectUri);
            String result = HttpClientUtil.postWithParamsForString(twitterOauthV1ReuestTokenUrl, paramsMap, headerMap);
            if(StringUtils.isEmpty(result)){
                logger.info("Twitter OAuthV1 获取认证地址,请求Twitter失败....");
                twitterOAuthV1UrlGetRes.setReturnCodeEnum(ReturnCodeEnum.FAIL);
            }else {
                String authorizationUrl = MessageFormat.format(twitterOauthV1AuthorizationUrl,result);
                logger.info("authorizationUrl:{}", authorizationUrl);
                twitterOAuthV1UrlGetRes.setAuthorizedUrl(authorizationUrl);
            }
        }catch (Exception e){
            logger.info("Twitter OAuthV1 获取认证地址,异常:{}",e.getMessage(),e);
            twitterOAuthV1UrlGetRes.setReturnCodeEnum(ReturnCodeEnum.FAIL);
        }
        return twitterOAuthV1UrlGetRes;
    }
}
