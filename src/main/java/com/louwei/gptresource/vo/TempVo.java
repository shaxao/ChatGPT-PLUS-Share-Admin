package com.louwei.gptresource.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 后台管理数据更新封装类
 */
@Data
public class TempVo implements Serializable {

  private Integer id;
  private Date createTime;
  private Date expireTime;
  private Integer importance;
  private Date updateTime;
  private String userEmail;
  private String password;
  private String userName;
  private String creator;
  private String userStatus;

  //订单模块
  private Double totalFee;
  private String title;
  private String orderStatus;
  private String orderNo;
  private Integer quantity;

  //商品模块
  private Integer price;
  private Integer stock;
  private String imageLink;
  private List<String> desImageLink;
  private String description;
  private String proStatus;
}
