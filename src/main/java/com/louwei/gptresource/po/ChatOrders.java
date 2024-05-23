package com.louwei.gptresource.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "用户订单表")
@TableName("chat_Orders")
public class ChatOrders {

    @ApiModelProperty(value = "主键，自增")
    @TableId
    private Integer id;

    @ApiModelProperty(value = "过期时间")
    @TableField(value = "expiration_time")
    private Date expirationTime;

    private Date createTime;

    /**
     * 目前由于支付限制，会在前台返回一个二维码添加好友后后台手动操作订单
     */
    @ApiModelProperty(value = "订单状态 0 未支付  1 支付成功 2 支付失败")
    @TableField(value = "order_status")
    private Integer orderStatus;

}
