package com.louwei.gptresource.service;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatUsers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.TempVo;

/**
* @author Administrator
* @description 针对表【chat_users(用户表)】的数据库操作Service
* @createDate 2023-12-25 16:01:13
*/
public interface ChatUsersService extends IService<ChatUsers> {

    int findByUserName(String userName);

    int findByphone(String phone);

    int findByemail(String email);

    int createUsersByMail(String toUserEmail,String code);

    ChatUsers findByEmail(String phoneOrEmail);

    ChatUsers findUserByUserName(String username);

    AjaxResult selectChunkPage(ListQueryVo listQueryVo);

    int updateChunk(TempVo tempVo);

    int createChunk(TempVo tempVo);

}
