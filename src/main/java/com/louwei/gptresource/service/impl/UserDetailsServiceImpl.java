package com.louwei.gptresource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.louwei.gptresource.domain.ChatPermission;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.mapper.ChatPermissionMapper;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户认证
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private ChatUsersMapper chatUsersMapper;
    @Autowired
    private ChatPermissionMapper chatPermissionMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("处理登录:" + username);
        QueryWrapper<ChatUsers> wrapper = new QueryWrapper<ChatUsers>().eq("user_name",username);
        ChatUsers chatUsers = chatUsersMapper.selectOne(wrapper);
        if(chatUsers == null || chatUsers.getDeleted() == 1){
            return null;
        }
        System.out.println("查询数据库用户:" +chatUsers.getUserEmail());
        List<ChatPermission> permissionByUsername = chatPermissionMapper.findPermissionByUsername(username);
        //将自定义权限集合转为spring security的权限类型集合
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (ChatPermission chatPermission:permissionByUsername) {
            grantedAuthorities.add(new SimpleGrantedAuthority(chatPermission.getUrl()));
        }
        //封装为UserDetails对象
        UserDetails userDetails = User.withUsername(username)
                .password(chatUsers.getUserPassword())
                .authorities(grantedAuthorities)
                .build();

        return userDetails;
    }
}
