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
 * @TableName chat_payment
 */
@TableName(value ="chat_payment")
@Data
public class ChatPayment implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 支付手段 跳转 扫码
     */
    private String payWay;

    /**
     * 支付名称
     */
    private String paymentName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户KEY
     */
    private String merchantKey;

    /**
     * 商户密钥
     */
    private String merchantSecret;

    /**
     * 支付场景
     */
    private String paymentScenario;

    /**
     * 支付处理路由
     */
    private String paymentHandlerRoute;

    /**
     * 是否启用
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
