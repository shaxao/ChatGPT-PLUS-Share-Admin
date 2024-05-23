package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName chat_url
 */
@TableName(value ="chat_url")
@Data
public class ChatUrl implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String routePath;

    /**
     * 
     */
    private String pageDescription;

    /**
     * 
     */
    private String routeStatus;

    /**
     * 
     */
    private Integer parentMenu;

    /**
     * 
     */
    private Integer childMenu;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}