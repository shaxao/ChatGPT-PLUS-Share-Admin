package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName chat_role
 */
@TableName(value ="chat_role")
@Data
public class ChatRole implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer rid;

    /**
     * 
     */
    private String rolename;

    /**
     * 
     */
    private String roledesc;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}