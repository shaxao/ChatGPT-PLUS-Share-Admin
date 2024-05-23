package com.louwei.gptresource.controller.admin.order;


import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.service.ChatOrdersService;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.TempVo;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vue-element-admin/order")
public class AdminOrderController {
    @Autowired
    private ChatOrdersService chatOrdersService;

    /**
     * 后台管理查询订单
     * @param listQueryVo
     * @return
     */
    @GetMapping("/list")
    public AjaxResult listOrderPage(@ModelAttribute ListQueryVo listQueryVo){
        System.out.println("开始执行后台管理查询订单数据模块:" + listQueryVo.getPage());
        AjaxResult ajaxResult = chatOrdersService.selectOrderPage(listQueryVo);
        return ajaxResult;
    }

    /**
     * 后台管理更新订单
     * @param tempVo
     * @return
     */
    @PostMapping("/update")
    public AjaxResult updateOrderPage(@RequestBody @NonNull TempVo tempVo){
        System.out.println("开始执行后台管理修改订单数据模块:" + tempVo.getOrderNo());
        int result = chatOrdersService.updateOrderPage(tempVo);
        return AjaxResult.toAjax(result);
    }


    /**
     * 后台创建订单
     * @param tempVo
     * @return
     */
    @PostMapping("/create")
    public AjaxResult createOrderPage(@RequestBody @NonNull TempVo tempVo){
        System.out.println("开始执行后台管理创建订单数据模块:" + tempVo.getOrderNo());
        int result = chatOrdersService.createOrderPage(tempVo);
        return AjaxResult.toAjax(result);
    }

    @GetMapping("/transaction/list")
    public AjaxResult listTransactionPage(@ModelAttribute ListQueryVo listQueryVo){
        System.out.println("开始执行后台管理查询订单数据模块:" + listQueryVo.getPage());
        AjaxResult ajaxResult = chatOrdersService.selectOrderPage(listQueryVo);
        return ajaxResult;
    }



}
