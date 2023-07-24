package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class BotReplyContentDto implements Serializable {

    //消息内容
    private String message;
    //文件ID
    private Map<String,String> fileIdMap;
    //文件路径
    private String filePath;

}
