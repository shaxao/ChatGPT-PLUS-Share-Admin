package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName chat_permission
 */
@TableName(value ="chat_permission")
@Data
public class ChatPermission implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer pid;

    /**
     * 
     */
    private String permissionname;

    /**
     * 
     */
    private String url;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}