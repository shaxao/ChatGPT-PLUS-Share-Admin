package com.louwei.gptresource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatNotice;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.service.ChatNoticeService;
import com.louwei.gptresource.mapper.ChatNoticeMapper;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminNoticeVo;
import com.louwei.gptresource.vo.admin.user.AdminUserReqVo;
import kotlin.collections.ArrayDeque;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @author Administrator
* @description 针对表【chat_notice】的数据库操作Service实现
* @createDate 2024-05-20 15:48:38
*/
@Service
public class ChatNoticeServiceImpl extends ServiceImpl<ChatNoticeMapper, ChatNotice>
    implements ChatNoticeService{
    @Autowired
    private ChatNoticeMapper chatNoticeMapper;

    @Override
    public String getNotice() {
        QueryWrapper<ChatNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_enabled", 1);
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT 1");
        ChatNotice chatNotice = chatNoticeMapper.selectOne(queryWrapper);
        return chatNotice == null ? null : chatNotice.getMessage();
    }

    @Override
    public AjaxResult selectNoticePage(ListQueryVo listQueryVo) {
        System.out.println("进入查询公告分页数据mapper");
        //从参数中查找查询条件
        QueryWrapper<ChatNotice> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getSort()) && !listQueryVo.getSort().contains("id"), "is_enabled", listQueryVo.getSort().equals(AllStatus.OPEN) ? 1 : 0);
        //3.创还能日期分类查询
        List<Date> createTime = listQueryVo.getCreateTime();
        if(CollUtil.isNotEmpty(createTime)){
            Date startTime = createTime.get(0);
            Date endTime = createTime.get(1);
            wrapper.ge("created_at",startTime);
            wrapper.le("created_at",endTime);
        }
        //5.id升序降序分
        if (StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("+id")) {
            wrapper.orderByAsc("id");
        } else {
            wrapper.orderByDesc("id");
        }
        Page<ChatNotice> page = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
        Page<ChatNotice> pages = chatNoticeMapper.selectPage(page, wrapper);
        List<ChatNotice> chatNoticeList = pages.getRecords();
        return AjaxResult.success("查询成功",CollUtil.isNotEmpty(chatNoticeList) ? chatNoticeList : Collections.emptyList(), pages.getTotal());
    }

    @Override
    public int updateNotice(AdminNoticeVo adminNoticeVo) {
        AtomicInteger result = new AtomicInteger(0);
        Optional<ChatNotice> chatNoticeOptional = Optional.ofNullable(chatNoticeMapper.selectById(adminNoticeVo.getId()));
        chatNoticeOptional.ifPresent(chatNotice -> {
            chatNotice.setUpdatedAt(new Date());
            chatNotice.setIsEnabled(adminNoticeVo.getIsEnabled());
            chatNotice.setMessage(adminNoticeVo.getMessage());
            chatNoticeMapper.updateById(chatNotice);
            result.set(1);
        });
        return result.get();
    }

    @Override
    public int createNotice(AdminNoticeVo adminNoticeVo) {
        int i = 0;
        ChatNotice chatNotice = new ChatNotice();
        BeanUtils.copyProperties(adminNoticeVo, chatNotice);
        chatNotice.setCreatedAt(new Date());
        try {
            i = chatNoticeMapper.insert(chatNotice);
        } catch (Exception e) {
            log.error("创建公告数据库异常:{}", e);
        }
        return i;
    }
}




