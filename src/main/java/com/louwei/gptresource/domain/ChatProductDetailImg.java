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
 * @TableName chat_product_detail_img
 */
@TableName(value ="chat_product_detail_img")
@Data
public class ChatProductDetailImg implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer productId;

    /**
     *
     */
    private String imageUrl;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private String creator;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
