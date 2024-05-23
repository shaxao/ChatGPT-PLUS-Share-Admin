package com.louwei.gptresource.vo;

import lombok.Data;

import java.util.Date;

/**
 * 账号以及Token的数据交互
 */
@Data
public class AccountTempVo {

    private Integer id;

    private String name;

    private String creator;

    private String email;

    private Boolean isPlus;

    private Boolean isShowInto;

    private Boolean isShare;

    private String password;

    private Date createTime;

    private Date expireTime;

    private String status;
}
