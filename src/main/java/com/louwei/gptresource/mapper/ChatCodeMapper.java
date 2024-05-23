package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【chat_code(聊天二维码表)】的数据库操作Mapper
* @createDate 2024-01-04 17:01:34
* @Entity com.louwei.gptresource.domain.ChatCode
*/
@Mapper
public interface ChatCodeMapper extends BaseMapper<ChatCode> {

}




