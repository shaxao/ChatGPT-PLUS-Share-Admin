package com.louwei.gptresource.domain;

import lombok.Data;

/**
 * share_token生成参数类
 */
@Data
public class ShareTokenValue {

    /**
     * 生成share_token的请求的url
     */
    private String url;

    /**
     * 过期时间  单位为天  实际为从1970开始的毫秒数  参考一天:86400
     */
    private String expireTime;

    /**
     * 唯一标识符
     */
    private String uniqueName;

    private String accessToken;

    /**
     * 域名限制，填写后share_token只能在该域名使用
     */
    private String siteLimit;

    /**
     * 是否显示会话列表 默认为false
     */
    private String showConversations;

    /**
     * 是否显示用户信息  默认为false
     */
    private String showUserInfo;
}
