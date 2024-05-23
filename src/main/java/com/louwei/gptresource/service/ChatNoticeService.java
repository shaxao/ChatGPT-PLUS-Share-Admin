package com.louwei.gptresource.service;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatNotice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminNoticeVo;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【chat_notice】的数据库操作Service
* @createDate 2024-05-20 15:48:38
*/

public interface ChatNoticeService extends IService<ChatNotice> {

    String getNotice();

    AjaxResult selectNoticePage(ListQueryVo listQueryVo);

    int updateNotice(AdminNoticeVo adminNoticeVo);

    int createNotice(AdminNoticeVo adminNoticeVo);
}
