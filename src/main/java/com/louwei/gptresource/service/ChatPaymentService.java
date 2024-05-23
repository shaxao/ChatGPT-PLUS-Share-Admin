package com.louwei.gptresource.service;

import cn.hutool.json.JSONObject;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatPayment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.louwei.gptresource.domain.PayReturn;
import com.louwei.gptresource.vo.ChatPaymentReqVo;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.PayReturnVo;
import com.louwei.gptresource.vo.admin.user.AdminPayTempVo;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【chat_payment】的数据库操作Service
* @createDate 2024-04-26 12:01:05
*/
public interface ChatPaymentService extends IService<ChatPayment> {


    List<ChatPaymentReqVo> findEnablePay();

    PayReturn payOrder(String orderNo, Integer payType, String ipAddress);

    String generateSignature(Map<String,String> params,String merchantSecret);

    int takePayResult(PayReturnVo payReturnVo);

    AjaxResult selectPayList(ListQueryVo listQueryVo);

    int updatePay(AdminPayTempVo adminPayTempVo);

    int createPay(AdminPayTempVo adminPayTempVo);
}
