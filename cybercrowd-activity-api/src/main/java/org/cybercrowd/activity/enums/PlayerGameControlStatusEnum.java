package org.cybercrowd.activity.enums;

public enum PlayerGameControlStatusEnum {

    GAME_STATUS_0("0","已开始"),
    GAME_STATUS_1("1","开奖中"),
    GAME_STATUS_2("2","已开奖"),

    ;

    private String code;
    private String name;

    PlayerGameControlStatusEnum(String code, String name){
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
