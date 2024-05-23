package com.louwei.gptresource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatAccessToken;
import com.louwei.gptresource.domain.ChatAccount;
import com.louwei.gptresource.domain.ChatSessionToken;
import com.louwei.gptresource.domain.ChatShareToken;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.mapper.ChatAccessTokenMapper;
import com.louwei.gptresource.mapper.ChatSessionTokenMapper;
import com.louwei.gptresource.mapper.ChatShareTokenMapper;
import com.louwei.gptresource.service.ChatAccountService;
import com.louwei.gptresource.mapper.ChatAccountMapper;
import com.louwei.gptresource.utils.RequestUtils;
import com.louwei.gptresource.vo.AccountTempVo;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.admin.user.AdminAccountReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【chat_account(用户账号表)】的数据库操作Service实现
* @createDate 2024-01-20 10:01:48
*/
@Service
@Slf4j
public class ChatAccountServiceImpl extends ServiceImpl<ChatAccountMapper, ChatAccount>
    implements ChatAccountService{
    @Autowired
    private ChatAccountMapper chatAccountMapper;
    @Autowired
    private ChatSessionTokenMapper chatSessionTokenMapper;
    @Autowired
    private ChatAccessTokenMapper chatAccessTokenMapper;
    @Autowired
    private ChatShareTokenMapper chatShareTokenMapper;
    @Autowired
    private RequestUtils requestUtils;
    @Value("${base.url}")
    private String baseUrl;
    @Value("${token.access.uri}")
    private String uri;
    @Value("${token.proxy_api_prefix}")
    private String prefix;

    @Override
    public AjaxResult selectAccountPage(ListQueryVo listQueryVo) {
        System.out.println("后台管理开始查询账号:" + listQueryVo.getPage());
        //从参数中查找查询条件
        try {
            QueryWrapper<ChatAccount> wrapper = new QueryWrapper<>();
            //1.仅仅分页查询  默认id升序
            //2.用户邮箱分页查询
            wrapper.eq(StrUtil.isNotBlank(listQueryVo.getUserEmail()), "email", listQueryVo.getUserEmail());
            //3.创还能日期分类查询
            List<Date> createTime = listQueryVo.getCreateTime();
            if(CollUtil.isNotEmpty(createTime)){
                Date startTime = createTime.get(0);
                Date endTime = createTime.get(1);
                wrapper.ge("create_time",startTime);
                wrapper.le("expire_time",endTime);
            }
            wrapper.eq(StrUtil.isNotBlank(listQueryVo.getStatus()),"status",listQueryVo.getStatus());
            wrapper.eq(StrUtil.isNotBlank(listQueryVo.getSort()) && !listQueryVo.getSort().contains("id"), "status", listQueryVo.getSort());
            //5.id升序降序分
            if (StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("+id")) {
                wrapper.orderByAsc("id");
            } else {
                wrapper.orderByDesc("id");
            }
            Page<ChatAccount> page = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
            Page<ChatAccount> chatAccountPage = chatAccountMapper.selectPage(page, wrapper);
            List<ChatAccount> chatAccounts = chatAccountPage.getRecords();
            List<AdminAccountReqVo> adminAccountReqVos = new ArrayList<>();
            for (ChatAccount chatAccount:chatAccounts) {
                AdminAccountReqVo accountReqVo = new AdminAccountReqVo();
                BeanUtils.copyProperties(chatAccount,accountReqVo);
                adminAccountReqVos.add(accountReqVo);
            }
            return AjaxResult.success("账号查询成功",adminAccountReqVos,page.getTotal());
        }catch (Exception e){
           log.info("账号查询失败:" + e);
           e.printStackTrace();
        }
        return AjaxResult.fail("账号查询失败");
    }

    /**
     * 更新账号信息
     *
     * @param tempVo
     * @param configFile
     * @return
     */
    @Override
    @Transactional
    public int updateAccount(AccountTempVo tempVo, String configFile) {
        int result = 0;
        ChatAccount chatAccount = chatAccountMapper.selectById(tempVo.getId());
        if(chatAccount == null){
            return 0;
        }
       // result = updateConfigFile(tempVo, configFile, null, null);
        BeanUtils.copyProperties(tempVo,chatAccount);
        result = chatAccountMapper.updateById(chatAccount);
        // 如果账号变动，与之相关的token状态也要进行修改
        if(!tempVo.getStatus().equals(AllStatus.NORMAL.getType())) {
            List<ChatAccessToken> chatAccessTokens = chatAccessTokenMapper.selectList(new QueryWrapper<ChatAccessToken>().eq("email", tempVo.getEmail()));
            List<ChatShareToken> chatShareTokens = chatShareTokenMapper.selectList(new QueryWrapper<ChatShareToken>().eq("email", tempVo.getEmail()));
            ExecutorService executorService = Executors.newFixedThreadPool(2);

            Runnable updateChatAccessTokensTask = () -> {
                chatAccessTokens.forEach(chatAccessToken -> {
                    chatAccessToken.setTokenStatus(tempVo.getStatus());
                    chatAccessTokenMapper.updateById(chatAccessToken);
                    });
               };

            Runnable updateChatShareTokensTask = () -> {
                chatShareTokens.forEach(chatShareToken -> {
                    chatShareToken.setTokenStatus(tempVo.getStatus());
                    chatShareTokenMapper.updateById(chatShareToken);
                });
            };

            executorService.submit(updateChatShareTokensTask);
            executorService.submit(updateChatAccessTokensTask);

            // 关闭线程池
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("更新任务被中断", e);
            }
        }
        return result;
    }

    /**
     * 刷新token
     *
     * @param id
     * @param configFile
     * @return
     */
    @Override
    public int refreshSessionToken(Integer id, String configFile) {
        //由于该刷新操作会返回access_token和session_token，因此需要同步这两张表的内容
        int result = 0;
        String url = baseUrl + prefix + uri;
        ChatAccount chatAccount = chatAccountMapper.selectById(id);
        if(chatAccount == null){
            return result;
        }
        StringBuilder responseText = requestUtils.getToken(url, chatAccount.getEmail(), chatAccount.getPassword());
        JSONObject jsonObject = new JSONObject(responseText);
        String accessToken = jsonObject.getStr("access_token");
        String sessionToken = jsonObject.getStr("session_token");
        if(accessToken == null || sessionToken == null){
            return result;
        }
        result = createToken(sessionToken,chatAccount,accessToken);
        // 更新配置文件token
        File file = new File(configFile);
        JSONObject oldJson = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
        JSONObject updateJson = (JSONObject) oldJson.get(chatAccount.getName());
        updateJson.put("access_token",accessToken);
        updateJson.put("session_token",sessionToken);
        try {
            FileUtil.writeString(oldJson.toStringPretty(),configFile,StandardCharsets.UTF_8);
            result = 1;
        }catch (Exception e){
            result = 0;
            e.printStackTrace();
            log.error("账号配置文件更新失败" + e);
        }
        System.out.println("responseText:" + responseText.toString());
//                    String tokenKey = (String) jsonObject.get("token_key");
        System.out.println("accessToken:" + accessToken);
        System.out.println("sessionToken:" + sessionToken);
        return result;
    }

    @Override
    public int createAccount(AccountTempVo accountTempVo) {
        int result = 0;
       // String url = baseUrl + prefix + uri;
        Optional<ChatAccount> chatAccount = Optional.ofNullable(chatAccountMapper.selectById(accountTempVo.getId()));
        if(chatAccount.isPresent()){
            return result;
        }
        ChatAccount account = new ChatAccount();
        BeanUtils.copyProperties(accountTempVo,account);
        //创建账号数据
        result = chatAccountMapper.insert(account);
        // 如果是share，生成token,并且同步添加到session_token以及access_token，最后更新配置文件
       // StringBuilder tokens = requestUtils.getToken(url, account.getEmail(), account.getPassword());
//        JSONObject jsonObject = new JSONObject(tokens);
//        String accessToken = jsonObject.getStr("access_token");
//        String sessionToken = jsonObject.getStr("session_token");
//        if(accessToken == null || sessionToken == null){
//            return result;
//        }
//        if(accountTempVo.getIsShare()){
//            result = createToken(sessionToken,account,accessToken);
//        }
      //   result = updateConfigFile(accountTempVo,configFile,sessionToken,accessToken);
        return result;
    }

    /**
     * 更新配置文件
     * @param accountTempVo
     */
    private int updateConfigFile(AccountTempVo accountTempVo,String configFile,String sessionToken,String accessToken) {
        File file = new File(configFile);
        JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
        String name = String.valueOf(jsonObject.get(accountTempVo.getName()));
        if(StrUtil.isBlank(name)){
            //如果不存在，那就新建
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("token",sessionToken);
            dataMap.put("access_token",accessToken);
            dataMap.put("username",accountTempVo.getName());
            dataMap.put("userPassword",accountTempVo.getPassword());
            dataMap.put("share",accountTempVo.getIsShare());
            dataMap.put("show_user_info",accountTempVo.getIsShowInto());
            dataMap.put("plus",accountTempVo.getIsPlus());
            JSONObject dataJson = new JSONObject();
            dataJson.putAll(dataJson);
            jsonObject.putOnce(accountTempVo.getName(),dataJson);
            try {
                FileUtil.writeString(jsonObject.toStringPretty(),configFile,StandardCharsets.UTF_8);
                return 1;
            }catch (Exception e){
                e.printStackTrace();
                log.error("账号配置文件创建失败" + e);
            }
        }else {
            // 如果存在，那么更新配置文件
            JSONObject oldJson = (JSONObject) jsonObject.get(accountTempVo.getName());
            putIfNotNull(oldJson, "token", sessionToken);
            putIfNotNull(oldJson, "access_token", accessToken);
            putIfNotNull(oldJson, "username", accountTempVo.getName());
            putIfNotNull(oldJson, "userPassword", accountTempVo.getPassword());
            putIfNotNull(oldJson, "share", accountTempVo.getIsShare());
            putIfNotNull(oldJson, "show_user_info", accountTempVo.getIsShowInto());
            putIfNotNull(oldJson, "plus", accountTempVo.getIsPlus());
            try {
                FileUtil.writeString(jsonObject.toStringPretty(),configFile,StandardCharsets.UTF_8);
                return 1;
            }catch (Exception e){
                e.printStackTrace();
                log.error("账号配置文件更新失败" + e);
            }
        }
        return 0;
    }

    private void putIfNotNull(JSONObject jsonObject, String key, Object value) {
        if (value != null) {
            jsonObject.put(key, value);
        }
    }


    /**
     * session_token生成或更新
     * @param sessionToken
     * @return
     */
    private int createToken(String sessionToken,ChatAccount chatAccount,String accessToken){
        int result = 0;
        //同步session_token和access_token表的内容
        // 始终更新或创建新的 ChatSessionToken  不管是不是空的，都创建新的session_token
        ChatSessionToken chatSessionToken = Optional.ofNullable(chatAccount.getSid())
                .map(chatSessionTokenMapper::selectById)
                .orElse(new ChatSessionToken());

        // 设置通用属性
        Date now = new Date();
        chatSessionToken.setToken(sessionToken);
        chatSessionToken.setCreateDate(now);
        chatSessionToken.setExpireDate(DateUtil.offsetMonth(now, 3));
        chatSessionToken.setEmail(chatAccount.getEmail());
        chatSessionToken.setUserCount(0);
        chatSessionToken.setTokenStatus(AllStatus.NORMAL.getType());
        ChatAccessToken chatAccessToken = Optional.ofNullable(chatSessionToken.getAid())
                .map(chatAccessTokenMapper::selectById)
                .orElse(new ChatAccessToken());
        chatAccessToken.setToken(accessToken);
        chatAccessToken.setEmail(chatAccount.getEmail());
        chatAccessToken.setTokenStatus(AllStatus.NORMAL.getType());
        chatAccessToken.setCreateDate(now);
        chatAccessToken.setExpireDate(DateUtil.offsetDay(now,10));
        // 如果session_token的aid为空，那么添加id然后再access_token添加数据返回id后添加数据
        if(chatSessionToken.getAid() == null){
            result = chatAccessTokenMapper.insert(chatAccessToken);
            chatSessionToken.setAid(chatAccessToken.getId());
            result = chatSessionTokenMapper.insert(chatSessionToken);
        }else {
            result = chatAccessTokenMapper.updateById(chatAccessToken);
            result = chatSessionTokenMapper.updateById(chatSessionToken);
        }
        // 最后更新账号的id
        if (chatAccount.getSid() == null) {
            chatAccount.setSid(chatSessionToken.getId());
            result = chatAccountMapper.updateById(chatAccount);
        }
      return result;
    }
}




