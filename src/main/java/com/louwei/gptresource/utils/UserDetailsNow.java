package com.louwei.gptresource.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsNow {
    public static String getUsername(){
        // 1.获取会话对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 2.获取认证对象
        Authentication authentication = context.getAuthentication();
        // 3.获取登录用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("获取登录用户信息：" + userDetails.getUsername());
        String username = userDetails.getUsername();
        return username;
    }
}
