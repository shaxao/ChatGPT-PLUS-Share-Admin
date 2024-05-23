package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 用户账号表
 * @TableName chat_account
 */
@TableName(value ="chat_account")
@Data
public class ChatAccount implements Serializable {
    /**
     * 账号ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String name;

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

    /**
     *
     */
    private Long sid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
