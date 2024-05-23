package com.louwei.gptresource.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UsersRegisterVo implements Serializable {
    private String username;
    private String password;
    private String code;
    private String phoneOrEmail;
}
