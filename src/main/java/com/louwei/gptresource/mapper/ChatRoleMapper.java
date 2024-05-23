package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【chat_role】的数据库操作Mapper
* @createDate 2023-12-27 15:45:26
* @Entity com.louwei.gptresource.domain.ChatRole
*/
@Mapper
public interface ChatRoleMapper extends BaseMapper<ChatRole> {

    boolean saveRoleUsers(Integer uid, int rid);
}




