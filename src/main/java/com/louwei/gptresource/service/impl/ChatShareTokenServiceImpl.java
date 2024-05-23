package com.louwei.gptresource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.config.RabbitConfig;
import com.louwei.gptresource.domain.ChatAccessToken;
import com.louwei.gptresource.domain.ChatShareToken;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.mapper.ChatAccessTokenMapper;
import com.louwei.gptresource.service.ChatShareTokenService;
import com.louwei.gptresource.mapper.ChatShareTokenMapper;
import com.louwei.gptresource.utils.JWTUtil;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminShareSetVo;
import com.louwei.gptresource.vo.admin.user.AdminTokenReqVo;
import kotlin.collections.ArrayDeque;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【chat_share_token(聊天会话Token表)】的数据库操作Service实现
* @createDate 2024-01-04 20:09:52
*/
@Service
@Slf4j
public class ChatShareTokenServiceImpl extends ServiceImpl<ChatShareTokenMapper, ChatShareToken>
    implements ChatShareTokenService{
    @Autowired
    private ChatShareTokenMapper chatShareTokenMapper;
    @Autowired
    private ChatAccessTokenMapper chatAccessTokenMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String findTokenUse() {
        AdminShareSetVo adminShareSetVo = new AdminShareSetVo();
        // 设置自动的token有效期为一天
        adminShareSetVo.setExpiry(60 * 60 * 24);
        adminShareSetVo.setName(UUID.fastUUID().toString());
        adminShareSetVo.setGpt4Uses(-1);
        adminShareSetVo.setGpt35Uses(-1);
        adminShareSetVo.setSessionIsolation(false);
        adminShareSetVo.setRestrictedSites("");
        String key = UUID.randomUUID().toString(true);
        refreshShareToken(adminShareSetVo, key);
        String shareToken = redisTemplate.opsForValue().get("shareToken:gen:" + key).toString();
        log.info("gen shareToken:{}", shareToken);
        return shareToken;
//        QueryWrapper<ChatShareToken> wrapper = new QueryWrapper<>();
//        wrapper.eq("token_status", AllStatus.NORMAL.getType());
//        wrapper.le("user_count",5);
//        Date now = new Date();
//        wrapper.ge("expire_date",now);
//        wrapper.last("LIMIT 1");  // 获取符合条件的第一个数据
//        ChatShareToken chatShareToken = chatShareTokenMapper.selectOne(wrapper);
//        if(chatShareToken != null) {
//            // System.out.println("shareToken:" + chatShareToken.toString());
//            Integer userCount = chatShareToken.getUserCount();
//            chatShareToken.setUserCount(userCount + 1);
//            chatShareTokenMapper.updateById(chatShareToken);
//            String token = chatShareToken.getToken();
//            // String sitePassword = chatShareToken.getSitePassword();
//            return token;
//        }
//       return null;
    }

    @Override
    public String getPanroraUrl() {
        String token = findTokenUse();
        if(token == null || token.equals("")) {
            return null;
        }
        OkHttpClient client = new OkHttpClient();

        String json = "{\"share_token\": " + "\"" + token + "\"" + "}";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url("https://openai.qipusong.site/api/auth/oauth_token")
                .header("Origin", "https://openai.qipusong.site")
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("错误代码 " + response);
            String responseBody = response.body().string();
            // System.out.println("responseBody: " + responseBody);
            JSONObject jsonObject = new JSONObject(responseBody);
            String loginUrl = jsonObject.getStr("login_url");
            // System.out.println("login_url:" + loginUrl);
            return loginUrl;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("pandora获取鉴权url异常: " + e);
        }
        return null;
    }

    @Override
    public AjaxResult selectChunkPage(ListQueryVo listQueryVo) {
        System.out.println("进入查询shareToken分页数据mapper");
        //从参数中查找查询条件
        QueryWrapper<ChatShareToken> wrapper = new QueryWrapper<>();
        //1.仅仅分页查询  默认id升序
        //2.用户邮箱分页查询
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getUserEmail()), "email", listQueryVo.getUserEmail());
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getSort()) && !listQueryVo.getSort().contains("id"), "token_status", listQueryVo.getSort());
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
        Page<ChatShareToken> page = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
        Page<ChatShareToken> pages = chatShareTokenMapper.selectPage(page, wrapper);
        List<ChatShareToken> chatShareTokens = pages.getRecords();
        List<AdminTokenReqVo> adminTokenReqVos = new ArrayDeque<>();
        for (ChatShareToken chatShareToken:chatShareTokens) {
            AdminTokenReqVo adminShareTokenReqVo = new AdminTokenReqVo();
            System.out.println("chatSharerToken:" + chatShareToken.toString());
            BeanUtils.copyProperties(chatShareToken,adminShareTokenReqVo);
            adminTokenReqVos.add(adminShareTokenReqVo);
        }
        return AjaxResult.success("查询成功",adminTokenReqVos,pages.getTotal());
    }

    @Override
    public int updateShareToken(AdminTokenReqVo adminTokenReqVo) {
        if(adminTokenReqVo.getId() != null && chatShareTokenMapper.selectById(adminTokenReqVo.getId()) != null){
            ChatShareToken chatShareToken = new ChatShareToken();
            BeanUtils.copyProperties(adminTokenReqVo,chatShareToken);
            int result = chatShareTokenMapper.updateById(chatShareToken);
            if (result > 0) {
                long delay = adminTokenReqVo.getExpireDate().getTime() - System.currentTimeMillis();
                // 提交token队列
                rabbitTemplate.convertAndSend("token_delayed_exchange","token_routing",adminTokenReqVo.getId(), message -> {
                    message.getMessageProperties().setDelay((int)delay);
                    return message;
                });
                return result;
            }
        }
        return 0;
    }

    @Override
    public int createShareToken(@NonNull AdminTokenReqVo adminTokenReqVo) {
        ChatShareToken chatShareToken = new ChatShareToken();
        BeanUtils.copyProperties(adminTokenReqVo,chatShareToken);
        chatShareToken.setCreateDate(new Date());
        chatShareToken.setUserCount(0);
        int result = chatShareTokenMapper.insert(chatShareToken);
        if(result > 0){
            Long id = chatShareToken.getId();
            log.info("share token创建成功:{}",id);
            long delay = adminTokenReqVo.getExpireDate().getTime() - System.currentTimeMillis();
            log.info("delay:{}",delay);
            // 提交token队列
            rabbitTemplate.convertAndSend("token_delayed_exchange","token_routing",id, message -> {
                message.getMessageProperties().setDelay((int)delay);
                return message;
            });
            return result;
        }
        return 0;
    }

    /**
     * 刷新token
     * @param adminShareSetVo
     * @return
     */
    @Override
    public AjaxResult refreshShareToken(AdminShareSetVo adminShareSetVo,String username) {
        // 1.找到对应的access_token
        long delay = adminShareSetVo.getExpiry() == 0 ? 60 * 60 * 24 * 10 : adminShareSetVo.getExpiry();
        QueryWrapper<ChatAccessToken> wrapper = new QueryWrapper<>();
        long id = -1;
        ChatShareToken chatShareToken = null;
        // id不存在，那就是生成，需要生成一个新的share_token
        if(adminShareSetVo.getId() != null) {
            chatShareToken = chatShareTokenMapper.selectById(adminShareSetVo.getId());
            if(chatShareToken != null) {
                id = chatShareToken.getAid();
            }
        }
        wrapper.eq(id >= 0, "id", id);
        wrapper.eq("token_status", AllStatus.NORMAL.getType());
        wrapper.ge("expire_date", new Date());
        wrapper.orderByAsc("user_count").last("LIMIT 1");
        ChatAccessToken chatAccessToken = chatAccessTokenMapper.selectOne(wrapper);
        if(chatAccessToken != null) {
            try {
                if(!redisTemplate.hasKey("accessToken:userCount")) {
                    redisTemplate.opsForValue().set("accessToken:userCount", "0");
                }
                String userCountStr = redisTemplate.opsForValue().get("accessToken:userCount");
                log.info("userCountStr:{}", userCountStr);
                // 如果最小值和当前最大值相等，说明目前所有账号都达到当前轮询使用上限，更新数据库和缓存+1继续进行下一次轮询
                if(String.valueOf(chatAccessToken.getUserCount()).equals(userCountStr)) {
                    redisTemplate.opsForValue().increment("accessToken:userCount");
                }
            } catch (Exception e) {
                log.error("redis处理userCount错误:{}", e);
            }
            String accessToken = chatAccessToken.getToken();
            chatAccessToken.setUserCount(chatAccessToken.getUserCount() + 1);
            chatAccessTokenMapper.updateById(chatAccessToken);
            // 创建OkHttpClient实例
            OkHttpClient client = new OkHttpClient();

            // 创建RequestBody，以表单形式提交数据
            RequestBody formBody = new FormBody.Builder()
                    .add("access_token", accessToken)
                    .add("expires_in", String.valueOf(adminShareSetVo.getExpiry()))
                    .add("unique_name", adminShareSetVo.getName())
                    .add("gpt35_limit", String.valueOf(adminShareSetVo.getGpt35Uses()))
                    .add("gpt4_limit", String.valueOf(adminShareSetVo.getGpt4Uses()))
                    .add("show_conversations", String.valueOf(adminShareSetVo.getSessionIsolation()))
                    .add("site_limit", adminShareSetVo.getRestrictedSites())
                    .build();

            // 创建Request对象
            Request request = new Request.Builder()
                    .url("https://chat.oaifree.com/token/register")
                    .post(formBody)
                    .build();

            // 发送请求并获取响应
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                   return null;
                }
                // 打印响应体的字符串
                String shareStr = response.body().string();
                log.info("shareStr:{}",shareStr);
                String shareToken = (String) JSONUtil.parseObj(shareStr).get("token_key");
                LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(delay);
                // 将 LocalDateTime 转换为 ZonedDateTime
                ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
                // 将 ZonedDateTime 转换为 Instant
                Instant instant = zonedDateTime.toInstant();
                if(id >= 0) {
                    chatShareToken.setAid(chatAccessToken.getId());
                    chatShareToken.setToken(shareToken);
                    chatShareToken.setExpireDate(Date.from(instant));
                    chatShareToken.setCreator(username);
                    chatShareToken.setTokenStatus(AllStatus.NORMAL.getType());
                    chatShareToken.setUserCount(0);
                    chatShareToken.setEmail(chatAccessToken.getEmail() != null ? chatAccessToken.getEmail() : null);
                    int result = chatShareTokenMapper.updateById(chatShareToken);
                    if(result > 0) {
                        AdminTokenReqVo adminTokenReqVo = new AdminTokenReqVo();
                        BeanUtils.copyProperties(chatShareToken,adminTokenReqVo);
                        rabbitTemplate.convertAndSend("token_delayed_exchange","token_routing",chatShareToken.getId(), message -> {
                            message.getMessageProperties().setDelay((int)delay * 1000);
                            return message;
                        });
                        return AjaxResult.success("刷新成功",adminTokenReqVo);
                    }else {
                        return AjaxResult.fail("刷新失败");
                    }

                }
                // 为了榨干username的使用价值
                redisTemplate.opsForValue().set("shareToken:gen:" + username, shareToken, 10, TimeUnit.SECONDS);
                // 如果是生成的，那么新建一个shareToken
                chatShareToken = new ChatShareToken();
                chatShareToken.setTokenStatus(AllStatus.NORMAL.getType());
                chatShareToken.setAid(chatAccessToken.getId());
                chatShareToken.setUserCount(0);
                chatShareToken.setCreateDate(new Date());
                chatShareToken.setEmail(chatAccessToken.getEmail() != null ? chatAccessToken.getEmail() : null);
                chatShareToken.setToken(shareToken);
                chatShareToken.setExpireDate(Date.from(instant));
                int result = chatShareTokenMapper.insert(chatShareToken);
                if(result > 0) {
                    rabbitTemplate.convertAndSend("token_delayed_exchange","token_routing",chatShareToken.getId(), message -> {
                        message.getMessageProperties().setDelay((int)delay * 1000);
                        return message;
                    });
                }
                return AjaxResult.toAjax(result,"生成成功","生成失败");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("shareToken请求获取数据失败:{}", e);
            }
        }
        return AjaxResult.fail("操作失败");
    }
}




