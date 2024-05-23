package com.louwei.gptresource.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.print.attribute.standard.PrinterURI;
import java.util.Date;

@Data
public class ChatOrderReqVo {

    private Long id;

    private String productName;

    private Integer userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 过期时间
     */
    private Date expireTime;

    private double price;

    private String orderStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
