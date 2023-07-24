package org.cybercrowd.activity.enums;

public enum PlayerWinningStatusEnum {

    WINNING_STATUS_0("0","未中奖"),
    WINNING_STATUS_1("1","中奖"),

    ;

    private String code;
    private String name;

    PlayerWinningStatusEnum(String code, String name){
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
