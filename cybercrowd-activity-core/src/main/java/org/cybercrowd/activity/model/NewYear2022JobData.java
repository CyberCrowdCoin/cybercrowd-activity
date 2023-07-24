package org.cybercrowd.activity.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor//生成无参构造
@AllArgsConstructor//生成有参构造
public class NewYear2022JobData implements Serializable {

    @ExcelProperty("账号类型")
    private String accountType;
    @ExcelProperty("用户ID")
    private String userId;
    @ExcelProperty("用户名称")
    private String userName;
    @ExcelProperty("邀请数量")
    private int inviterNumber;
    @ExcelProperty("钱包地址")
    private String content;
}
