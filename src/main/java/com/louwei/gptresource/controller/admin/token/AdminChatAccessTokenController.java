package com.louwei.gptresource.controller.admin.token;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatAccessToken;
import com.louwei.gptresource.domain.ChatShareToken;
import com.louwei.gptresource.service.ChatAccessTokenService;
import com.louwei.gptresource.service.ChatShareTokenService;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminTokenReqVo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/vue-element-admin/user")
@Slf4j
public class AdminChatAccessTokenController {

    @Autowired
    private ChatAccessTokenService chatAccessTokenService;

    @GetMapping("/accessToken/list")
    public AjaxResult getAccessTokenList(@ModelAttribute @NonNull ListQueryVo listQueryVo) {
        log.info("开始执行后台管理查询用户数据模块:" + listQueryVo.getPage());
        AjaxResult ajaxResult = chatAccessTokenService.selectAccessTokenPage(listQueryVo);
        return ajaxResult;
    }

    /**
     * 更改AccessToken数据
     * @return
     */
    @PostMapping("/accessToken/update")
    public AjaxResult updateAccessToken(@RequestBody @NonNull AdminTokenReqVo adminTokenReqVo){
        log.info("开始执行后台管理更新AccessToken数据模块:" + adminTokenReqVo.getCreator());
        int result = chatAccessTokenService.updateAccessToken(adminTokenReqVo);
        log.info("AccessToken更新模块账号更新结果：" + result);
        return AjaxResult.toAjax(result);
    }

    @GetMapping("/accessToken/delete")
    public AjaxResult accessTokenDelete(@RequestParam("id")  @NonNull Integer id) {
        log.info("开始执行后台管理删除accessToken数据模块:" + id);
        Optional<ChatAccessToken> chatShareToken = Optional.ofNullable(chatAccessTokenService.getById(id));
        if (!chatShareToken.isPresent()) {
            return AjaxResult.fail("token不存在:" + id);
        }
        boolean b = chatAccessTokenService.removeById(id);
        if (b) {
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.fail("删除失败");
        }
    }

    @PostMapping("/accessToken/create")
    public AjaxResult createAccessToken(@RequestBody @NonNull AdminTokenReqVo adminTokenReqVo) {
        log.info("开始执行后台管理创建AccessToken数据模块:" + adminTokenReqVo.getCreator());
        int result = chatAccessTokenService.createAccessToken(adminTokenReqVo);

        log.info("AccessToken创建模块账号创建结果：" + result);
        return AjaxResult.toAjax(result);
    }
}
