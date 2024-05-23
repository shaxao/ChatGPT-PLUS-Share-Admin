package com.louwei.gptresource.controller.admin.pay;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.service.ChatPaymentService;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminPayTempVo;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vue-element-admin/pay")
public class AdminPayController {

    @Autowired
    private ChatPaymentService chatPaymentService;

    @GetMapping("/list")
    public AjaxResult listPay(@ModelAttribute ListQueryVo listQueryVo) {
        System.out.println("开始执行后台支付方式查询订单数据模块:" + listQueryVo.getPage());
        return chatPaymentService.selectPayList(listQueryVo);
    }

    @PostMapping("/update")
    public AjaxResult updatePay(@RequestBody @NonNull AdminPayTempVo adminPayTempVo) {
        System.out.println("开始执行后台支付方式更新模块:" + adminPayTempVo.toString());
        return AjaxResult.toAjax(chatPaymentService.updatePay(adminPayTempVo));
    }

    @PostMapping("/create")
    public AjaxResult createPay(@RequestBody @NonNull AdminPayTempVo adminPayTempVo) {
        System.out.println("开始执行后台支付方式创建模块:" + adminPayTempVo.getPaymentName());
        return AjaxResult.toAjax(chatPaymentService.createPay(adminPayTempVo));
    }
}
