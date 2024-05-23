package com.louwei.gptresource.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChatProductReqVo {

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
}
