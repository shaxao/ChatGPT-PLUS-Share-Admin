package com.louwei.gptresource.controller.admin.user;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatAccount;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.domain.LoginRequest;
import com.louwei.gptresource.domain.Token;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.mapper.ChatAccountMapper;
import com.louwei.gptresource.service.ChatAccountService;
import com.louwei.gptresource.service.ChatNoticeService;
import com.louwei.gptresource.service.ChatUsersService;
import com.louwei.gptresource.service.impl.UserDetailsServiceImpl;
import com.louwei.gptresource.utils.JWTUtil;
import com.louwei.gptresource.vo.AccountTempVo;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.TempVo;
import com.louwei.gptresource.vo.admin.user.AdminAccountReqVo;
import com.louwei.gptresource.vo.admin.user.AdminInfoReqVo;
import com.louwei.gptresource.vo.admin.user.AdminNoticeVo;
import com.louwei.gptresource.vo.admin.user.AdminUserReqVo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.*;

@RestController
@RequestMapping("/vue-element-admin/user")
@Slf4j
public class AdminUserController {
    @Autowired
    private ChatUsersService chatUsersService;
    @Autowired
    private ChatAccountService chatAccountService;
    @Autowired
    private ChatAccountMapper chatAccountMapper;
    @Value("${pandora.config.file}")
    private String configFile;
    @Autowired
    private ChatNoticeService chatNoticeService;

    /**
     * 获取用户的信息（角色、描述、头像URL）
     * @param status
     * @return
     */
    @GetMapping("/info")
    public AjaxResult getInfo(@RequestParam(value = "status") @NonNull String status, HttpServletRequest request){
        System.out.println("后台管理系统获取用户信息 " + status);
        if(status.equals("admin")){
            String username = "";
            String token = request.getHeader("Authorization");
            log.info("请求中token={}", token);
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (JWTUtil.verify(jwtToken)) {
                     username = JWTUtil.getUsernameFromToken(jwtToken);
                    log.info("Username from token: {}", username);
                }
                ChatUsers user = chatUsersService.findUserByUserName(username);
                if(user == null){
                    return AjaxResult.fail("用户不存在");
                }
                AdminInfoReqVo adminUserReqVo = new AdminInfoReqVo();
                adminUserReqVo.setAvatar(user.getAvatar());
                adminUserReqVo.setName(user.getUserName());
                adminUserReqVo.setRoles("admin");
                return AjaxResult.success(adminUserReqVo);
            }
            return AjaxResult.fail("未获取到认证信息");
            // 1.获取会话对象
           // SecurityContext context = SecurityContextHolder.getContext();
            // 2.获取认证对象
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (authentication != null) {
//                Object principal = authentication.getPrincipal();
//                if (principal instanceof UserDetails) {
//                    UserDetails userDetails = (UserDetails) principal;
//                    username = userDetails.getUsername();
//                    log.info("Username from userDetails: {}", username);
//                    // 处理 UserDetails
//                } else {
//                    log.info("Principal type: {}", principal.getClass().getSimpleName());
//                    // 处理其他类型的 principal，如 String
//                    if (principal instanceof String) {
//                        // 可能只是用户名
//                        username = (String) principal;
//                        log.info("Username from principal: {}", username);
//                    }
//                }
            }
//            Authentication authentication = context.getAuthentication();
//            log.info("是否认证{}",authentication.isAuthenticated());
//            if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//                // 3.获取登录用户信息
//                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                String username = userDetails.getUsername();
//                ChatUsers user = chatUsersService.findUserByUserName(username);
//                if(user == null){
//                    return AjaxResult.fail("用户不存在");
//                }
//                AdminInfoReqVo adminUserReqVo = new AdminInfoReqVo();
//                adminUserReqVo.setAvatar(user.getAvatar());
//                adminUserReqVo.setName(user.getUserName());
//                adminUserReqVo.setRoles("admin");
//                return AjaxResult.success(adminUserReqVo);
//            }

        return AjaxResult.fail("非管理员无法获取");
    }

    /**
     * 创建账号，并且同步生成配置文件内容
     * @param accountTempVo
     * @return
     */
    @PostMapping("/account/create")
    public AjaxResult createAccount(@RequestBody AccountTempVo accountTempVo){
        System.out.println("开始执行后台管理创建账号数据模块:" + accountTempVo.getName());
        // 检查配置文件中是否已经存在用户名
//        File file = new File(configFile);
//        JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
//        Optional<Object> account = Optional.ofNullable(jsonObject.get(accountTempVo.getName()));
//        if(account.isEmpty()){
//            return AjaxResult.fail("账号已经存在，如果需要token请点击刷新");
//        }
        //如果不分享，那就只放入账号密码 创建配置文件账号数据
//        if(!accountTempVo.getIsShare()){
//            Map<String,Object> dataMap = new HashMap<>();
//            dataMap.put("username",accountTempVo.getName());
//            dataMap.put("userPassword",accountTempVo.getPassword());
//            dataMap.put("share",accountTempVo.getIsShare());
//            dataMap.put("show_user_info",accountTempVo.getIsShowInto());
//            dataMap.put("plus",accountTempVo.getIsPlus());
           // JSONObject dataJson = new JSONObject();
//dataJson.putAll(dataJson);
            //jsonObject.putOnce(accountTempVo.getName(),dataJson);
//            try {
//               // FileUtil.writeString(jsonObject.toStringPretty(),configFile,StandardCharsets.UTF_8);
//            }catch (Exception e){
//                e.printStackTrace();
//                log.error("账号配置文件创建失败" + e);
//                return AjaxResult.fail("账号配置文件创建失败，请检查用户名是否重复");
//            }
        //}
        // 否则配置文件需要更新token信息
        int result = chatAccountService.createAccount(accountTempVo);

        return AjaxResult.toAjax(result);
    }

    /**
     * 查询账号
     * @param listQueryVo
     * @return
     */
    @GetMapping("/account/list")
    public AjaxResult accountList(@ModelAttribute  @NonNull ListQueryVo listQueryVo){
        System.out.println("开始执行后台管理查询账号数据模块:" + listQueryVo.getPage());
        AjaxResult ajaxResult =  chatAccountService.selectAccountPage(listQueryVo);
        return ajaxResult;
    }

    /**
     * 更改账号数据  以及账号状态的更新
     * @return
     */
    @PostMapping("/account/update")
    public AjaxResult updateAccount(@RequestBody @NonNull AccountTempVo tempVo){
        System.out.println("开始执行后台管理更新账号数据模块:" + tempVo.getCreator());
        int result = chatAccountService.updateAccount(tempVo,configFile);
        System.out.println("用户更新模块账号更新结果：" + result);
        return AjaxResult.toAjax(result);
    }

    /**
     * 删除账号 需要同步session_token access_token 以及 share_token
     * @param id
     * @return
     */
    @GetMapping("/account/delete")
    public AjaxResult accountDelete(@RequestParam("id")  @NonNull Integer id){
        System.out.println("开始执行后台管理删除账号数据模块:" + id);
        int result = 0;
        Optional<ChatAccount> chatAccount = Optional.ofNullable(chatAccountMapper.selectById(id));
        if(!chatAccount.isPresent()){
            return AjaxResult.fail("账号不存在:" + id);
        }
        ChatAccount account = chatAccount.get();
        // 删除配置文件信息
        File file = new File(configFile);
        JSONObject needDelJson = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
        needDelJson.remove(account.getName());
        try {
            FileUtil.writeString(needDelJson.toStringPretty(),configFile,StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            log.error("账号配置文件更新失败" + e);
            return AjaxResult.fail("删除失败：" + e);
        }
        result = chatAccountMapper.deleteById(id);
        return AjaxResult.toAjax(result);
    }

    /**
     * 刷新session_token access_token 同步修改session_token access_token
     * @param id
     * @return
     */
    @GetMapping("/account/refreshSessionToken")
    public AjaxResult refreshSessionToken(@RequestParam("id")  @NonNull Integer id){
        System.out.println("开始执行后台管理删除账号数据模块:" + id);
        int result =  chatAccountService.refreshSessionToken(id,configFile);
        return AjaxResult.toAjax(result);
    }


    /**
     * 分页查询会员数据
     * @return
     */
    @GetMapping("/chunk/list")
    public AjaxResult listChunkPage(@ModelAttribute  @NonNull ListQueryVo listQueryVo){
        System.out.println("开始执行后台管理查询用户数据模块:" + listQueryVo.getPage());
        AjaxResult ajaxResult = chatUsersService.selectChunkPage(listQueryVo);
        return ajaxResult;
    }

    /**
     * 更改会员数据  以及用户状态的更新
     * @return
     */
    @PostMapping("/chunk/update")
    public AjaxResult updateChunk(@RequestBody @NonNull TempVo tempVo){
        System.out.println("开始执行后台管理更新用户数据模块:" + tempVo.getUserName());
        int result = chatUsersService.updateChunk(tempVo);
        System.out.println("用户更新模块用户更新结果：" + result);
        return AjaxResult.toAjax(result);
    }

    /**
     * 创建新用户
     * @return
     */
    @PostMapping("/chunk/create")
    public AjaxResult createChunk(@RequestBody @NonNull TempVo tempVo){
        System.out.println("开始执行后台管理创建用户数据模块:" + tempVo.getCreator());
        int result = chatUsersService.createChunk(tempVo);
        System.out.println("用户更新模块用户创建结果：" + result);
        return AjaxResult.toAjax(result);
    }


    /**
     * 分页查询公告
     * @return
     */
    @GetMapping("/notice/list")
    public AjaxResult listNoticePage(@ModelAttribute  @NonNull ListQueryVo listQueryVo){
        System.out.println("开始执行后台管理查询公告数据模块:" + listQueryVo.getPage());
        AjaxResult ajaxResult = chatNoticeService.selectNoticePage(listQueryVo);
        return ajaxResult;
    }

    /**
     * 更改公告
     * @return
     */
    @PostMapping("/notice/update")
    public AjaxResult updateNotice(@RequestBody @NonNull AdminNoticeVo adminNoticeVo){
        System.out.println("开始执行后台管理更新公告数据模块:" + adminNoticeVo);
        int result = chatNoticeService.updateNotice(adminNoticeVo);
        System.out.println("用户更新模块用户更新结果：" + result);
        return AjaxResult.toAjax(result);
    }

    /**
     * 创建公告
     * @return
     */
    @PostMapping("/notice/create")
    public AjaxResult createNotice(@RequestBody @NonNull AdminNoticeVo adminNoticeVo){
        System.out.println("开始执行后台管理创建公告数据模块:" + adminNoticeVo);
        int result = chatNoticeService.createNotice(adminNoticeVo);
        System.out.println("公告创建结果：" + result);
        return AjaxResult.toAjax(result);
    }


//    public static void main(String[] args) {
//        //仅仅只是测试  很显然  BeanUtils需要两个类属性相同
//        ChatUsers chatUsers = new ChatUsers();
//        chatUsers.setUserStatus(AllStatus.NORMALUSER.getType());
//        chatUsers.setUserEmail("asdasd");
//        chatUsers.setCode("4545");
//        chatUsers.setId(1);
//        System.out.println("更改前:" + chatUsers.toString());
//        TempVo tempVo = new TempVo();
//        tempVo.setUserStatus(AllStatus.VIP.getType());
//        BeanUtils.copyProperties(tempVo,chatUsers);
//        System.out.println("更改后:" + chatUsers.toString());
//    }

//    public static void main(String[] args) {
//        Clock clock = Clock.systemUTC();   代替System.currentTimeMillis()
//        Clock clock1 = Clock.systemDefaultZone();
//        System.out.println(clock1.millis());
//        System.out.println(clock.millis());
//    }

}
