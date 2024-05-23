package com.louwei.gptresource.config;

import com.alibaba.fastjson.JSON;
import com.louwei.gptresource.filter.JsonUsernamePasswordAuthenticationFilter;
import com.louwei.gptresource.hanler.*;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import com.louwei.gptresource.service.CustomOAuth2UserService;
import com.louwei.gptresource.service.impl.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityConfig{

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ChatUsersMapper chatUsersMapper;
    @Autowired
    private PersistentTokenRepository repository;
    @Value("${spring.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.oauth2.client.registration.github.client-secret}")
    private String clientSecret;
    @Value("${spring.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.oauth2.client.registration.google.client-secret}")
    private String googleSecret;
    @Value("${spring.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;
    @Value("${spring.oauth2.client.provider.github.authorization-uri}")
    private String authorizationUri;
    @Value("${spring.oauth2.client.provider.github.token-uri}")
    private String tokenUri;
    @Value("${spring.oauth2.client.provider.github.user-info-uri}")
    private String userInfoUri;
    private CustomOAuth2UserService customOAuth2UserService;

    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }

    private static final String[] PERMITS = new String[]{"/login","/login.html", "/logout", "/register.html", "/resetPassword.html","/resetPassword","/register","/email/code","/phone","/email","/fail.html","/noPermission.html",};
    public static final List<String> PERMITS_LIST;

//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return new AuthenticationManager() {
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                return null;
//            }
//        };
//        // 返回构建的 AuthenticationManager
//    }


//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
//    }


    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> formLogin
                        .loginPage("/login.html")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginProcessingUrl("/login")
                        //登录成功处理器
                        .successHandler(new LoginSuccessHandle(this.chatUsersMapper))
                        .failureHandler(new LoginFailHandle()));
        http.logout(logout -> logout.clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessHandler(new LogOutSuccessHandle()));
                //配置csrf
        // http.csrf().disable();
        http.csrf(csrf -> csrf
                .ignoringAntMatchers("/vue-element-admin/**")); // 对自定义登录端点禁用CSRF保护
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));  // 根据需要创建会话
         http.addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);
         http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));   //测试暂时关闭
        http.authorizeRequests(auth -> auth
                        .antMatchers("/pandora/share").hasAnyAuthority("/pandora/share").antMatchers("/pandora/login").hasAnyAuthority("/pandora/login").antMatchers("/shared.html").hasAnyAuthority("/shared.html").antMatchers("/pandora/no").hasAnyAuthority("/pandora/no")
                        .anyRequest().permitAll())
                .oauth2Login(oauth -> oauth.
                        userInfoEndpoint().userService(this.customOAuth2UserService)
                        .and().successHandler(this.customOAuth2SuccessHandler));
//                        .anyRequest().access("@myAuthorizationService.hasPermission(#httpServletRequest,#authentication)"))
                //配置记住我
//                .rememberMe(remenber -> remenber.tokenRepository(repository)
//                        .tokenValiditySeconds(3600)
//                        .userDetailsService(userDetailsService))
                http.addFilterBefore(new JsonUsernamePasswordAuthenticationFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class);

        http.rememberMe()
                .userDetailsService(userDetailsService)
                .tokenRepository(repository)
                .tokenValiditySeconds(60 * 60 * 24 * 7);
        http.exceptionHandling().accessDeniedHandler(new MyAccessDeniedHandler());
        http.cors().configurationSource(corsConfigurationSource());
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    final class CsrfCookieFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            log.info("csrfToken:" + JSON.toJSONString(csrfToken));
            // 如果CsrfToken存在且响应中没有CSRF token的Cookie，就添加Cookie
            if (csrfToken != null && !hasCsrfCookie(request)) {
                log.info("添加CSRF token的Cookie" + csrfToken.getToken());
                Cookie cookie = new Cookie("X-CSRF-TOKEN", csrfToken.getToken());
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            filterChain.doFilter(request, response);
        }

        // 检查响应中是否包含CSRF token的Cookie
        private boolean hasCsrfCookie(HttpServletRequest request) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("X-CSRF-TOKEN".equals(cookie.getName())) {
                        log.info("_csrf:" + cookie.toString() + ",value:" + cookie.getValue());
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleRegistration = ClientRegistration.withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email", "address", "phone")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .build();
        ClientRegistration githubRegistration = ClientRegistration.withRegistrationId("github")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .scope("read:user")
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .userInfoUri(userInfoUri)
                .redirectUri(redirectUri)
                .userNameAttributeName("login")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
        return new InMemoryClientRegistrationRepository(githubRegistration, googleRegistration);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://47.99.126.109","https://system.qipusong.site","http://localhost:8080","http://localhost:9528"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.addAllowedHeader("*"); // 允许所有请求头
        configuration.setAllowCredentials(true); // 允许发送凭证信息（如cookies）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // source.registerCorsConfiguration("/pay/order/createOrder", configuration); // 指定允许跨域的路径
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    static {
        PERMITS_LIST = Arrays.asList(PERMITS);
    }

}
