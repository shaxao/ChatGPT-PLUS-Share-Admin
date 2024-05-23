package com.louwei.gptresource.hanler;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LoginSuccessHandle implements AuthenticationSuccessHandler {
    private final ChatUsersMapper chatUsersMapper;

    @Autowired
    public LoginSuccessHandle(ChatUsersMapper chatUsersMapper) {
        this.chatUsersMapper = chatUsersMapper;
        log.info("ChatUsersMapper已经成功安装.");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("登录成功:" + request.getHeader("System-name") + "cookie:" + response.getHeader("X-CSRF-TOKEN"));
        // 对于后台管理系统和服务系统进行拆分
        if(StrUtil.isNotBlank(request.getHeader("System-name")) && request.getHeader("System-name").equals("man")){
            log.info("进入后台管理系统");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            System.out.println("用户名：" + username);
            try {
                ChatUsers chatUsers = chatUsersMapper.selectOne(new QueryWrapper<ChatUsers>().eq("user_name", username));
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\": true, \"message\": \"登陆成功\",\"code\": \"200\",\"status\": \"" + chatUsers.getUserStatus() + "\"}");
            }catch (Exception e){
                log.error("登录异常信息:" + e);
                e.printStackTrace();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\": error, \"message\": \"获取用户信息失败\",\"code\": \"500\",\"name\": \"" + userDetails.getUsername() + "\"}");
            }
        }else {
            // 拿到登录用户的信息
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println("用户名："+userDetails.getUsername());
            System.out.println("进入服务系统主页");
            //TODO 前端对于重定向不支持，需要更改一下，前端需要支持重定向或者后端返回json格式信息
            // 重定向到主页
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write("{\"success\": true, \"message\": \"登录成功\", \"redirect\": \"/product.html\"}");
            response.sendRedirect("/index.html");
        }
    }
}
