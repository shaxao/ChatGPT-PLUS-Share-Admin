package com.louwei.gptresource.vo.admin.user;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

@Data
public class AdminOrderReqVo {

    private String orderNo;

    private String title;

    private Date createTime;

    private String creator;

    private String userEmail;

    private Integer totalFee;

    private String orderStatus;
}
