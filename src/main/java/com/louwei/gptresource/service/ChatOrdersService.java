package com.louwei.gptresource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatOrders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.louwei.gptresource.vo.*;

import java.util.List;

/**
* @author Administrator
* @description 针对表【chat_orders】的数据库操作Service
* @createDate 2023-12-31 16:21:55
*/
public interface ChatOrdersService extends IService<ChatOrders> {

    ChatOrderRepVo createOrder(ChatProductDetailReqVo detailReqVo, Integer id);

    Page<ChatOrders> findOrderByUsername(String username);

    boolean refrshCountById(Long id);

    boolean cancelOrder(String orderNo);

    AjaxResult selectOrderPage(ListQueryVo listQueryVo);

    int updateOrderPage(TempVo tempVo);

    int createOrderPage(TempVo tempVo);

    String checkUserStatus(Integer id);

    AjaxResult pageResult(Integer page, Integer limit);

    List<ChatOrderReqVo> chatOrderToReq(List<ChatOrders> chatOrdersList);

    String checkOrderStatusByOrderNo(String orderNo);
}
