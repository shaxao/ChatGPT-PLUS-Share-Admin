package com.louwei.gptresource.vo.admin.user;

import lombok.Data;

import java.util.Date;

@Data
public class AdminProductReqVo {

    private Integer id;

    private Date createTime;

    private String title;

    private String imageUrl;

    private String creator;

    private Integer price;

    private Integer stock;

    private String proStatus;

    private String description;
}
