package com.louwei.gptresource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {
    /**
     * 未支付
     */
    NOTPAY("未支付"),

    /**
     * 已过期
     */
    EXPIRE("已过期"),

    /**
     * 支付成功
     */
    SUCCESS("已支付"),

    /**
     * 已关闭
     */
    CLOSED("超时已关闭"),

    /**
     * 支付超时
     */
    TIMEOUT("支付超时"),

    /**
     * 已取消
     */
    CANCEL("用户已取消"),

    /**
     * 退款中
     */
    REFUND_PROCESSING("退款中"),

    /**
     * 已退款
     */
    REFUND_SUCCESS("已退款"),

    /**
     * 退款异常
     */
    REFUND_ABNORMAL("退款异常");

    /**
     * 类型
     */
    private final String type;
}
