package com.louwei.gptresource.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.louwei.gptresource.domain.ChatAccessToken;
import com.louwei.gptresource.domain.ChatOrders;
import com.louwei.gptresource.domain.ChatShareToken;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.enums.OrderStatus;
import com.louwei.gptresource.mapper.ChatOrdersMapper;
import com.louwei.gptresource.service.ChatAccessTokenService;
import com.louwei.gptresource.service.ChatShareTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 每个月的第一天刷新用户支付订单的获取token次数
 */
@Component
public class TokenCountTask {
    @Autowired
    private ChatOrdersMapper chatOrdersMapper;
    @Autowired
    private ChatShareTokenService chatShareTokenService;
    @Autowired
    private ChatAccessTokenService chatAccessTokenService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 0 1 * *")
    public void refreshTokenCount(){
        //获取所有支付订单，不用管是否过期，不由该程序负责
        QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
        wrapper.eq("order_status", OrderStatus.SUCCESS.getType());
        List<ChatOrders> chatOrders = chatOrdersMapper.selectList(wrapper);
        for (ChatOrders chatOrders1:chatOrders){
            chatOrders1.setRefreshCount(5);
            chatOrdersMapper.updateById(chatOrders1);
        }
    }


    // @Scheduled(fixedDelay = 30000)
    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void refreshUseCount() {
        System.out.println("开始刷新使用次数");
        UpdateWrapper<ChatShareToken> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("user_count", 0).eq("token_status", AllStatus.NORMAL.getType());
        chatShareTokenService.update(updateWrapper);
        UpdateWrapper<ChatAccessToken> chatAccessTokenUpdateWrapper = new UpdateWrapper<>();
        chatAccessTokenUpdateWrapper.set("user_count", 0).eq("token_status", AllStatus.NORMAL.getType());
        chatAccessTokenService.update(chatAccessTokenUpdateWrapper);
        redisTemplate.opsForValue().set("accessToken:userCount", "0");
    }
}
