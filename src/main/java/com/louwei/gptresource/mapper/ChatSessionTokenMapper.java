package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatSessionToken;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【chat_session_token(聊天会话Token表)】的数据库操作Mapper
* @createDate 2024-01-20 16:10:52
* @Entity com.louwei.gptresource.domain.ChatSessionToken
*/
@Mapper
public interface ChatSessionTokenMapper extends BaseMapper<ChatSessionToken> {

}




