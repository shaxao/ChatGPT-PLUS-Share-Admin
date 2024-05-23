package com.louwei.gptresource.vo.admin.user;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 管理系统用户信息返回封装
 */
@Data
public class AdminUserReqVo implements Serializable {
    private Integer id;

    private String userName;

    private String userEmail;

    private Date createTime;

    private Date expireTime;

    private String creator;

    private String userStatus;

    /**
     * 用户等级，关联users_role uid
     */
    private Integer importance;
}
