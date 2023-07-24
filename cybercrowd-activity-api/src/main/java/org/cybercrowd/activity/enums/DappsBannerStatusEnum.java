package org.cybercrowd.activity.enums;

public enum DappsBannerStatusEnum {

    STATUS_0("0","已下架"),
    STATUS_1("1","已上架"),
    ;

    private String code;
    private String name;

    DappsBannerStatusEnum(String code,String name){
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
