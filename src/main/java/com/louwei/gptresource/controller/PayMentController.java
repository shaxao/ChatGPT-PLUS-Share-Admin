package com.louwei.gptresource.controller;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatOrders;
import com.louwei.gptresource.domain.ChatPayment;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.domain.PayReturn;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.enums.OrderStatus;
import com.louwei.gptresource.service.ChatOrdersService;
import com.louwei.gptresource.service.ChatPaymentService;
import com.louwei.gptresource.service.ChatProductService;
import com.louwei.gptresource.service.ChatUsersService;
import com.louwei.gptresource.utils.RequestUtils;
import com.louwei.gptresource.vo.ChatOrderRepVo;
import com.louwei.gptresource.vo.ChatProductDetailReqVo;
import com.louwei.gptresource.vo.PayReturnVo;
import io.swagger.models.auth.In;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pay/order")
@Slf4j
public class PayMentController {
    @Autowired
    private ChatOrdersService chatOrdersService;
    @Autowired
    private ChatUsersService chatUsersService;
    @Autowired
    private ChatProductService chatProductService;
    @Autowired
    private ChatPaymentService chatPaymentService;

    /**
     * 创建订单
     * @param chatProductDetailReqVo
     * @return
     */
    @PostMapping("/createOrder")
    public AjaxResult createOrder(@RequestBody ChatProductDetailReqVo chatProductDetailReqVo){
        System.out.println("开始创建订单:" + chatProductDetailReqVo.toString());
        String username = chatProductDetailReqVo.getUsername();
        ChatUsers chatUser = chatUsersService.findUserByUserName(username);
        if (chatUser == null){
            return AjaxResult.fail("用户不存在");
        }
        Integer quantity = chatProductDetailReqVo.getQuantity();
        //更新库存
        ChatProductDetailReqVo detail = chatProductService.findDetailById(chatProductDetailReqVo.getProductId(),quantity);
        if(detail == null){
            return AjaxResult.fail("商品不存在、库存不足或者商品已下架");
        }else if (detail.getStock() - chatProductDetailReqVo.getQuantity() < 0){
            return AjaxResult.fail("库存不足");
        }
        // 时间管理监控订单超时未支付修改订单为超时未支付
        //创建订单
        ChatOrderRepVo chatOrderRepVo = chatOrdersService.createOrder(chatProductDetailReqVo,chatUser.getId());
        return AjaxResult.success(chatOrderRepVo);
    }

    /**
     * 取消订单
     * @param orderNo
     * @return
     */
    @GetMapping("/cancelOrder")
    public AjaxResult cancelOrder(@RequestParam("orderNo")@NonNull String orderNo){
        System.out.println("处理取消订单："+orderNo);
        boolean suceess = chatOrdersService.cancelOrder(orderNo);
        if (suceess){
            return AjaxResult.success("取消成功");
        }
        return AjaxResult.fail("订单已支付或者不存在");
    }

    @GetMapping("/pay")
    @ResponseBody
    public AjaxResult payOrder(@RequestParam("orderNo")@NonNull String orderNo, @RequestParam(value = "payType") Integer payType, HttpServletRequest request){
        System.out.println("支付订单："+orderNo + ",支付方式:" + payType);
        String ipAddress = RequestUtils.getClientIpAddress(request);
        log.info("ipAddress:{}", ipAddress);
        PayReturn payReturn = chatPaymentService.payOrder(orderNo,payType,ipAddress);
        //String payUrl = "https://qr.95516.com/00010048/unifiedNative?token=41ce6be61a4a53d55c925e5f5fa555ef&target=qfs&unit=shs1";
        return payReturn == null ? AjaxResult.fail("请求失败，请重试") : AjaxResult.success(payReturn);
    }

    @GetMapping("/checkPaymentStatus")
    public AjaxResult checkPaymentStatus(@RequestParam("orderNo") String orderNo) {
        String orderStatus = chatOrdersService.checkOrderStatusByOrderNo(orderNo);
        //String orderStatus = OrderStatus.SUCCESS.getType();
        return AjaxResult.success(orderStatus, orderStatus);
    }

    /**
     * 支付回调处理
     * @param payReturnVo
     * @return
     */
    @GetMapping("/notify")
    public String yiPayReturn(@ModelAttribute PayReturnVo payReturnVo){
        log.info("支付回调："+payReturnVo.toString());
        if(payReturnVo.getTradeStatus().equals("TRADE_SUCCESS")) {
            chatPaymentService.takePayResult(payReturnVo);
            return "success";
        }
        return "success";
    }
}
