package com.louwei.gptresource.controller;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.service.ChatOrdersService;
import com.louwei.gptresource.vo.ChatOrderReqVo;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ChatOrdersService chatOrdersService;

    @GetMapping("/order/{orderId}")
    public AjaxResult placeOrder(@PathVariable("orderId") String orderId) {
        System.out.println("开始下单，处理订单数据......");
        MessagePostProcessor message = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setDelay(10000);
                return message;
            }
        };

        rabbitTemplate.convertAndSend("delayed_exchange", "order_routing", orderId, message);
        return AjaxResult.success("下单订单号: " + orderId);
    }

    @GetMapping("/orders")
    public AjaxResult pageResult(@RequestParam Integer page,@RequestParam Integer limit) {
        System.out.println("分页查询，第:" + page + "页");
        return chatOrdersService.pageResult(page, limit);
    }


}
