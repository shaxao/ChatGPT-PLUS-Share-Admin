package com.louwei.gptresource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatAccessToken;
import com.louwei.gptresource.domain.ChatShareToken;
import com.louwei.gptresource.service.ChatAccessTokenService;
import com.louwei.gptresource.mapper.ChatAccessTokenMapper;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminTokenReqVo;
import kotlin.collections.ArrayDeque;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【chat_access_token(聊天会话Token表)】的数据库操作Service实现
* @createDate 2024-01-20 16:10:52
*/
@Service
public class ChatAccessTokenServiceImpl extends ServiceImpl<ChatAccessTokenMapper, ChatAccessToken>
    implements ChatAccessTokenService{
    @Autowired
    private ChatAccessTokenMapper chatAccessTokenMapper;

    @Override
    public AjaxResult selectAccessTokenPage(ListQueryVo listQueryVo) {
        System.out.println("进入查询shareToken分页数据mapper");
        //从参数中查找查询条件
        QueryWrapper<ChatAccessToken> wrapper = new QueryWrapper<>();
        //1.仅仅分页查询  默认id升序
        //2.用户邮箱分页查询
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getUserEmail()), "email", listQueryVo.getUserEmail());
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getSort()) && !listQueryVo.getSort().contains("id"), "token_status", listQueryVo.getSort());
        //3.日期分类查询
        List<Date> createTime = listQueryVo.getCreateTime();
        if(CollUtil.isNotEmpty(createTime)){
            Date startTime = createTime.get(0);
            Date endTime = createTime.get(1);
            wrapper.ge("create_date",startTime);
            wrapper.le("expire_date",endTime);
        }
        //5.id升序降序分
        if (StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("+id")) {
            wrapper.orderByAsc("id");
        } else {
            wrapper.orderByDesc("id");
        }
        Page<ChatAccessToken> page = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
        Page<ChatAccessToken> pages = chatAccessTokenMapper.selectPage(page, wrapper);
        List<ChatAccessToken> chatAccessTokens = pages.getRecords();
        List<AdminTokenReqVo> adminTokenReqVos = new ArrayDeque<>();
        for (ChatAccessToken chatAccessToken:chatAccessTokens) {
            AdminTokenReqVo adminTokenReqVo = new AdminTokenReqVo();
            System.out.println("chatAccessToken:" + chatAccessToken.toString());
            BeanUtils.copyProperties(chatAccessToken,adminTokenReqVo);
            adminTokenReqVos.add(adminTokenReqVo);
        }
        return AjaxResult.success("查询成功",adminTokenReqVos,pages.getTotal());
    }

    @Override
    public int updateAccessToken(AdminTokenReqVo adminTokenReqVo) {
        if(adminTokenReqVo.getId() != null && chatAccessTokenMapper.selectById(adminTokenReqVo.getId()) != null){
            ChatAccessToken chatAccessToken = new ChatAccessToken();
            BeanUtils.copyProperties(adminTokenReqVo,chatAccessToken);
            return chatAccessTokenMapper.updateById(chatAccessToken);
        }
        return 0;
    }

    @Override
    public int createAccessToken(AdminTokenReqVo adminTokenReqVo) {
        // TODO 是否需要 创建access token时，创建share token
        ChatAccessToken chatAccessToken = new ChatAccessToken();
        BeanUtils.copyProperties(adminTokenReqVo,chatAccessToken);
        chatAccessToken.setCreateDate(new Date());
        chatAccessToken.setUserCount(0);
        return chatAccessTokenMapper.insert(chatAccessToken);
    }
}




