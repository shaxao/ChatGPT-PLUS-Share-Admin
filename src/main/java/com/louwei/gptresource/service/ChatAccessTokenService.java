package com.louwei.gptresource.service;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatAccessToken;
import com.baomidou.mybatisplus.extension.service.IService;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminTokenReqVo;

/**
* @author Administrator
* @description 针对表【chat_access_token(聊天会话Token表)】的数据库操作Service
* @createDate 2024-01-20 16:10:52
*/
public interface ChatAccessTokenService extends IService<ChatAccessToken> {

    AjaxResult selectAccessTokenPage(ListQueryVo listQueryVo);

    int updateAccessToken(AdminTokenReqVo adminTokenReqVo);

    int createAccessToken(AdminTokenReqVo adminTokenReqVo);
}
