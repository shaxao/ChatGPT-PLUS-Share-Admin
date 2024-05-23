package com.louwei.gptresource.service;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatShareToken;
import com.baomidou.mybatisplus.extension.service.IService;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminShareSetVo;
import com.louwei.gptresource.vo.admin.user.AdminTokenReqVo;
import lombok.NonNull;

/**
* @author Administrator
* @description 针对表【chat_share_token(聊天会话Token表)】的数据库操作Service
* @createDate 2024-01-04 20:09:52
*/
public interface ChatShareTokenService extends IService<ChatShareToken> {

    String findTokenUse();

    String getPanroraUrl();

    AjaxResult selectChunkPage(ListQueryVo listQueryVo);

    int updateShareToken(AdminTokenReqVo adminTokenReqVo);

    int createShareToken(@NonNull AdminTokenReqVo accountTempVo);

    AjaxResult refreshShareToken(AdminShareSetVo adminShareSetVo, String username);

}
