package com.louwei.gptresource.vo.admin.user;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class AdminShareSetVo {
    private Long id;
    private long expiry;
    private String name;
    private Integer gpt35Uses;
    private Integer gpt4Uses;
    private Boolean sessionIsolation;
    private String restrictedSites;
}
