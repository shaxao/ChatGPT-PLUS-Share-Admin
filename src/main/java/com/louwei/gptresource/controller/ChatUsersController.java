package com.louwei.gptresource.controller;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.service.ChatNoticeService;
import com.louwei.gptresource.service.ChatRoleService;
import com.louwei.gptresource.service.ChatUsersService;
import com.louwei.gptresource.vo.UsersRegisterVo;
import com.louwei.gptresource.vo.UsersResetVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Slf4j
public class ChatUsersController {

    @Autowired
    private ChatUsersService chatUsersService;
    @Autowired
    private ChatRoleService chatRoleService;
    @Autowired
    private ChatNoticeService chatNoticeService;

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principal) {
        log.info("github oauth验证通过，获取用户信息");
        principal.getAttributes().keySet().forEach(System.out::println);
        return "/index.html";
    }


    @GetMapping("/getNotice")
    public AjaxResult getNotice() {
        String message = chatNoticeService.getNotice();
        return AjaxResult.success("获取成功", message);
    }

    // 获取当前登录用户名
    @RequestMapping("/users/username")
    public String getUsername(){
        // 1.获取会话对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 2.获取认证对象
        Authentication authentication = context.getAuthentication();
        // 3.获取登录用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("获取登录用户信息：" + userDetails.getUsername());
        return userDetails.getUsername();
    }

    @GetMapping("/userName")
    public AjaxResult verifyUserName(@RequestParam String userName){
        System.out.println("执行用户名验证程序:" + userName);
        int result = chatUsersService.findByUserName(userName);
        return AjaxResult.toAjax(result);
    }

    @GetMapping("/phone")
    public AjaxResult verifyPhone(@RequestParam String phone){
        int result = chatUsersService.findByphone(phone);
        return AjaxResult.toAjax(result);
    }
    @GetMapping("/email")
    public AjaxResult verifyEmail(@RequestParam String email){
        System.out.println("执行邮箱验证程序:" + email);
        int result = chatUsersService.findByemail(email);
        return AjaxResult.toAjax(result);
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody UsersRegisterVo usersRegisterVo){
        System.out.println("开始处理注册请求");
        ChatUsers chatUsers = chatUsersService.findByEmail(usersRegisterVo.getPhoneOrEmail());
        if (chatUsers != null){
            if (!chatUsers.getCode().equals(usersRegisterVo.getCode())){
                return AjaxResult.fail("验证码错误");
            }
            Date now  = new Date();
            chatUsers.setCreateTime(now);
            chatUsers.setUserStatus(AllStatus.NORMALUSER.getType());
            chatUsers.setDeleted(0);
            chatUsers.setCodeExpireTime(now);
            //待完善注册逻辑  密码加密
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodePassword = passwordEncoder.encode(usersRegisterVo.getPassword());
            chatUsers.setUserName(usersRegisterVo.getUsername());
            chatUsers.setUserPassword(encodePassword);
            boolean success = chatUsersService.updateById(chatUsers);
            boolean a = chatRoleService.createRole(chatUsers.getId());
            if(success && a){
                return AjaxResult.success("注册成功");
            }else {
                return AjaxResult.fail("注册失败");
            }
        }else {
            return AjaxResult.fail("验证码与邮箱不符");
        }
    }


    @PostMapping("/resetPassword")
    public AjaxResult resetPassword(@RequestBody UsersResetVo usersResetVo){
        System.out.println("执行重置密码程序:"+ usersResetVo.getPhoneOrEmail());
        //验证验证码，正确修改密码
        ChatUsers chatUsers = chatUsersService.findByEmail(usersResetVo.getPhoneOrEmail());
        if(chatUsers != null){
            Date now  = new Date();
            if (now.compareTo(chatUsers.getCodeExpireTime()) < 0){
                return AjaxResult.fail("验证码过期");
            }
            if(!usersResetVo.getCode().equals(chatUsers.getCode())){
                return AjaxResult.fail("验证码有误");
            }
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodePassword = passwordEncoder.encode(usersResetVo.getPassword());
            chatUsers.setUserPassword(encodePassword);
            chatUsers.setCodeExpireTime(now);
            chatUsers.setUpdateTime(now);
            boolean success = chatUsersService.updateById(chatUsers);
            if(success){
                return AjaxResult.success("重置密码成功");
            }else {
                return AjaxResult.fail("数据库繁忙，请稍后再试");
            }
        }else {
            return AjaxResult.fail("用户不存在");
        }
    }
}
