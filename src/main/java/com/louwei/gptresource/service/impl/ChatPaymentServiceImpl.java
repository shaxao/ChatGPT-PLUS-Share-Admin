package com.louwei.gptresource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatOrders;
import com.louwei.gptresource.domain.ChatPayment;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.domain.PayReturn;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.enums.OrderStatus;
import com.louwei.gptresource.mapper.ChatOrdersMapper;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import com.louwei.gptresource.service.ChatPaymentService;
import com.louwei.gptresource.mapper.ChatPaymentMapper;
import com.louwei.gptresource.vo.ChatPaymentReqVo;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.PayReturnVo;
import com.louwei.gptresource.vo.admin.user.AdminPayTempVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【chat_payment】的数据库操作Service实现
* @createDate 2024-04-26 12:01:05
*/
@Service
@Slf4j
public class ChatPaymentServiceImpl extends ServiceImpl<ChatPaymentMapper, ChatPayment>
    implements ChatPaymentService {
    @Autowired
    private ChatPaymentMapper chatPaymentMapper;
    @Autowired
    private ChatOrdersMapper chatOrdersMapper;
    @Autowired
    private ChatUsersMapper chatUsersMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<ChatPaymentReqVo> findEnablePay() {
        List<ChatPayment> chatPayments = chatPaymentMapper.selectList(new QueryWrapper<ChatPayment>().eq("is_enabled", 1));

        if (CollUtil.isEmpty(chatPayments)) {
            return Collections.emptyList();
        }

        return chatPayments.stream()
                .map(chatPayment -> {
                    ChatPaymentReqVo chatPaymentReqVo = new ChatPaymentReqVo();
                    chatPaymentReqVo.setPaymentName(chatPayment.getPaymentName());
                    chatPaymentReqVo.setId(chatPayment.getId());
                    return chatPaymentReqVo;
                })
                .collect(Collectors.toList());
    }


    @Override
    public PayReturn payOrder(String orderNo, Integer payType, String ipAddress) {
        ChatOrders chatOrders = chatOrdersMapper.selectById(orderNo);
        if (chatOrders != null) {
            log.info("支付订单:{}", chatOrders.getTotalFee());
            ChatPayment chatPayment = chatPaymentMapper.selectById(payType);
            log.info("key:{}", chatPayment.getMerchantSecret());
            Map<String, String> params = new TreeMap<>();
            params.put("pid", chatPayment.getMerchantId());
            params.put("type", chatPayment.getPaymentMethod());
            params.put("out_trade_no", chatOrders.getOrderNo());
            params.put("name", chatOrders.getTitle());
            params.put("money", String.format("%.2f", (double) chatOrders.getTotalFee() / 100));
            params.put("notify_url", chatPayment.getPaymentHandlerRoute());
            // params.put("notify_url", "http://z5hcp5.natappfree.cc/pay/order/notify");
            params.put("device", chatPayment.getPaymentScenario());
            params.put("clientip", ipAddress);
            String sigh = generateSignature(params, chatPayment.getMerchantSecret());
            log.info("sigh:{}", sigh);
            // return toPay(chatOrders,chatPayment,sigh);
            return apiPay(chatOrders,chatPayment,ipAddress,sigh,payType);
        }
       return null;
    }

    /**
     * 处理支付结果
     * @param payReturnVo
     * @return
     */
    @Override
    public int takePayResult(PayReturnVo payReturnVo) {
        ChatOrders chatOrders = chatOrdersMapper.selectById(payReturnVo.getOutTradeNo());
        ChatPayment chatPayment = chatPaymentMapper.selectById(chatOrders.getPayId());
        if(chatOrders != null) {
            //如果数据库更新失败，应该发起退款，并且在前台提醒用户重新购买
            // 更新成功，应该刷新addWechat.html页面，提示支付成功
            Map<String,String> params = new TreeMap<>();
            params.put("pid", String.valueOf(payReturnVo.getPid()));
            params.put("type", payReturnVo.getType());
            params.put("out_trade_no", payReturnVo.getOutTradeNo());
            params.put("name", payReturnVo.getName());
            params.put("trade_no", payReturnVo.getTradeNo());
            params.put("money", payReturnVo.getMoney());
            params.put("trade_status", payReturnVo.getTradeStatus());
            String signature = generateSignature(params, chatPayment.getMerchantSecret());
            log.info("支付回调验证签证:{}",signature);
            if(signature.equals(payReturnVo.getSign())) {
                chatOrders.setOrderStatus(OrderStatus.SUCCESS.getType());
                ChatUsers chatUsers = chatUsersMapper.selectById(chatOrders.getUserId());
                chatUsers.setUserStatus(AllStatus.VIP.getType());
                chatUsers.setUpdateTime(new Date());
                chatUsers.setExpireTime(chatOrders.getExpireTime());
                rabbitTemplate.convertAndSend("chunk_delayed_exchange","chunk_routing",chatUsers.getId(), message -> {
                     message.getMessageProperties().setDelay((int) (chatUsers.getExpireTime().getTime() - chatUsers.getUpdateTime().getTime()));
                     return message;
                });
                return chatOrdersMapper.updateById(chatOrders) > 0 && chatUsersMapper.updateUserRole(chatOrders.getUserId(),2) > 0 && chatUsersMapper.insert(chatUsers) > 0 ? 1 : 0 ;
            }
        }
        // 如果上述操作失败，执行退款
        return refund(chatPayment,chatOrders);
    }

    @Override
    public AjaxResult selectPayList(ListQueryVo listQueryVo) {
        //从参数中查找查询条件
        QueryWrapper<ChatPayment> wrapper = new QueryWrapper<>();
        //1.仅仅分页查询  默认id升序
        //2.用户邮箱分页查询
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getPaymentName()), "payment_name", listQueryVo.getPaymentName());
        //3.日期分类查询
        List<Date> createTime = listQueryVo.getCreateTime();
        if(CollUtil.isNotEmpty(createTime)){
            Date startTime = createTime.get(0);
            Date endTime = createTime.get(1);
            wrapper.ge("create_date",startTime);
            wrapper.le("expire_date",endTime);
        }
        //5.id升序降序分
        if (StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("+id")) {
            wrapper.orderByAsc("id");
        } else {
            wrapper.orderByDesc("id");
        }
        Page<ChatPayment> page = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
        Page<ChatPayment> pages = chatPaymentMapper.selectPage(page, wrapper);
        List<ChatPayment> chatPayments = pages.getRecords();
        return AjaxResult.success("查询成功",chatPayments,pages.getTotal());
    }

    @Override
    public int updatePay(AdminPayTempVo adminPayTempVo) {
        if (adminPayTempVo != null) {
            ChatPayment chatPayment = chatPaymentMapper.selectById(adminPayTempVo.getId());
            if (chatPayment != null) {
                try {
                    BeanUtils.copyProperties(adminPayTempVo, chatPayment);
                } catch (BeansException e) {
                    log.error("BeanUtils.copyProperties error", e);
                    throw new RuntimeException(e);
                }
                chatPayment.setUpdatedAt(new Date());
                chatPayment.setIsEnabled(adminPayTempVo.getIsEnabled() ? 1:0);
                log.info("chatPayment:{}", chatPayment.getIsEnabled());
                return chatPaymentMapper.updateById(chatPayment);
            }
        }
        return 0;
    }

    @Override
    public int createPay(AdminPayTempVo adminPayTempVo) {
        if (adminPayTempVo != null) {
            ChatPayment chatPayment = new ChatPayment();
            BeanUtils.copyProperties(adminPayTempVo, chatPayment);
            chatPayment.setIsEnabled(adminPayTempVo.getIsEnabled() ? 1:0);
            chatPayment.setCreatedAt(new Date());
            return chatPaymentMapper.insert(chatPayment);
        }
        return 0;
    }

    /**
     * 退款处理
     * @return
     */
    public int refund(ChatPayment chatPayment, ChatOrders chatOrders) {
        OkHttpClient client = new OkHttpClient();
        StringBuilder data = new StringBuilder();
        data.append("pid=").append(chatPayment.getMerchantId()).append("&")
                .append("type=").append(chatPayment.getPaymentMethod()).append("&")
                .append("out_trade_no=").append(chatOrders.getOrderNo()).append("&")
                .append("trade_no=").append(chatOrders.getTradeNo()).append("&")
                .append("money=").append(String.format("%.2f", (double) chatOrders.getTotalFee() / 100));
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, data.toString());
        Request request = new Request.Builder()
                .url(chatPayment.getMerchantKey())
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Host", "pay.qyyun.top")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            log.info("responsePay:{}", response);
            if (!response.isSuccessful()) {
                return 0;
            }
            String responseStr = response.body().string();
            JSONObject jsonObject = JSONUtil.parseObj(responseStr);
            return jsonObject.getInt("code") == 1 ? 1 : 0;
        }catch (IOException e) {
            log.error("退款请求失败:{}",e.getMessage());
        }
        return 0;
    }

    /**
     * 直接跳转代码
     * @param chatOrders
     * @param chatPayment
     * @param sigh 签名
     * @return
     */
    public String toPay(ChatOrders chatOrders, ChatPayment chatPayment, String sigh) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("pid", chatPayment.getMerchantId())
                .add("type", chatPayment.getPaymentMethod())
                .add("out_trade_no", chatOrders.getOrderNo())
                .add("notify_url", chatPayment.getPaymentHandlerRoute())
                .add("return_url", "http://localhost:9090/returnUrl.html")
                .add("name", chatOrders.getTitle())
                .add("money", String.format("%.2f", (double)chatOrders.getTotalFee() / 100))
                .add("sign", sigh)
                .add("sign_type", "MD5")
                .build();
        Request request = new Request.Builder()
                .url(chatPayment.getMerchantKey())
                .post(formBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            log.info("responsePay:{}", response);
            if (!response.isSuccessful()) {
                return null;
            }
            // 这里可以根据需要处理响应
            String responseStr = response.body().string();
            log.info("responsePayStr:{}", responseStr);
            JSONObject jsonObject = JSONUtil.parseObj(responseStr);
            int code = (int) jsonObject.get("code");
            if (code == 1) {
                String qrcode = (String) jsonObject.get("payurl");
                return qrcode;
            } else {
                String errorMsg = (String) jsonObject.get("msg");
                return errorMsg;
            }
        } catch (IOException e) {
            log.error("支付请求失败:{}", e.getMessage());
        } finally {
            response.close();
        }
        return null;
    }

    /**
     * API跳转
     *
     * @param chatOrders
     * @param chatPayment
     * @param ipAddress   用户IP
     * @param sigh        签名
     * @param payType
     * @return
     */
    public  PayReturn apiPay(ChatOrders chatOrders, ChatPayment chatPayment, String ipAddress, String sigh, Integer payType) {
        OkHttpClient client = new OkHttpClient();
        StringBuilder data = new StringBuilder();
        data.append("pid=").append(chatPayment.getMerchantId()).append("&")
                .append("type=").append(chatPayment.getPaymentMethod()).append("&")
                .append("out_trade_no=").append(chatOrders.getOrderNo()).append("&")
                .append("notify_url=").append(chatPayment.getPaymentHandlerRoute()).append("&")
                .append("name=").append(chatOrders.getTitle()).append("&")
                .append("money=").append(String.format("%.2f", (double) chatOrders.getTotalFee() / 100)).append("&")
                .append("clientip=").append(ipAddress).append("&")
                .append("device=").append(chatPayment.getPaymentScenario()).append("&")
                .append("sign=").append(sigh).append("&")
                .append("sign_type=MD5");

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, data.toString());
        Request request = new Request.Builder()
                .url(chatPayment.getMerchantKey())
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            log.info("responsePay:{}", response);
            if (!response.isSuccessful()) {
                return null;
            }
            // 这里可以根据需要处理响应
            String responseStr = response.body().string();
            log.info("responsePayStr:{}", responseStr);
            JSONObject jsonObject = null;
            try {
                jsonObject = JSONUtil.parseObj(responseStr);
            } catch (Exception e) {
                log.error("支付返回结果格式化异常：{}", e);
                return null;
            }
            int code = (int) jsonObject.get("code");
            PayReturn payReturn = new PayReturn();
            payReturn.setCode(code);
            if (code == 1) {
                JSONObject finalJsonObject = jsonObject;
                jsonObject.keySet().stream().forEach(key -> {
                    log.info("response key:{}", key);
                    switch (key){
                        case "qrcode":
                            payReturn.setQrcode((String) finalJsonObject.get(key));
                            break;
                        case "payurl":
                            payReturn.setPayUrl((String) finalJsonObject.get(key));
                            break;
                    }
                });
                String tradeNo = (String) jsonObject.get("trade_no");
                chatOrders.setTradeNo(tradeNo);
                chatOrders.setPayId(payType);
                chatOrdersMapper.updateById(chatOrders);
                return payReturn;
            } else {
                payReturn.setMsg((String)jsonObject.get("msg"));
                return payReturn;
            }
        } catch (IOException e) {
            log.error("支付请求失败:{}", e.getMessage());
        } finally {
            response.close();
        }
        return null;
    }

    public String generateSignature(Map<String, String> params, String key)  {
        // Construct signature string
        StringBuilder signStrBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (signStrBuilder.length() > 0) {
                signStrBuilder.append("&");
            }
            signStrBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        signStrBuilder.append(key);

        // MD5 encryption
        byte[] digest = new byte[0];
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(signStrBuilder.toString().getBytes("UTF-8"));
            digest = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // Convert the byte array to hexadecimal string
        StringBuilder hexStrBuilder = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexStrBuilder.append('0');
            }
            hexStrBuilder.append(hex);
        }

        // Return the signature
        return hexStrBuilder.toString();
    }
}




