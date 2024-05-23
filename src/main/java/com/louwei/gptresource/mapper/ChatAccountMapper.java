package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【chat_account(用户账号表)】的数据库操作Mapper
* @createDate 2024-01-20 10:01:48
* @Entity com.louwei.gptresource.domain.ChatAccount
*/
@Mapper
public interface ChatAccountMapper extends BaseMapper<ChatAccount> {

}




