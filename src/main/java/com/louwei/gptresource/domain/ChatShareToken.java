package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 聊天会话Token表
 * @TableName chat_share_token
 */
@TableName(value ="chat_share_token")
@Data
public class ChatShareToken implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 绑定access_token
     */
    private Long aid;

    /**
     * 邮箱，关联 chat_users 表的 chat_email 列
     */
    private String email;

    /**
     * token
     */
    private String token;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 过期日期
     */
    private Date expireDate;

    /**
     * 创建人，默认 admin
     */
    private String creator;

    /**
     * token状态
     */
    private String tokenStatus;

    /**
     * 使用人数
     */
    private Integer userCount;

//    /**
//     * 网站密码
//     */
//    private String sitePassword;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
