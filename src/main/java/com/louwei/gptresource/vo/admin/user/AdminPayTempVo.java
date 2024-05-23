package com.louwei.gptresource.vo.admin.user;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class AdminPayTempVo {
    private Integer id;

    // 支付名称，用于标识不同的支付方式
    private String paymentName;

    // 支付方式，例如：支付宝、微信支付等
    private String paymentMethod;

    // 支付方式的展示名称，用于用户界面显示
    private String payWay;

    // 商户ID，每个商户的唯一标识
    private String merchantId;

    // 商户密钥，用于支付接口的安全验证
    private String merchantKey;

    // 商户密钥，用于支付接口的安全验证
    private String merchantSecret;

    // 支付结果展示，决定支付成功后跳转的页面或逻辑
    private String paymentScenario;

    // 支付处理路由，指定处理支付请求的路径
    private String paymentHandlerRoute;

    // 支付是否启用，用于控制支付方式的启用状态
    private Boolean isEnabled;

    // 创建时间，记录配置的创建时间
    private Date createdAt;

    // 更新时间，记录配置的最后更新时间
    private Date updatedAt;
}
