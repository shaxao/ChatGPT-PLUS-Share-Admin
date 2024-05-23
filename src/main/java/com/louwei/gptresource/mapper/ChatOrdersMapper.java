package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatOrders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import javax.mail.MailSessionDefinition;

/**
* @author Administrator
* @description 针对表【chat_orders】的数据库操作Mapper
* @createDate 2023-12-31 16:21:55
* @Entity com.louwei.gptresource.domain.ChatOrders
*/
@Mapper
public interface ChatOrdersMapper extends BaseMapper<ChatOrders> {

}




