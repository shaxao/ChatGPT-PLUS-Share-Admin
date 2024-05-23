package com.louwei.gptresource.service;

import com.louwei.gptresource.domain.ChatRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【chat_role】的数据库操作Service
* @createDate 2023-12-27 15:45:26
*/
public interface ChatRoleService extends IService<ChatRole> {

    boolean createRole(Integer id);
}
