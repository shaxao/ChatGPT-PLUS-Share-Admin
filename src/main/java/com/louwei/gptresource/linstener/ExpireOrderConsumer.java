package com.louwei.gptresource.linstener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.louwei.gptresource.domain.ChatOrders;
import com.louwei.gptresource.domain.ChatProduct;
import com.louwei.gptresource.domain.ChatShareToken;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.enums.OrderStatus;
import com.louwei.gptresource.mapper.ChatOrdersMapper;
import com.louwei.gptresource.mapper.ChatProductMapper;
import com.louwei.gptresource.mapper.ChatShareTokenMapper;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.events.Event;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ExpireOrderConsumer {
    @Autowired
    private ChatOrdersMapper chatOrdersMapper;
    @Autowired
    private ChatProductMapper chatProductMapper;
    @Autowired
    private ChatShareTokenMapper chatShareTokenMapper;
    @Autowired
    private ChatUsersMapper chatUsersMapper;

    private final ExecutorService executors = Executors.newFixedThreadPool(10);


//    @RabbitListener(queues = "expire_queue")
//    public void expireOrder(String orderId) {
//        System.out.println("超时未处理订单:" + orderId + ",查看订单状态，如果未处理，回滚库存");
//        QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
//        //查询数据库中所有未支付的数据，超时，则取消，并回滚库存
//        wrapper.eq("order_no", orderId);
//        ChatOrders c = chatOrdersMapper.selectOne(wrapper);
//        Date now = new Date();
//
////        Date createTime = c.getCreateTime();
////        DateTime dateTime = DateUtil.offsetMinute(createTime, 10);
//        //超时未支付，取消订单，回滚库存
//        if(!c.getOrderStatus().equals(OrderStatus.SUCCESS.getType())){
//            System.out.println("订单监控系统开始处理超时订单：" + c.getTitle());
//            c.setExpireTime(now);
//            c.setOrderStatus(OrderStatus.CANCEL.getType());
//            chatOrdersMapper.updateById(c);
//            Integer quantity = c.getQuantity();
//            Integer proId = c.getProId();
//            ChatProduct chatProduct = chatProductMapper.selectById(proId);
//            chatProduct.setStock(chatProduct.getStock() + quantity);
//            int i = chatProductMapper.updateById(chatProduct);
//            System.out.println("修改库存结果:" + i);
//        }
//    }

    /**
     * 批量处理过期消息  事实证明每次只能监听一个消息
     * @param orderId
     */
    @RabbitListener(queues = "order_delayed_queue", containerFactory = "batchListenerContainerFactory")
    public void batchExpireOrder(Long orderId, Message message, Channel channel) {
        System.out.println("监听过期订单: " + orderId);
        executors.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("超时未处理订单:" + orderId + ",查看订单状态，如果未处理，回滚库存");
                QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
                //查询数据库中所有未支付的数据，超时，则取消，并回滚库存
                wrapper.eq("id", orderId);
                ChatOrders c = chatOrdersMapper.selectOne(wrapper);
                Date now = new Date();
                //超时未支付，取消订单，回滚库存
                if (!c.getOrderStatus().equals(OrderStatus.SUCCESS.getType())) {
                    System.out.println("订单监控系统开始处理超时订单：" + c.getTitle());
                    c.setExpireTime(now);
                    c.setOrderStatus(OrderStatus.TIMEOUT.getType());
                    chatOrdersMapper.updateById(c);
                    Integer quantity = c.getQuantity();
                    Integer proId = c.getProId();
                    ChatProduct chatProduct = chatProductMapper.selectById(proId);
                    chatProduct.setStock(chatProduct.getStock() + quantity);
                    int i = chatProductMapper.updateById(chatProduct);
                    // 获取消息投递序号
                    long deliveryTag = message.getMessageProperties().getDeliveryTag();
                    try {
                        if (i > 0) {
                            log.info("订单超时更新成功:{}", orderId);
                            // 签收
                            channel.basicAck(deliveryTag, false);
                        } else {
                            log.info("订单超时更新失败:{}", orderId);
                            channel.basicNack(deliveryTag, false, false);
                        }
                    } catch (IOException e) {
                        try {
                            channel.basicNack(deliveryTag,false,false);
                        } catch (IOException ex) {
                            log.error("消息签收异常:{}",ex.getMessage());
                        }
                    }
                    log.info("修改库存结果:{}", i);
                }
            }
        });
    }

    @RabbitListener(queues = "token_delayed_queue")
    public void tokenExpireExecute(Long id, Message message, Channel channel) {
        log.info("监听过期share token: " + id);
        ChatShareToken chatShareToken = chatShareTokenMapper.selectById(id);
        if (chatShareToken != null) {
            chatShareToken.setTokenStatus(AllStatus.EXPIREVIP.getType());
            chatShareTokenMapper.updateById(chatShareToken);
            // 获取消息投递序号
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            try {
                log.info("share token过期队列签收成功");
                // 签收
                channel.basicAck(deliveryTag, true);
            } catch (IOException e) {
                log.error("share token过期队列消息签收失败: {}", e.getMessage());
                // 尝试重新签收，或者决定是否重新排队
                try {
                    Thread.sleep(5000); // 5秒后重试
                    channel.basicAck(deliveryTag, false);
                } catch (InterruptedException | IOException retryException) {
                    log.error("重试签收失败，消息发送到死信队列: {}", retryException.getMessage());
                    try {
                        channel.basicNack(deliveryTag,false,false);
                    } catch (IOException ex) {
                        log.error("消息签收异常:{}",ex.getMessage());
                    }
                }
            }
        }

    }

    @RabbitListener(queues = "chunk_delayed_queue")
    public void expireChunkExecute(Integer id, Message message,Channel channel) {
        log.info("处理过期会员:{}",id);
        ChatUsers chatUsers = chatUsersMapper.selectById(id);
        if(chatUsers != null) {
            log.info("过期会员信息:{}",chatUsers.getUserName());
            chatUsersMapper.updateUserStatus(chatUsers.getId(),AllStatus.NORMALUSER.getType());
            chatUsersMapper.updateUserRole(chatUsers.getId(),3);
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            try {
                channel.basicAck(deliveryTag,true);
                log.info("过期会员处理签收成功");
            } catch (IOException e) {
                log.info("过期会员队列签收失败,发送到死信队列");
                try {
                    channel.basicNack(deliveryTag,false,false);
                } catch (IOException ex) {
                    log.error("消息签收异常:{}",ex.getMessage());
                }
            }
        }
    }

    @RabbitListener(queues = "dead_queue")
    public void deadQueueExecute(Message message,Channel channel) {
        log.info("死信队列消息: {}", message);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

    }



}
