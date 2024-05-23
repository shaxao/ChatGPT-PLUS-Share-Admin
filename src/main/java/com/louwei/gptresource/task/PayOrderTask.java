package com.louwei.gptresource.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.louwei.gptresource.domain.ChatOrders;
import com.louwei.gptresource.domain.ChatRole;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.enums.OrderStatus;
import com.louwei.gptresource.mapper.ChatOrdersMapper;
import com.louwei.gptresource.mapper.ChatRoleMapper;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 对于已经支付的订单的过期监控  15天执行一次
 */
@Component
public class PayOrderTask {
    @Autowired
    private ChatOrdersMapper chatOrdersMapper;
    @Autowired
    private ChatUsersMapper chatUsersMapper;


    /**
     * 每15天00:00执行
     */
    @Scheduled(cron = "0 0 0 */15 * *")
    @Transactional
    public void orderTask(){
        QueryWrapper<ChatOrders> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("order_status", OrderStatus.SUCCESS.getType());
        orderWrapper.orderByDesc("expire_time");
        List<ChatOrders> chatOrders = chatOrdersMapper.selectList(orderWrapper);
        int id = 0;
        for (ChatOrders chatOrders1:chatOrders){
            if(id == chatOrders1.getUserId()){
                continue;
            }
            //处理所有已支付订单过期的部分，并将相关的用户权限降级
            Date now = new Date();
            if(now.compareTo(chatOrders1.getExpireTime()) > 0){
                id = chatOrders1.getUserId();
                chatOrders1.setOrderStatus(OrderStatus.EXPIRE.getType());
                chatOrdersMapper.updateById(chatOrders1);
                chatUsersMapper.updateUserStatus(id,AllStatus.NORMALUSER.getType());
                chatUsersMapper.updateUserRole(id,3);
            }
        }
    }
}
