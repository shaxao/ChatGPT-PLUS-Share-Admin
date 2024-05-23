package com.louwei.gptresource.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class JsonUsernamePasswordAuthenticationFilter extends OncePerRequestFilter {
    private UserDetailsService userDetailsService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public JsonUsernamePasswordAuthenticationFilter(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    private void responseError(HttpServletRequest request, HttpServletResponse response, Integer code, String message) {
        try {
            // 存储错误信息到会话中
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", message);

            // 重定向到错误页面
            response.sendRedirect("/error.html");

        } catch (IOException e) {
            log.error("Failed to redirect to error page", e);
        }
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        log.info("请求URI：{}", requestURI);

        try {
            if (requestURI.equals("/vue-element-admin/user/login") && request.getMethod().equals("POST")) {
                // 登录请求的处理
                handleLogin(request, response);
                return;
            } else if (requestURI.startsWith("/vue-element-admin")) {
                // 对于其他需要验证的请求
                if (!handleTokenValidation(request, response)) {
                    // 如果token验证失败，handleTokenValidation 方法已设置响应并返回false
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SecurityContextHolder.clearContext();
            responseError(request, response,50012,"后台管理请求处理错误");
        }
        log.info("通过验证，进入下一个过滤链");
        // 继续过滤链处理（对于登录和有效token的请求）
        filterChain.doFilter(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
        // 从 request 输入流中读取数据并转换为 Java 对象
        UserLogin userLogin = mapper.readValue(request.getInputStream(), UserLogin.class);

        log.info("Username from JSON: {}", userLogin.getUsername());
        String username = userLogin.getUsername();
        String password = userLogin.getPassword();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        log.info("后台管理系统 userDetails={},password={}", userDetails.getUsername(), userDetails.getPassword());

        if (userDetails != null && passwordEncoder.matches(password,userDetails.getPassword())) {
            String token = JWTUtil.token(username);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 200);
            responseData.put("data", Map.of(
                    "status", "admin",
                    "token", token
            ));

            log.info("生成token={}", token);
            response.setHeader("Authorization", "Bearer " + token);
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(mapper.writeValueAsString(responseData));
            out.flush();
            out.close();
        } else {
            responseError(request, response,60204,"账号或者密码错误");
        }
    }

    private boolean handleTokenValidation(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        log.info("请求中token={}", token);
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);

            try {
                if (JWTUtil.verify(jwtToken)) {
                    return true;
                }
            } catch (Exception e) {
                log.error("Token验证失败", e);
            }
        }
        responseError(request, response,50008,"token验证错误");
        return false;
    }

}
class UserLogin {
    private String username;
    private String password;

    // getter 和 setter 方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
