package com.louwei.gptresource.vo.admin.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminInfoReqVo implements Serializable {

    private String avatar;

    private String roles;

    private String name;

}
