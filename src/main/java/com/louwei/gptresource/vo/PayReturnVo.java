package com.louwei.gptresource.vo;

import lombok.Data;

@Data
public class PayReturnVo {
    private Integer pid;

    // 易支付订单号
    private String tradeNo;

    // 易支付订单号
    private String qianyiTradeNo;

    // 商户订单号
    private String outTradeNo;

    // 支付方式
    private String type;

    // 商品名称
    private String name;

    // 商品金额
    private String money;

    // 支付状态
    private String tradeStatus;

    // 业务扩展参数
    private String param;

    // 签名字符串
    private String sign;

    // 签名类型
    private String signType;
}
