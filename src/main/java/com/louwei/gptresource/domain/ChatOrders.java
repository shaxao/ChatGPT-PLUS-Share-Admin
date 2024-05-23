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
 * @TableName chat_orders
 */
@TableName(value ="chat_orders")
@Data
public class ChatOrders implements Serializable {
    /**
     * 订单id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付方式ID
     */
    private Integer payId;

    /**
     * 订单标题
     */
    private String title;

    /*
     * 商户订单编号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 订单金额(分)
     */
    private Integer totalFee;

    /**
     * 订单二维码连接
     */
    private String codeUrl;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 刷新次数
     */
    private Integer refreshCount;

    /**
     * 商品ID  关联商品ID
     */
    private Integer proId;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 支付平台返回订单号
     */
    private String tradeNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
