package org.cybercrowd.activity.enums;

public enum CommunityBotInteractiveTypeEnum {

    VOTE("VOTE","投票"),
    QA("QA","QA"),

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

    CommunityBotInteractiveTypeEnum(String code,String message){
        this.code = code;
        this.message = message;
    }
}
