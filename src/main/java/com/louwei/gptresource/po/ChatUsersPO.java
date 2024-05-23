package com.louwei.gptresource.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "用户表")
public class ChatUsersPO {

    @ApiModelProperty(value = "主键，自增")
    @TableId
    private Integer id;

    /**
     * 订单ID，和订单表关联
     */
    @TableField(value = "order_id")
    private Integer orderId;

    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @TableField(value = "user_email")
    private String userEmail;

    /**
     * 用户手机
     */
    @TableField(value = "user_phone")
    private String userPhone;

    @ApiModelProperty(value = "用户状态 0 未验证 1 未购买 2 购买",example = "0")
    @TableField(value = "user_status")
    private Integer userStatus;

    @ApiModelProperty(value = "用户状态 0 为删除 1 违规或者其他原因被删除",example = "0")
    private Integer deleted;

    private Date createTime;

    private String userName;

    private Date updateTime;

}
