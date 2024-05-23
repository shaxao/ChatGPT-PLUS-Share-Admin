package com.louwei.gptresource.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChatProductDetailReqVo {
    private Integer id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 用户名
     */
    private String username;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 商品ID
     */
    private Integer productId;

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

}
