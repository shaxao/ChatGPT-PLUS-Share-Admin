package com.louwei.gptresource.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 验证码实体类
 */
@Data
@AllArgsConstructor
public class EmailVo {
    private String phone;
    private String toUserEmail;
}
