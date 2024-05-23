package com.louwei.gptresource.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.louwei.gptresource.domain.ChatPermission;
import com.louwei.gptresource.domain.ChatUsers;
import com.louwei.gptresource.domain.CustomOAuth2User;
import com.louwei.gptresource.domain.CustomUserDetails;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.mapper.ChatPermissionMapper;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private ChatUsersMapper chatUsersMapper;
    @Autowired
    private ChatPermissionMapper chatPermissionMapper;
    @Autowired
    private ChatRoleService chatRoleService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 获取用户信息
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // [login, id, node_id, avatar_url, gravatar_id, url, html_url, followers_url, following_url, gists_url, starred_url, subscriptions_url, organizations_url, repos_url, events_url, received_events_url, type, site_admin, name, company, blog, location, email, hireable, bio, twitter_username, public_repos, public_gists, followers, following, created_at, updated_at, private_gists, total_private_repos, owned_private_repos, disk_usage, collaborators, two_factor_authentication, plan]
        log.info("用户信息集合:{}", attributes.keySet());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String username;

        if ("github".equals(registrationId)) {
            username = (String) attributes.get("login");
        } else if ("google".equals(registrationId)) {
            username = (String) attributes.get("name");
        } else {
            username = oAuth2User.getName();
        }
        log.info("oauth获取用户名:{}", username);
        QueryWrapper<ChatUsers> wrapper = new QueryWrapper<ChatUsers>().eq("user_name",username);
        ChatUsers chatUsers = chatUsersMapper.selectOne(wrapper);
        if(chatUsers == null){
            // 不存在，则注册
            chatUsers = new ChatUsers();
            chatUsers.setUserName(username);
            chatUsers.setUserStatus(AllStatus.NORMALUSER.getType());
            chatUsers.setCreateTime(new Date());
            chatUsers.setDeleted(0);
            chatUsersMapper.insert(chatUsers);
            chatRoleService.createRole(chatUsers.getId());
        }
        List<ChatPermission> permissionByUsername = chatPermissionMapper.findPermissionByUsername(username);
        //将自定义权限集合转为spring security的权限类型集合
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (ChatPermission chatPermission:permissionByUsername) {
            grantedAuthorities.add(new SimpleGrantedAuthority(chatPermission.getUrl()));
        }
        // 创建UserDetails对象
        UserDetails userDetails = new CustomUserDetails(username, grantedAuthorities, attributes);

        return new CustomOAuth2User(userDetails, attributes, grantedAuthorities);
    }
}
