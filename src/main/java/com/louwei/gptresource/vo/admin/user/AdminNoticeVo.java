package com.louwei.gptresource.vo.admin.user;


import lombok.Data;

@Data
public class AdminNoticeVo {
    private Integer id;

    private String message;

    private Integer isEnabled;
}
