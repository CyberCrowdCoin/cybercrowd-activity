package org.cybercrowd.activity.enums;

public enum ReturnCodeEnum {
    SUCCESS("SUCCESS","Successful"),
    FAIL("FAIL","System exception"),

    PARAMETER_ERROR("PARAMETER_ERROR","parameter error"),
    PLEASE_LOGIN("PLEASE_LOGIN", "please login to your account"),

    ACITVITY_ENDED_ERROR("ACITVITY_ENDED_ERROR","activity ended"),
    ACTIVITY_NOT_FOUND_ERROR("ACTIVITY_NOT_FOUND_ERROR","no relevant activity data found"),
    AUTHORIZED_LOGIN_EXCEPTION("AUTHORIZED_LOGIN_EXCEPTION","authorized login exception"),
    ABNORMAL_BUSINESS_DATA_EXCEPTION("ABNORMAL_BUSINESS_DATA_EXCEPTION","abnormal business data"),
    TWITTER_NOT_OAUTH_LOGIN_EXCEPTION("TWITTER_NOT_OAUTH_LOGIN_EXCEPTION","twitter is not authorized to login"),
    ACTIVITY_INVITE_CODE_ERROR("ACTIVITY_INVITE_CODE_ERROR","activity invitation code error"),
    ACTIVITY_TASK_NOT_COMPLETED_ERROR("ACTIVITY_TASK_NOT_COMPLETED_ERROR","activity task not completed"),
    DISCORE_ALREADY_BOUND_USER_ERROR("DISCORE_ALREADY_BOUND_USER_ERROR","discord is already bound by another user"),
    TWITTER_FOLLOW_TASK_NOT_COMPLETED_ERROR("TWITTER_FOLLOW_TASK_NOT_COMPLETED_ERROR","twitter Follow Task Not Completed"),

    PLAYER_PULL_DATA_ERROR("PLAYER_PULL_DATA_ERROR","player data pull failed"),
    PLAYER_ADDRESS_ERROR("PLAYER_ADDRESS_ERROR","player address error"),
    PLAYER_WINNING_ERROR("PLAYER_WINNING_ERROR","player winning data pull failed"),
    PLAYER_REGISTER_ERROR("PLAYER_REGISTER_ERROR","during the current lottery, player registration is suspended, please try again later"),


    ;

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ReturnCodeEnum(String code,String message){
        this.code = code;
        this.message = message;
    }
}
