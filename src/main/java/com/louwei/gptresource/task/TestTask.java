//package com.louwei.gptresource.task;
//
//import cn.hutool.core.date.DateTime;
//import cn.hutool.core.date.DateUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.louwei.gptresource.domain.ChatOrders;
//import com.louwei.gptresource.domain.ChatProduct;
//import com.louwei.gptresource.enums.OrderStatus;
//import com.louwei.gptresource.mapper.ChatOrdersMapper;
//import com.louwei.gptresource.mapper.ChatProductMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * 处理未支付订单
// */
//@Component
//public class TestTask {
//    @Autowired
//    private ChatOrdersMapper chatOrdersMapper;
//    @Autowired
//    private ChatProductMapper chatProductMapper;
//
//    @Scheduled(cron = "0 0/5 * * * *")
//    public void task(){
//        System.out.println("开始执行订单监控程序");
//        QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
//        //查询数据库中所有未支付的数据，超时，则取消，并回滚库存
//        wrapper.eq("order_status",OrderStatus.NOTPAY.getType());
//        List<ChatOrders> chatOrders = chatOrdersMapper.selectList(wrapper);
//        Date now = new Date();
//        for (ChatOrders c:chatOrders) {
//            Date createTime = c.getCreateTime();
//            DateTime dateTime = DateUtil.offsetMinute(createTime, 10);
//            //超时，取消订单
//            if(now.compareTo(dateTime) > 0){
//                System.out.println("订单监控系统开始处理超时订单：" + c.getTitle());
//                c.setExpireTime(now);
//                c.setOrderStatus(OrderStatus.CANCEL.getType());
//                chatOrdersMapper.updateById(c);
//                Integer quantity = c.getQuantity();
//                Integer proId = c.getProId();
//                ChatProduct chatProduct = chatProductMapper.selectById(proId);
//                chatProduct.setStock(chatProduct.getStock() + quantity);
//                int i = chatProductMapper.updateById(chatProduct);
//                System.out.println("修改库存结果:" + i);
//            }
//        }
//    }
//}
