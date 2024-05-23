package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatShareToken;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【chat_share_token(聊天会话Token表)】的数据库操作Mapper
* @createDate 2024-01-04 20:09:52
* @Entity com.louwei.gptresource.domain.ChatShareToken
*/
@Mapper
public interface ChatShareTokenMapper extends BaseMapper<ChatShareToken> {

}




