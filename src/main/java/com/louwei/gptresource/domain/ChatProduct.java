package com.louwei.gptresource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 商品详情表
 * @TableName chat_product
 */
@TableName(value ="chat_product")
@Data
public class ChatProduct implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品库存
     */
    private Integer stock;

    /**
     * 商品价格，显示两位小数
     */
    private BigDecimal price;

    /**
     * 商品图片地址
     */
    private String imageUrl;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品状态
     */
    private String proStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Integer chatOrderId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
