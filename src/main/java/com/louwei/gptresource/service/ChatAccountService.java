package com.louwei.gptresource.service;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.louwei.gptresource.vo.AccountTempVo;
import com.louwei.gptresource.vo.ListQueryVo;

/**
* @author Administrator
* @description 针对表【chat_account(用户账号表)】的数据库操作Service
* @createDate 2024-01-20 10:01:48
*/
public interface ChatAccountService extends IService<ChatAccount> {

    AjaxResult selectAccountPage(ListQueryVo listQueryVo);

    int updateAccount(AccountTempVo tempVo, String configFile);

    int refreshSessionToken(Integer id, String configFile);

    int createAccount(AccountTempVo accountTempVo);
}
