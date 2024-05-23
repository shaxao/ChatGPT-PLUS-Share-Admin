package com.louwei.gptresource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatRole;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.mapper.ChatRoleMapper;
import com.louwei.gptresource.service.ChatUsersService;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.TempVo;
import com.louwei.gptresource.vo.admin.user.AdminUserReqVo;
import kotlin.collections.ArrayDeque;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【chat_users(用户表)】的数据库操作Service实现
* @createDate 2023-12-25 16:01:13
*/
@Service
@Slf4j
public class ChatUsersServiceImpl extends ServiceImpl<ChatUsersMapper, ChatUsers>
    implements ChatUsersService{
    @Autowired
    private ChatUsersMapper chatUsersMapper;
    @Autowired
    private ChatRoleMapper chatRoleMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public int findByUserName(String userName) {
        QueryWrapper<ChatUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", userName);
        ChatUsers user_name = chatUsersMapper.selectOne(wrapper);
        if(user_name == null){
            return 0;
        }
        return 1;
    }

    @Override
    public int findByphone(String phone) {
        QueryWrapper<ChatUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("user_phone", phone);
        ChatUsers user_name = chatUsersMapper.selectOne(wrapper);
        if(user_name == null){
            return 0;
        }
        return 1;
    }

    @Override
    public int findByemail(String email) {
        QueryWrapper<ChatUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("user_email", email);
        ChatUsers user_name = chatUsersMapper.selectOne(wrapper);
        if(user_name == null){
            return 0;
        }
        if(user_name.getUserStatus().equals("未验证")){
            return 0;
        }
        return 1;
    }

    /**
     * 发送验证码，先记录，等待验证
     * @param toUserEmail
     */
    @Override
    public int createUsersByMail(String toUserEmail,String code) {
        ChatUsers chatUsers = new ChatUsers();
        chatUsers.setUserEmail(toUserEmail);
        chatUsers.setCode(code);
        //设置用户为未验证状态
        chatUsers.setUserStatus(AllStatus.NOTVER.getType());
        Date now  = new Date();
        Date tenMinuteAfter = DateUtil.offsetMinute(now, 10);
        chatUsers.setCodeExpireTime(tenMinuteAfter);
        int insert = chatUsersMapper.insert(chatUsers);
        System.out.println("insert:" + insert);
        return insert;
    }

    @Override
    public ChatUsers findByEmail(String phoneOrEmail) {
        QueryWrapper<ChatUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("user_email",phoneOrEmail);
        ChatUsers chatUsers = chatUsersMapper.selectOne(wrapper);
        if(chatUsers != null){
            return chatUsers;
        }
        return null;
    }

    @Override
    public ChatUsers findUserByUserName(String username) {
        ChatUsers chatUsers = chatUsersMapper.selectOne(new QueryWrapper<ChatUsers>().eq("user_name", username));
        if(chatUsers != null){
            return chatUsers;
        }
        return null;
    }

    /**
     * 管理系统查询会员分类
     *
     * @param listQueryVo
     * @return
     */
    @Override
    public AjaxResult selectChunkPage(ListQueryVo listQueryVo) {
        // TODO 由于前端页面有查询，并且请求参数都封装在该类中，因此会分类进行数据查询和返回
        System.out.println("进入查询用户分页数据mapper");
        //从参数中查找查询条件
       QueryWrapper<ChatUsers> wrapper = new QueryWrapper<>();
        //1.仅仅分页查询  默认id升序
        //2.用户邮箱分页查询
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getUserEmail()), "user_email", listQueryVo.getUserEmail());
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getSort()) && !listQueryVo.getSort().contains("id"), "user_status", listQueryVo.getSort());
        //3.创还能日期分类查询
        List<Date> createTime = listQueryVo.getCreateTime();
        if(CollUtil.isNotEmpty(createTime)){
            Date startTime = createTime.get(0);
            Date endTime = createTime.get(1);
            wrapper.ge("create_time",startTime);
            wrapper.le("create_time",endTime);
        }
        //4.用户等级分类查询
        if (listQueryVo.getImportance() != null){
            wrapper.eq("user_status", 2 < listQueryVo.getImportance() ? AllStatus.NORMALUSER.getType() : AllStatus.VIP.getType());
        }
        //5.id升序降序分
        if (StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("+id")) {
            wrapper.orderByAsc("id");
        } else {
            wrapper.orderByDesc("id");
        }
        Page<ChatUsers> page = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
        Page<ChatUsers> pages = chatUsersMapper.selectPage(page, wrapper);
        List<ChatUsers> chatUsersList = pages.getRecords();
        List<AdminUserReqVo> adminUserReqVos = new ArrayDeque<>();
        for (ChatUsers chatUsers:chatUsersList) {
            AdminUserReqVo adminUserReqVo = new AdminUserReqVo();
            System.out.println("chatusers:" + chatUsers.toString());
            if (StrUtil.isNotBlank(chatUsers.getUserStatus()) && chatUsers.getUserStatus().equals(AllStatus.VIP.getType())) {
                adminUserReqVo.setImportance(2);
            } else {
                adminUserReqVo.setImportance(3);
            }
            BeanUtils.copyProperties(chatUsers,adminUserReqVo);
            adminUserReqVos.add(adminUserReqVo);
        }
        return AjaxResult.success("查询成功",adminUserReqVos,pages.getTotal());
    }

    /**
     * 更新用户状态信息
     * @param tempVo
     * @return
     */
    @Override
    public int updateChunk(TempVo tempVo) {
        // 根据角色修改权限
        ChatUsers chatUsers = this.findUserByUserName(tempVo.getUserName());
        if(chatUsers == null){
            return 0;
        }
        BeanUtils.copyProperties(tempVo,chatUsers);
        chatUsers.setUpdateTime(new Date());
        if(tempVo.getUserStatus().equals(AllStatus.ISDELETED.getType())) {
            chatUsers.setDeleted(1);
        }else {
            chatUsers.setDeleted(0);
        }
        if(null == tempVo.getExpireTime()) {
            long delayMillis = Duration.ofDays(1).toMillis();
            // long testMillis = Duration.ofSeconds(60).toMillis();
            chatUsers.setExpireTime(new Date(System.currentTimeMillis() + delayMillis));
            rabbitTemplate.convertAndSend("chunk_delayed_exchange","chunk_routing",chatUsers.getId(),message -> {
                message.getMessageProperties().setDelay((int)delayMillis);
                return message;
            });
        }
        return chatUsersMapper.updateById(chatUsers) & chatUsersMapper.updateUserRole(chatUsers.getId(),tempVo.getUserStatus().equals(AllStatus.NORMALUSER.getType()) ? 3 : 2);
    }

    /**
     * 后台管理创建新用户
     * @param tempVo
     * @return
     */
    @Override
    public int createChunk(TempVo tempVo) {
        System.out.println("后台管理开始创建新用户");
        if(this.findByemail(tempVo.getUserEmail()) > 0){
            return 0;
        }
        //创建用户需要分配权限
        ChatUsers chatUsers = new ChatUsers();
        BeanUtils.copyProperties(tempVo,chatUsers);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePassword = bCryptPasswordEncoder.encode(tempVo.getPassword());
        chatUsers.setUserPassword(encodePassword);
        chatUsers.setCreateTime(new Date());
        int i = chatUsersMapper.insert(chatUsers);
        if (i > 0){
            //分配权限
            ChatUsers userByUserName = this.findUserByUserName(chatUsers.getUserName());
            if (tempVo.getUserStatus().equals(AllStatus.NORMALUSER.getType())){
                //普通用户分配权限3
                chatRoleMapper.saveRoleUsers(userByUserName.getId(),3);
            }else if(tempVo.getUserStatus().equals(AllStatus.VIP.getType())){
                chatRoleMapper.saveRoleUsers(userByUserName.getId(),2);
            }
        }
        return i;
    }


    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode("test");
        System.out.println(encode);
    }

}




