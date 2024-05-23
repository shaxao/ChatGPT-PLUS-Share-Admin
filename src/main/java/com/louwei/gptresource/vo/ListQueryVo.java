package com.louwei.gptresource.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Data
public class ListQueryVo {

    private Integer page;

    private Integer limit;

    private String userEmail;

    private String status;

    private String paymentName;

    /**
     * 时间戳  yy-mm-dd hh:mm:ss
     */
    private List<Date> createTime;

    private Integer importance;

    private String sort;

    private String priceSort;

    private String stockSort;

    /**
     * 商品标题
     */
    private String title;
}
