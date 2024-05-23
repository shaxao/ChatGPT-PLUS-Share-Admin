package com.louwei.gptresource.vo.admin.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminAccountReqVo implements Serializable {
    private Integer id;

    /**
     * 账号（邮箱）
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 过期日期
     */
    private Date expireTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 账号状态
     */
    private String status;

}
