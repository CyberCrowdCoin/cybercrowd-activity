package org.cybercrowd.activity.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.twitter.clientlib.model.Get2ListsIdFollowersResponseMeta;
import com.twitter.clientlib.model.Get2UsersIdFollowersResponse;
import com.twitter.clientlib.model.Problem;
import com.twitter.clientlib.model.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.cybercrowd.activity.dto.TwitterFollowMessageDto;
import org.cybercrowd.activity.mapper.TwitterFollowersMapper;
import org.cybercrowd.activity.model.TwitterFollowers;
import org.cybercrowd.activity.service.ActivityService;
import org.cybercrowd.activity.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("twitterService")
public class TwitterServiceImpl implements TwitterService {

    private Logger logger = LoggerFactory.getLogger(TwitterServiceImpl.class);

    @Autowired
    ActivityService activityService;

    @Autowired
    TwitterFollowersMapper twitterFollowersMapper;

    @Override
    public void pullTwitterFollowUser(int fileNumber) {
        loadLocalFileSaveFollowers(fileNumber);
    }

    @Async
    @Override
    public void pullTwitterFollowUserList(String bearerToken,String paginationToken,Map<String, List<String>> headers, int i){
        try{
            String url = "https://api.twitter.com/2/users/1555116916145999872/followers?max_results=1000";
            if(!StringUtils.isEmpty(paginationToken)){
                url = MessageFormat.format("{0}{1}{2}",url,"&pagination_token=",paginationToken);
            }

            HttpRequest httpRequest = HttpUtil.createRequest(Method.GET, url);
            if(null != headers){
                httpRequest.header(headers);
            }
            httpRequest.header("user-agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
            httpRequest.header("Authorization", "Bearer "+bearerToken);

            HttpResponse httpResponse = httpRequest.execute();
            int status = httpResponse.getStatus();
            String body = httpResponse.body();
            if(200 == status){
                headers = httpResponse.headers();
                Get2UsersIdFollowersResponse followersResponse = JSON.parseObject(body,Get2UsersIdFollowersResponse.class);
                JSONObject jsonObject = JSONObject.parseObject(body);
                JSONObject meta = (JSONObject) jsonObject.get("meta");

                List<Problem> errors = followersResponse.getErrors();
                List<User> followersList = followersResponse.getData();

                if(null != followersList && followersList.size() > 0){
                    logger.info("Twitter 关注用户列表拉取成功,第 {} 页",i);
                    long start = System.currentTimeMillis();
                    logger.info("twitter 关注用户列表处理数据开始时间:{} ",start);
                    //处理数据
                    updateOrSaveTwitterFollowers(followersList);
                    long end = System.currentTimeMillis();
                    logger.info("twitter 关注用户列表处理数据耗时:{} ",(end-start));

                    if ( null != meta) {
                        String nextToken = (String)meta.get("next_token");
                        //如果 nextToken 不为空,那么就再次执行获取数据
                        if (!StringUtils.isEmpty(nextToken)) {
                            long time = 1000*70;
                            if ((end-start) < time) {
                                Thread.sleep((time - (end-start)));
                                logger.info("twitter 关注用户列表拉取线程休眠....");
                            }
                            i++;
                            this.pullTwitterFollowUserList(bearerToken,nextToken,headers,i);
                        }
                    }
                }else {
                    logger.info("Twitter 关注用户列表拉取失败,code{} message:{}",status,JSON.toJSONString(errors));
                }

            }else {
                logger.info("Twitter 关注用户列表拉取失败,code{} message:{}",status,body);
            }
        }catch (Exception e){
            logger.error("Twitter 关注用户列表拉取异常:{}",e.getMessage(),e);
        }

    }


    @Override
    public boolean twitterFollowHandle(TwitterFollowMessageDto twitterFollowMessageDto) {
        String sourceTwitterUserId = twitterFollowMessageDto.getSourceTwitterUserId();
        //更新或者保存关注数据
        TwitterFollowers twitterFollowersReq = new TwitterFollowers();
        twitterFollowersReq.setTwitterId(sourceTwitterUserId);
        TwitterFollowers twitterFollowers = twitterFollowersMapper.selectOne(twitterFollowersReq);
        updateOrSaveTwitterFollowers(twitterFollowers,twitterFollowMessageDto);
        //2022新年活动,关注twitter成功更新任务状态
        activityService.updateActivity2022NewYearTwitterJobStatus(sourceTwitterUserId);
        return true;
    }

    @Async
    protected void loadLocalFileSaveFollowers(int fileNumber){

        for(int i=1;i<=fileNumber;i++){
            FileInputStream fileInputStream = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                File file = new File("/data/twitter_followers_file/" + i + ".json");
//                File file = new File("/Users/gengchaonan/Downloads/followers/" + i + ".json");
                logger.info("twitter关注列表获取接口,文件路径:{}",file.getPath());

                fileInputStream = new FileInputStream(file);
                isr = new InputStreamReader(fileInputStream, "utf-8");
                br = new BufferedReader(isr);
                StringBuffer jsonStr = new StringBuffer();
                String line;
                //将字符缓冲流读到的每行赋值给line字符串
                while ((line = br.readLine()) != null) {
                    jsonStr.append(line);
                }

                jsonStr.toString();

                Get2UsersIdFollowersResponse result = JSON.parseObject(jsonStr.toString(), Get2UsersIdFollowersResponse.class);

                List<User> followersList = result.getData();
                logger.info("twitter关注列表获取接口,响应数据:{}", JSON.toJSONString(followersList));
                long start = System.currentTimeMillis();
                logger.info("twitter 处理数据开始时间:{} ", start);
                if (null != followersList && followersList.size() > 0) {
                    for (User user : followersList) {
                        TwitterFollowers twitterFollowersReq = new TwitterFollowers();
                        twitterFollowersReq.setTwitterId(user.getId());
                        TwitterFollowers twitterFollowers = twitterFollowersMapper.selectOne(twitterFollowersReq);
                        if (null != twitterFollowers) {
                            twitterFollowers.setTwitterId(user.getId());
                            twitterFollowers.setTwitterName(user.getName());
                            twitterFollowers.setTwitterUserName(user.getUsername());
                            twitterFollowers.setFollowStatus("1");
                            twitterFollowers.setUpdateTime(new Date());
                            twitterFollowersMapper.updateByPrimaryKeySelective(twitterFollowers);
                        } else {
                            twitterFollowers = new TwitterFollowers();
                            twitterFollowers.setTwitterId(user.getId());
                            twitterFollowers.setTwitterName(user.getName());
                            twitterFollowers.setTwitterUserName(user.getUsername());
                            twitterFollowers.setCreateTime(new Date());
                            twitterFollowers.setUpdateTime(new Date());
                            twitterFollowers.setFollowStatus("1");
                            twitterFollowersMapper.insertSelective(twitterFollowers);
                        }

                    }
                }
            }catch (Exception e){
                logger.error("twitter关注列表获取接口,执行异常:{}",e.getMessage(),e);
            }finally {
                try {
                    if (null != br) {
                        br.close();
                    }
                    if (null != isr) {
                        isr.close();
                    }
                    if (null != fileInputStream) {
                        fileInputStream.close();
                    }
                }catch (Exception e){
                    logger.error("twitter关注列表获取接口,文件流关闭异常:{}",e.getMessage(),e);
                }

            }
        }
    }

    private void updateOrSaveTwitterFollowers(TwitterFollowers twitterFollowers,
                                              TwitterFollowMessageDto twitterFollowMessageDto) {
        if(null == twitterFollowers){
            twitterFollowers = new TwitterFollowers();
            twitterFollowers.setTwitterId(twitterFollowMessageDto.getSourceTwitterUserId());
            twitterFollowers.setTwitterName(twitterFollowMessageDto.getSourceTwitterUserName());
            twitterFollowers.setTwitterUserName(twitterFollowMessageDto.getSourceTwitterUserName());
            twitterFollowers.setFollowStatus("1");
            twitterFollowers.setCreateTime(new Date());
            twitterFollowers.setUpdateTime(new Date());
            twitterFollowersMapper.insertSelective(twitterFollowers);
        }else{
            twitterFollowers.setFollowStatus("1");
            twitterFollowers.setUpdateTime(new Date());
            twitterFollowersMapper.updateByPrimaryKeySelective(twitterFollowers);
        }
    }

    private void updateOrSaveTwitterFollowers(List<User> followersList) {
        for(User user:followersList) {
            TwitterFollowers twitterFollowersReq = new TwitterFollowers();
            twitterFollowersReq.setTwitterId(user.getId());
            TwitterFollowers twitterFollowers = twitterFollowersMapper.selectOne(twitterFollowersReq);
            if (null != twitterFollowers) {
                twitterFollowers.setTwitterId(user.getId());
                twitterFollowers.setTwitterName(user.getName());
                twitterFollowers.setTwitterUserName(user.getUsername());
                twitterFollowers.setFollowStatus("1");
                twitterFollowers.setUpdateTime(new Date());
                twitterFollowersMapper.updateByPrimaryKeySelective(twitterFollowers);
            } else {
                twitterFollowers = new TwitterFollowers();
                twitterFollowers.setTwitterId(user.getId());
                twitterFollowers.setTwitterName(user.getName());
                twitterFollowers.setTwitterUserName(user.getUsername());
                twitterFollowers.setFollowStatus("1");
                twitterFollowers.setCreateTime(new Date());
                twitterFollowers.setUpdateTime(new Date());
                twitterFollowersMapper.insertSelective(twitterFollowers);
            }
        }
    }

    private String getSignature(String secret,String crc_token) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] source = crc_token.getBytes("UTF-8");
        return Base64.encodeBase64String(mac.doFinal(source));
    }
}
