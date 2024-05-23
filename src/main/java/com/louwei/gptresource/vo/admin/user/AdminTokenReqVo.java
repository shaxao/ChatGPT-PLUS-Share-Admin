package com.louwei.gptresource.vo.admin.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminTokenReqVo {
    private Long id;
    private String token;
    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 过期日期
     */
    private Date expireDate;

    private String creator;

    /**
     * token状态
     */
    private String tokenStatus;

    /**
     * 使用人数
     */
    private Integer userCount;

    private String email;
}
