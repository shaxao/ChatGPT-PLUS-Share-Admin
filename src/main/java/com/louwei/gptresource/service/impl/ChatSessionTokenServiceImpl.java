package com.louwei.gptresource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.domain.ChatSessionToken;
import com.louwei.gptresource.service.ChatSessionTokenService;
import com.louwei.gptresource.mapper.ChatSessionTokenMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【chat_session_token(聊天会话Token表)】的数据库操作Service实现
* @createDate 2024-01-20 16:10:52
*/
@Service
public class ChatSessionTokenServiceImpl extends ServiceImpl<ChatSessionTokenMapper, ChatSessionToken>
    implements ChatSessionTokenService{

}




