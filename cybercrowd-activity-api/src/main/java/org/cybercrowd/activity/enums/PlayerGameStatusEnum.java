package org.cybercrowd.activity.enums;

public enum PlayerGameStatusEnum {

    GAME_STATUS_0("0","未参与"),
    GAME_STATUS_1("1","参与中"),
    GAME_STATUS_2("2","已结束"),

    ;

    private String code;
    private String name;

    PlayerGameStatusEnum(String code,String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
