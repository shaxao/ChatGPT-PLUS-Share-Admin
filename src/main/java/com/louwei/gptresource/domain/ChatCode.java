package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 聊天二维码表
 * @TableName chat_code
 */
@TableName(value ="chat_code")
@Data
public class ChatCode implements Serializable {
    /**
     * id主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 二维码链接
     */
    private String qrcodeLink;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 过期时间
     */
    private Date expireTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}