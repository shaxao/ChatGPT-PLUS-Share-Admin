package com.louwei.gptresource.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ChatOrderRepVo {

    private String tradeNo;

    private String productName;

    /**
     * 创建时间
     */
    private Date createTime;

    private BigDecimal price;

    private String orderStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
