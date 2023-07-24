package org.cybercrowd.activity.service;

import com.alibaba.fastjson2.JSON;
import org.cybercrowd.activity.constant.RedisCacheKeyConstants;
import org.cybercrowd.activity.mapper.ActivityTopicMapper;
import org.cybercrowd.activity.model.ActivityTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service
public class ActivityTopicService {

    @Autowired
    ActivityTopicMapper activityTopicMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    public boolean activityTopicStatus(String topic){
        String cacheValue = redisTemplate.opsForValue().get(
                MessageFormat.format(RedisCacheKeyConstants.ACTIVITY_TOPIC_STATUS_CACHE,topic));

        if(StringUtils.hasText(cacheValue)){
            return true;
        }else {
            ActivityTopic activityTopic =
                    activityTopicMapper.findActivityTopic(topic);
            if (null != activityTopic
                    && "1".equals(activityTopic.getStatus())) {
                redisTemplate.opsForValue().set(
                        MessageFormat.format(
                                RedisCacheKeyConstants.ACTIVITY_TOPIC_STATUS_CACHE,topic), JSON.toJSONString(activityTopic));
                return true;
            }
        }
        return false;
    }

    public void activityTopicExpireUpdate(){
        List<ActivityTopic> activityTopics = activityTopicMapper.selectActivityTopicExpireList();
        if(null != activityTopics && activityTopics.size() >0){
            for(ActivityTopic activityTopic:activityTopics){
                activityTopic.setStatus("0");
                activityTopic.setUpdateTime(new Date());
                activityTopicMapper.updateByPrimaryKeySelective(activityTopic);
                String topic = activityTopic.getTopic();
                //删除缓存
                redisTemplate.delete(MessageFormat.format(RedisCacheKeyConstants.ACTIVITY_TOPIC_STATUS_CACHE,topic));
            }
        }
    }
}
