package com.louwei.gptresource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.domain.ChatRole;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.service.ChatRoleService;
import com.louwei.gptresource.mapper.ChatRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【chat_role】的数据库操作Service实现
* @createDate 2023-12-27 15:45:26
*/
@Service
public class ChatRoleServiceImpl extends ServiceImpl<ChatRoleMapper, ChatRole>
    implements ChatRoleService{
    @Autowired
    private ChatRoleMapper chatRoleMapper;

    @Override
    public boolean createRole(Integer id) {
        boolean success = chatRoleMapper.saveRoleUsers(id,3);
        return success;
    }
}




