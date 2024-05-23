package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName chat_notice
 */
@TableName(value ="chat_notice")
@Data
public class ChatNotice implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 公告内容
     */
    private String message;

    /**
     * 是否启用
     */
    private Integer isEnabled;

    /**
     * 创建日期
     */
    private Date createdAt;

    /**
     * 更新日期
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
