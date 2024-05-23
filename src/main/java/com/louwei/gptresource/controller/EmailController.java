package com.louwei.gptresource.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.service.ChatOrdersService;
import com.louwei.gptresource.service.ChatShareTokenService;
import com.louwei.gptresource.service.ChatUsersService;
import com.louwei.gptresource.vo.EmailVo;
import com.louwei.gptresource.pojo.MailMessages;
import com.louwei.gptresource.utils.VerifyCodeUtils;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private MailMessages mailMessages;
    @Autowired
    private ChatUsersService chatUsersService;
    @Autowired
    private ChatShareTokenService chatShareTokenService;
    @Autowired
    private ChatOrdersService chatOrdersService;


    /**
     * 发送验证码
     * @param emailVo
     * @return
     */
    @GetMapping("/code")
    public AjaxResult getEmailCode(EmailVo emailVo){
        System.out.println("执行发送验证码程序:" + emailVo.getToUserEmail());
        ChatUsers chatUsers;
        QueryWrapper<ChatUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("user_email",emailVo.getToUserEmail());
        chatUsers = chatUsersService.getOne(wrapper);
        Date date = new Date();
        if(chatUsers != null && date.compareTo(chatUsers.getCodeExpireTime()) < 0){
            return AjaxResult.fail("请不要频繁发送验证码");
        }else if(chatUsers != null && date.compareTo(chatUsers.getCodeExpireTime()) > 0){
            String code = VerifyCodeUtils.createCode();
            chatUsers.setCode(code);
            Date tenLater = DateUtil.offsetMinute(date,10);
            chatUsers.setCodeExpireTime(tenLater);
            mailMessages.createMessages(code,emailVo.getToUserEmail());
            boolean send = mailMessages.send();
            boolean b = chatUsersService.updateById(chatUsers);
            if(send && b){
                return AjaxResult.success();
            }else {
                return AjaxResult.fail();
            }
        }
        String code = VerifyCodeUtils.createCode();
        System.out.println("生成验证码:" + code);
        // 验证码发送录入数据库，前端完善请求，实体类创建，验证码验证
        mailMessages.createMessages(code,emailVo.getToUserEmail());

        boolean success = mailMessages.send();
        if(success){
            int usersByMail = chatUsersService.createUsersByMail(emailVo.getToUserEmail(), code);
            System.out.println("usersByMail:" + usersByMail);
            return AjaxResult.toAjax(usersByMail);
        }else {
            return AjaxResult.fail();
        }
    }

    @GetMapping("/sendToken")
    public AjaxResult sendToken(@RequestParam("userId")Integer userId,@RequestParam("id")Long id){
        //从数据库获取token并发送到用户的邮箱中
        System.out.println("执行Token发送程序:" + id);
        ChatUsers user = chatUsersService.getOne(new QueryWrapper<ChatUsers>().eq("id", userId));
        String tokenPassword = chatShareTokenService.findTokenUse();
        //刷新订单的次数
        boolean success = chatOrdersService.refrshCountById(id);
        if (!success){
            return AjaxResult.fail("您的订单已过期或者次数已用完");
        }

        String subject = "【木火科技】账号邮件";
        String text = "尊贵的VIP用户，这是您的Token【" + tokenPassword + "】" +"请查收，有效期10天，每人每月有5次刷新机会，请保存好以防丢失";
        mailMessages.createMessagesBySelf(user.getUserEmail(),subject,text);
        boolean send = mailMessages.send();
        if (send){
            return AjaxResult.success("token发送成功，大概15分钟后送达，请勿重复刷新浪费次数");
        }else {
            // TODO 如果发送失败。重试，有点懒得写，有时间再写
            return AjaxResult.fail("token发送失败");
        }
    }

}
