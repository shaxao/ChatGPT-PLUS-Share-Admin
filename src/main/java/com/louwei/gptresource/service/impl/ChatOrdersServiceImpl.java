package com.louwei.gptresource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatOrders;
import com.louwei.gptresource.domain.ChatProduct;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.enums.NumberValue;
import com.louwei.gptresource.enums.OrderStatus;
import com.louwei.gptresource.mapper.ChatProductMapper;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import com.louwei.gptresource.service.ChatOrdersService;
import com.louwei.gptresource.mapper.ChatOrdersMapper;
import com.louwei.gptresource.service.ChatUsersService;
import com.louwei.gptresource.utils.DanweiUtils;
import com.louwei.gptresource.utils.UserDetailsNow;
import com.louwei.gptresource.vo.*;
import com.louwei.gptresource.vo.admin.user.AdminOrderReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【chat_orders】的数据库操作Service实现
* @createDate 2023-12-31 16:21:55
*/
@Service
@Slf4j
public class ChatOrdersServiceImpl extends ServiceImpl<ChatOrdersMapper, ChatOrders>
    implements ChatOrdersService{
    @Autowired
    private ChatOrdersMapper chatOrdersMapper;
    @Autowired
    private ChatUsersMapper chatUsersMapper;
    @Autowired
    private ChatProductMapper chatProductMapper;
    @Autowired
    private ChatUsersService chatUsersService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public ChatOrderRepVo createOrder(ChatProductDetailReqVo detailReqVo, Integer id) {
        try {
            ChatOrders chatOrders = new ChatOrders();
            chatOrders.setQuantity(detailReqVo.getQuantity());
            chatOrders.setProId(detailReqVo.getProductId());
            Date now = new Date();
            chatOrders.setId(System.currentTimeMillis());
            chatOrders.setCreateTime(now);
            chatOrders.setOrderStatus(OrderStatus.NOTPAY.getType());
            String orderNo = String.valueOf(System.currentTimeMillis());
            chatOrders.setOrderNo(orderNo);
            String title = detailReqVo.getTitle();
            //TODO 这是一个坑，限制了标题
            int offer = Integer.parseInt(title.substring(0,title.lastIndexOf("天")));
            DateTime expireTime = DateUtil.offsetDay(now, offer);
            chatOrders.setExpireTime(expireTime);
            chatOrders.setUserId(id);
            //元转分
            BigDecimal price = detailReqVo.getPrice();
            System.out.println("price:" + price);
            BigDecimal priceInCents = price.multiply(new BigDecimal("100"));
            System.out.println("priceInCents:" + priceInCents);
            Integer quantity = detailReqVo.getQuantity();
            System.out.println("quantity:" + quantity);
            BigDecimal totalPriceInCents = priceInCents.multiply(new BigDecimal(quantity));
            Integer totalFee = totalPriceInCents.intValue(); // 四舍五入到最近的整数
            chatOrders.setTotalFee(totalFee);
            chatOrders.setTitle(title);
            int i = chatOrdersMapper.insert(chatOrders);
            if(i > 0){
                ChatOrderRepVo chatOrderRepVo = new ChatOrderRepVo();
                chatOrderRepVo.setCreateTime(now);
                BigDecimal centsToYuan = DanweiUtils.centsToYuan(totalFee);
                chatOrderRepVo.setPrice(centsToYuan);
                chatOrderRepVo.setTradeNo(orderNo);
                chatOrderRepVo.setOrderStatus(OrderStatus.NOTPAY.getType());
                chatOrderRepVo.setProductName(title);
                // 放入队列监听订单变化
                MessagePostProcessor message = new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        message.getMessageProperties().setDelay(300000);
                        return message;
                    }
                };
                rabbitTemplate.convertAndSend("order_delayed_exchange", "order_routing", chatOrders.getId(), message);
                return chatOrderRepVo;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取订单详情
     *
     * @param username
     * @return
     */
    @Override
    public Page<ChatOrders> findOrderByUsername(String username) {
        Page<ChatOrders> pageSelect = new Page(1,10);
        ChatUsers chatUsers = chatUsersMapper.selectOne(new QueryWrapper<ChatUsers>().eq("user_name", username));
        QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", chatUsers.getId()).orderByDesc("create_time");
        Page<ChatOrders> chatOrdersPage = chatOrdersMapper.selectPage(pageSelect, wrapper);
        return chatOrdersPage;
    }

    @Override
    public List<ChatOrderReqVo> chatOrderToReq(List<ChatOrders> chatOrdersList) {
        List<ChatOrderReqVo> chatOrderReqVos = new ArrayList<>();
        for (ChatOrders chatOrders:chatOrdersList) {
            ChatOrderReqVo chatOrderReqVo = new ChatOrderReqVo();
            chatOrderReqVo.setId(chatOrders.getId());
            chatOrderReqVo.setUserId(chatOrders.getUserId());
            chatOrderReqVo.setCreateTime(chatOrders.getCreateTime());
            chatOrderReqVo.setProductName(chatOrders.getTitle());
            chatOrderReqVo.setExpireTime(chatOrders.getExpireTime());
            chatOrderReqVo.setOrderStatus(chatOrders.getOrderStatus());
            chatOrderReqVo.setPrice(chatOrders.getTotalFee() / 100.00);
            chatOrderReqVos.add(chatOrderReqVo);
        }
        return chatOrderReqVos;
    }

    @Override
    public String checkOrderStatusByOrderNo(String orderNo) {
        ChatOrders chatOrders = chatOrdersMapper.selectById(orderNo);
        if(chatOrders != null) {
            return chatOrders.getOrderStatus();
        }
        return null;
    }

    /**
     * 刷新订单次数
     * @param id
     * @return
     */
    @Override
    public boolean refrshCountById(Long id) {
        System.out.println("开始刷新订单次数:" + id);
        ChatOrders chatOrders = chatOrdersMapper.selectById(id);
        System.out.println("chatOrDERS:"+ chatOrders.toString());
        if(chatOrders != null){
            Date now = new Date();
            Date expireTime = chatOrders.getExpireTime();
            Integer refreshCount = chatOrders.getRefreshCount();
            if(now.compareTo(expireTime) > 0 || !chatOrders.getOrderStatus().equals(OrderStatus.SUCCESS.getType()) || refreshCount<= 0 ){
                return false;
            }
            chatOrders.setRefreshCount(refreshCount - 1);
            int update = chatOrdersMapper.updateById(chatOrders);
            if (update > 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 取消订单
     * @param orderNo
     * @return
     */
    @Override
    public boolean cancelOrder(String orderNo) {
        QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        ChatOrders chatOrders = chatOrdersMapper.selectOne(wrapper);
        if (chatOrders != null){
            //如果用户不是未支付状态无法取消订单
            if (!chatOrders.getOrderStatus().equals(OrderStatus.NOTPAY.getType())){
                ChatProduct product = chatProductMapper.selectById(chatOrders.getProId());
                Integer stock = product.getStock();
                //回滚库存
                System.out.println("回滚库存，回滚前:" + product.getStock());
                product.setStock(stock + chatOrders.getQuantity());
                System.out.println("回滚库存，回滚后:" + product.getStock());
                chatProductMapper.updateById(product);
                return false;
            }
            chatOrders.setOrderStatus(OrderStatus.CANCEL.getType());
            ChatProduct product = chatProductMapper.selectById(chatOrders.getProId());
            Integer stock = product.getStock();
            //回滚库存
            System.out.println("回滚库存，回滚前:" + product.getStock());
            product.setStock(stock + chatOrders.getQuantity());
            System.out.println("回滚库存，回滚后:" + product.getStock());
            chatProductMapper.updateById(product);

            Date now  = new Date();
            chatOrders.setExpireTime(now);
            int update = chatOrdersMapper.updateById(chatOrders);
            return update > 0;
        }
        return false;
    }

    /**
     * 后台管理查询订单分页
     * @param listQueryVo
     * @return
     */
    @Override
    public AjaxResult selectOrderPage(ListQueryVo listQueryVo) {
        System.out.println("后台管理开始查询订单");
        //多重条件查询  商品标题  创建时间  id升序  价格升序降序
        //从参数中查找查询条件
        // 商品标题
        QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getTitle()), "title", listQueryVo.getTitle());
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getSort()) && !listQueryVo.getSort().contains("price"), "order_status", listQueryVo.getSort());
        //3.创还能日期分类查询
        List<Date> createTime = listQueryVo.getCreateTime();
        if(CollUtil.isNotEmpty(createTime)){
            Date startTime = createTime.get(0);
            Date endTime = createTime.get(1);
            wrapper.ge("create_time",startTime);
            wrapper.le("create_time",endTime);
        }
        //5.价格升序降序
        if (StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("+price")) {
            wrapper.orderByAsc("total_fee");
        } else if(listQueryVo.getSort().equals("-price")){
            wrapper.orderByDesc("total_fee");
        }else {
            wrapper.orderByAsc("create_time");
        }
        Page<ChatOrders> ordersPage = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
        Page<ChatOrders> chatOrdersPage = chatOrdersMapper.selectPage(ordersPage, wrapper);
        log.info("订单查询结果:" + chatOrdersPage.getTotal());
        List<ChatOrders> chatOrdersList = chatOrdersPage.getRecords();
        List<AdminOrderReqVo> adminOrderReqVos = new ArrayList<>();
        for (ChatOrders chatOrders:chatOrdersList){
            AdminOrderReqVo adminOrderReqVo = new AdminOrderReqVo();
            BeanUtils.copyProperties(chatOrders,adminOrderReqVo);
            //添加用户邮箱
            ChatUsers chatUsers = chatUsersMapper.selectById(chatOrders.getUserId());
            adminOrderReqVo.setUserEmail(chatUsers.getUserEmail());
            adminOrderReqVos.add(adminOrderReqVo);
        }
        return AjaxResult.success("订单查询成功",adminOrderReqVos,chatOrdersPage.getTotal());
    }

    /**
     * 修改订单信息  以及单独修改订单状态
     * @param tempVo
     * @return
     */
    @Override
    public int updateOrderPage(TempVo tempVo) {
        ChatOrders chatOrders = chatOrdersMapper.selectOne(new QueryWrapper<ChatOrders>().eq("order_no",tempVo.getOrderNo()));
        if(chatOrders == null){
            return 0;
        }
        //修改订单信息
        BeanUtils.copyProperties(tempVo,chatOrders);
        int i = chatOrdersMapper.updateById(chatOrders);
        return i;
    }

    /**
     * 创建订单
     * @param tempVo
     * @return
     */
    @Override
    public int createOrderPage(TempVo tempVo) {
        log.info("后台管理系统开始处理订单添加......");
        ChatOrders chatOrders = new ChatOrders();
        BeanUtils.copyProperties(tempVo,chatOrders);
        //生成订单编号
        String orderNo = String.valueOf(System.currentTimeMillis());
        chatOrders.setOrderNo(orderNo);
        chatOrders.setRefreshCount(NumberValue.REFRESHCOUNT.getValue());
        //根据邮箱查找用户ID
        Integer uid = chatUsersMapper.findUserIdByEmail(tempVo.getUserEmail());
        chatOrders.setUserId(uid);
        chatOrders.setQuantity(tempVo.getQuantity());
        chatOrders.setId(System.currentTimeMillis());
        String title = tempVo.getTitle();
        Date now = new Date();
        int offer = Integer.parseInt(title.substring(0,title.lastIndexOf("天")));
        DateTime expireTime = DateUtil.offsetDay(now, offer);
        chatOrders.setExpireTime(expireTime);
        Integer pid = chatProductMapper.findProIdByT(title);
        chatOrders.setProId(pid);
        int i = chatOrdersMapper.insert(chatOrders);
        log.info("后台管理系统处理订单添加结果:" + i);
        return i;
    }

    /**
     * 在渲染导航页面数据前检查用户的会员是否过期
     * @param id
     * @return
     */
    @Override
    public String checkUserStatus(Integer id) {
        // 获取该用户已支付的过期时间最晚的订单信息
        ChatOrders chatOrders = chatOrdersMapper.selectOne(new QueryWrapper<ChatOrders>()
                .eq("user_id", id)
                .eq("order_status",OrderStatus.SUCCESS.getType())
                .orderByDesc("expire_time"));
        Date now = new Date();
        if(now.compareTo(chatOrders.getExpireTime()) > 0){
            // 说明该用户已经过期
            chatOrders.setOrderStatus(OrderStatus.EXPIRE.getType());
            chatOrdersMapper.updateById(chatOrders);
            // 用户权限降级
            chatUsersMapper.updateUserStatus(id,AllStatus.NORMALUSER.getType());
            chatUsersMapper.updateUserRole(id,3);
            return AllStatus.NORMALUSER.getType();
        }else {
            return AllStatus.VIP.getType();
        }
    }

    /**
     * 分页查询
     *
     * @param page  查询页数
     * @param limit
     * @return
     */
    @Override
    public AjaxResult pageResult(Integer page, Integer limit) {
        Page<ChatOrders> pageSelect = new Page(page,limit);
        String username = UserDetailsNow.getUsername();
        ChatUsers chatUsers = chatUsersMapper.selectOne(new QueryWrapper<ChatUsers>().eq("user_name", username));
        QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", chatUsers.getId()).orderByDesc("create_time");
        Page<ChatOrders> chatOrdersPage = chatOrdersMapper.selectPage(pageSelect, wrapper);
        List<ChatOrderReqVo> chatOrderReqVos = chatOrderToReq(chatOrdersPage.getRecords());
        System.out.println("订单查询条数: " + ((chatOrdersPage.getTotal() / limit) + (chatOrdersPage.getTotal() % limit > 0 ? 1 : 0)));
        return AjaxResult.success("分页查询成功", chatOrderReqVos, ((chatOrdersPage.getTotal() / limit) + (chatOrdersPage.getTotal() % limit > 0 ? 1 : 0)));
    }

//    public static void main(String[] args) {
//
//        Integer a = 1265;
//
//        BigDecimal price = BigDecimal.valueOf(8.00); // 单价（元），假设为19.99元
//
//        Integer quantity = 5; // 数量
//
//        // 将价格从元转换为分（1元 = 100分）
//        BigDecimal priceInCents = price.multiply(new BigDecimal("100"));
//
//        // 总价 = 单价（分）* 数量
//        BigDecimal totalPriceInCents = priceInCents.multiply(new BigDecimal(quantity));
//
//        // 将总价转换为Integer类型
//        Integer totalFee = totalPriceInCents.intValue(); // 四舍五入到最近的整数
//
//        // 输出结果
//        System.out.println("Total Fee in cents: " + totalFee); // 总费用（分）
//    }
}




