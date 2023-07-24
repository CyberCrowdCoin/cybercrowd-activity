package org.cybercrowd.activity.dto;

import lombok.Data;
import org.cybercrowd.activity.enums.ReturnCodeEnum;

import java.io.Serializable;

@Data
public class BaseResponse implements Serializable {

    private String code = "SUCCESS";
    private String message = "Successful";

    public void setReturnCodeEnum(ReturnCodeEnum returnCodeEnum){
        this.code = returnCodeEnum.getCode();
        this.message = returnCodeEnum.getMessage();
    }
}
