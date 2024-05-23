package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * 用户表
 * @TableName chat_users
 */
@TableName(value ="chat_users")
@Data
@ToString
public class ChatUsers implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer orderId;

    @TableField(value = "user_password")
    private String userPassword;

    private String userName;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码过期时间
     */
    private Date codeExpireTime;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     *
     */
    private String userPhone;

    /**
     * 用户状态 0 未验证 1 普通用户 2 会员
     */
    private String userStatus;

    /**
     * 用户状态 0 为删除 1 违规或者其他原因被删除
     */
    private Integer deleted;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    private Date expireTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 头像
     */
    private String avatar;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
