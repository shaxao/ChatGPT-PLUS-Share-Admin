package com.louwei.gptresource.controller.admin.token;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatAccount;
import com.louwei.gptresource.domain.ChatShareToken;
import com.louwei.gptresource.mapper.ChatShareTokenMapper;
import com.louwei.gptresource.service.ChatShareTokenService;
import com.louwei.gptresource.utils.JWTUtil;
import com.louwei.gptresource.vo.AccountTempVo;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminShareSetVo;
import com.louwei.gptresource.vo.admin.user.AdminTokenReqVo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/vue-element-admin/user")
@Slf4j
public class AdminChatShareTokenController {
    @Autowired
    private ChatShareTokenService chatShareTokenService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/shareToken/list")
    public AjaxResult getShareTokenList(@ModelAttribute @NonNull ListQueryVo listQueryVo) {
        log.info("开始执行后台管理查询用户数据模块:" + listQueryVo.getPage());
        AjaxResult ajaxResult = chatShareTokenService.selectChunkPage(listQueryVo);
        return ajaxResult;
    }

    /**
     * 更改shareToken数据
     * @return
     */
    @PostMapping("/shareToken/update")
    public AjaxResult updateShareToken(@RequestBody @NonNull AdminTokenReqVo adminTokenReqVo){
        log.info("开始执行后台管理更新shareToken数据模块:" + adminTokenReqVo.getCreator());
        int result = chatShareTokenService.updateShareToken(adminTokenReqVo);
        log.info("shareToken更新模块账号更新结果：" + result);
        return AjaxResult.toAjax(result);
    }

    @GetMapping("/shareToken/delete")
    public AjaxResult shareTokenDelete(@RequestParam("id")  @NonNull Integer id) {
        log.info("开始执行后台管理删除shareToken数据模块:" + id);
        Optional<ChatShareToken> chatShareToken = Optional.ofNullable(chatShareTokenService.getById(id));
        if (!chatShareToken.isPresent()) {
            return AjaxResult.fail("token不存在:" + id);
        }
        boolean b = chatShareTokenService.removeById(id);
        if (b) {
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.fail("删除失败");
        }
    }

    @PostMapping("/shareToken/create")
    public AjaxResult createShareToken(@RequestBody @NonNull AdminTokenReqVo adminTokenReqVo) {
        log.info("开始执行后台管理创建shareToken数据模块:" + adminTokenReqVo.getCreator());
        int result = chatShareTokenService.createShareToken(adminTokenReqVo);
        log.info("shareToken创建模块账号创建结果：" + result);
        return AjaxResult.toAjax(result);
    }

    @PostMapping("/shareToken/refresh")
    public AjaxResult refreshShareToken(@RequestBody AdminShareSetVo adminShareSetVo, HttpServletRequest request) {
        log.info("开始执行后台管理刷新shareToken数据模块:" + adminShareSetVo.getId());
        String token = request.getHeader("Authorization");
        log.info("请求中token={}", token);
        String username = "";
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if (JWTUtil.verify(jwtToken)) {
                username = JWTUtil.getUsernameFromToken(jwtToken);
                log.info("Username from token: {}", username);
            }
        }
        return chatShareTokenService.refreshShareToken(adminShareSetVo, username);

    }
}
