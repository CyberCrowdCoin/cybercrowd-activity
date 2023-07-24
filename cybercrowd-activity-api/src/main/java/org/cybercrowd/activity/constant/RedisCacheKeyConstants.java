package org.cybercrowd.activity.constant;

public class RedisCacheKeyConstants {

    /**
     * 应用名称
     */
    private final static String APP_NAME = "CyberCrowd";

    /**
     * 服务名称
     */
    private static final String CYBERPAY_CROWD_SERVER_NAME = ":ACTIVITY";

    /**
     * 缓存前缀
     */
    public static final String REDIS_CACHE_PREFIX = APP_NAME + CYBERPAY_CROWD_SERVER_NAME + ":CACHE";

    /**
     * 登录用户session
     */
    public static final String LOGIN_USER_SESSION_ID = APP_NAME + ":LOGIN_USER_SESSION_ID:";

    /**
     *
     */
    public static final String ACTIVITY_TOPIC_STATUS_CACHE = REDIS_CACHE_PREFIX + ":ACTIVITY_TOPIC_STATUS_CACHE:{0}";

    /**
     * twitter 关注拉取开关
     * 不存在数据或者数据是NULL 不执行拉取
     */
    public static final String TWITTER_FOLLOW_PULL_SWITCH = REDIS_CACHE_PREFIX + ":TWITTER_FOLLOW_PULL_SWITCH";

    /**
     * 用户活动任务更新锁
     */
    public static final String USER_ACTIVITY_JOB_UPDATE_LOCK_KEY = REDIS_CACHE_PREFIX + ":USER_ACTIVITY_JOB_UPDATE_LOCK_KEY:JOB_{0}";

    /**
     * 授权登录锁
     */
    public static final String OAUTH_LOGIN_LOCK_KEY = REDIS_CACHE_PREFIX + ":USER_ACTIVITY_JOB_UPDATE_KEY:JOB_{0}";

    /**
     * 社区机器人交互命令列表缓存
     */
    public static final String COMMUNITY_BOT_INTERACTIVE_COMMAND_CACHE = REDIS_CACHE_PREFIX + ":COMMUNITY_BOT_INTERACTIVE_COMMAND";

    /**
     * 社区机器人交互异常回复缓存
     */
    public static final String COMMUNITY_BOT_INTERACTIVE_EXCEPTION_CACHE = REDIS_CACHE_PREFIX + ":COMMUNITY_BOT_INTERACTIVE_EXCEPTION:{0}:{1}";
    /**
     * 社群机器人私信内容
     */
    public static final String COMMUNITY_BOT_PRIVATE_MESSAGE_CACHE = REDIS_CACHE_PREFIX + ":COMMUNITY_BOT_PRIVATE_MESSAGE";

    /**
     * 社群机器人私信内容 Telegram
     */
    public static final String COMMUNITY_BOT_PRIVATE_MESSAGE_TELEGRAM_CACHE = REDIS_CACHE_PREFIX + ":COMMUNITY_BOT_PRIVATE_MESSAGE:Telegram";

    /**
     * 社群机器人私信内容 Discord
     */
    public static final String COMMUNITY_BOT_PRIVATE_MESSAGE_DISCORD_CACHE = REDIS_CACHE_PREFIX + ":COMMUNITY_BOT_PRIVATE_MESSAGE:Discord";

    /**
     * Telegram 群组ID配置缓存
     */
    public static final String TELEGRAM_GROUP_ID_LIST_CACHE = REDIS_CACHE_PREFIX + ":TELEGRAM_GROUP_ID_LIST";

    /**
     * 玩家注册锁
     */
    public static final String PLAYER_REGISTER_LOCK = REDIS_CACHE_PREFIX + ":PLAYER_REGISTER_LOCK:{0}";

    /**
     * 彩票开奖时间
     */
    public static final String PLAYER_DRAW_LOTTERY_TIME_CACHE = REDIS_CACHE_PREFIX + ":PLAYER_DRAW_LOTTERY_TIME_CACHE";

    /**
     * 玩家数据同步标签缓存
     */
    public static final String PLAYER_SYNC_TAG_CACHE = REDIS_CACHE_PREFIX + ":PLAYER_SYNC_TAG_CACHE";

}
