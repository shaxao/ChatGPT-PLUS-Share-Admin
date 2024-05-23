package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【chat_permission】的数据库操作Mapper
* @createDate 2023-12-27 15:45:26
* @Entity com.louwei.gptresource.domain.ChatPermission
*/
@Mapper
public interface ChatPermissionMapper extends BaseMapper<ChatPermission> {
    List<ChatPermission> findPermissionByUsername(String username);
}




