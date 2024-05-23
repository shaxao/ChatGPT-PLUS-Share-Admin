package com.louwei.gptresource.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.common.PagePath;
import com.louwei.gptresource.domain.*;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.mapper.ChatCodeMapper;
import com.louwei.gptresource.mapper.ChatProductDetailImgMapper;
import com.louwei.gptresource.mapper.ChatUrlMapper;
import com.louwei.gptresource.mapper.ChatUsersMapper;
import com.louwei.gptresource.service.*;
import com.louwei.gptresource.service.impl.ChatOrdersServiceImpl;
import com.louwei.gptresource.utils.RequestUtils;
import com.louwei.gptresource.utils.UserDetailsNow;
import com.louwei.gptresource.vo.ChatOrderReqVo;
import com.louwei.gptresource.vo.ChatPaymentReqVo;
import com.louwei.gptresource.vo.ChatProductDetailReqVo;
import com.louwei.gptresource.vo.ChatProductReqVo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
public class PageController {
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private RequestUtils requestUtils;
    @Autowired
    private ChatProductService chatProductService;
    @Autowired
    private ChatOrdersService chatOrdersService;
    @Autowired
    private ChatCodeMapper chatCodeMapper;
    @Autowired
    private ChatProductDetailImgMapper chatProductDetailImgMapper;
    @Autowired
    private ChatUrlMapper chatUrlMapper;
    @Autowired
    private ChatUsersService chatUsersService;
    @Autowired
    private ChatShareTokenService chatShareTokenService;
    @Autowired
    private ChatPaymentService chatPaymentService;

    @RequestMapping("/{page}")
    public String getPage(@PathVariable String page, Model model, RedirectAttributes redirectAttributes){
        System.out.println("接收到的请求页面：" + page);
        if(page.equals(PagePath.INDEX.getPath())){
            System.out.println("处理导航页面");
//            // 1.获取会话对象
//            SecurityContext context = SecurityContextHolder.getContext();
//            // 2.获取认证对象
//            Authentication authentication = context.getAuthentication();
//            // 3.获取登录用户信息
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String username = userDetails.getUsername();
            String username = UserDetailsNow.getUsername();
            System.out.println("获取导航登录用户信息：" + username);
            //chatCodeMapper.

            List<ChatUrl> chatUrls = chatUrlMapper.selectList(new QueryWrapper<ChatUrl>().isNotNull("id"));
            model.addAttribute("vipuse",chatUrls.get(0).getRoutePath());
            model.addAttribute("tokenLogin",chatUrls.get(1).getRoutePath());
            model.addAttribute("qshop",chatUrls.get(2).getRoutePath());
            model.addAttribute("freechat1",chatUrls.get(3).getRoutePath());
            model.addAttribute("freechat2",chatUrls.get(4).getRoutePath());
            model.addAttribute("freechat3",chatUrls.get(5).getRoutePath());
            model.addAttribute("freechat4",chatUrls.get(6).getRoutePath());
            //获取登录菜单路径或者转发链接
            ChatUsers userByUserName = chatUsersService.findUserByUserName(username);
            String userStatus = userByUserName.getUserStatus();
            //如果是会员，那么进行一遍检查，确认未过期，如果过期，那么权限降级，否则不变
            if(userByUserName.equals(AllStatus.VIP.getType())){
                userStatus = chatOrdersService.checkUserStatus(userByUserName.getId());
            }
            if(userStatus.equals(AllStatus.ADMIN.getType())){
                userStatus = AllStatus.VIP.getType();
            }
            model.addAttribute("userStatus", userStatus);
            model.addAttribute("username",username);
//            List<ChatProductReqVo> productReqVos = chatProductService.findAll();
//            model.addAttribute("products",productReqVos);
        }else if(page.equals(PagePath.PRODUCT.getPath())) {
            System.out.println("处理商品页面");
            String username = UserDetailsNow.getUsername();
            model.addAttribute("username", username);
            List<ChatProductReqVo> productReqVos = chatProductService.findAll();
            model.addAttribute("products", productReqVos);
        }else if (page.equals(PagePath.PANDORA.getPath())){
            System.out.println("处理pandora鉴权");
            String url = chatShareTokenService.getPanroraUrl();
            if(url == null) {
                url = "error.html";

                redirectAttributes.addFlashAttribute("errorMessage", "当前无可用账号，请稍后再试！");
            }

            log.info("redirect for url:{}", url);
            return "redirect:" + url;
        }
        return page;
    }

    @GetMapping("/error.html")
    public String handleError(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
                session.removeAttribute("errorMessage"); // Clean up session attribute
            }
        }
        return "error";
    }

    @GetMapping("/returnUrl.html")
    public String requestUrl(Model model, HttpServletRequest request) {
        log.info("request:{}",request.getHeader("trade_status"));
        return "/returnUrl.html";
    }




    /**
     * 商品详情页面
     * @param proId
     * @param model
     * @return
     */
    @GetMapping("/product-detail.html")
    public String getProduct(@RequestParam("prodId") Integer proId, Model model){
        System.out.println("正在处理商品详情页面:" + proId);
        String username = UserDetailsNow.getUsername();
        model.addAttribute("username", username);
        ChatProductDetailReqVo chatProductDetailReqVos = chatProductService.findDetail(proId);
        List<ChatProductDetailImg> productDetailImgs = chatProductDetailImgMapper.selectList(new QueryWrapper<ChatProductDetailImg>().eq("product_id", proId));
        List<ChatPaymentReqVo> chatPayments = chatPaymentService.findEnablePay();
        model.addAttribute("product",chatProductDetailReqVos);
        model.addAttribute("productDetailImgs",productDetailImgs);
        model.addAttribute("chatPayments",chatPayments);
        return "product-detail.html";
    }

    @GetMapping("/addWechat.html")
    public String toWechat(@RequestParam("payUrl") String payUrl,@RequestParam("orderNo") String orderNo, Model model){
//        ChatCode chatCode = chatCodeMapper.selectById(1);
//        model.addAttribute("orderNo",orderNo);
        model.addAttribute("payUrl",payUrl);
        model.addAttribute("orderNo",orderNo);
        return "addWechat.html";
    }

    @GetMapping("/orderQuery")
    public String getProduct(@RequestParam("username") @NonNull String username, Model model){
        System.out.println("正在处理订单详情页面:" + username);
        if (username == null){
            username = UserDetailsNow.getUsername();
        }
        Page<ChatOrders> chatOrdersPage = chatOrdersService.findOrderByUsername(username);
        List<ChatOrderReqVo> chatOrderReqVos = chatOrdersService.chatOrderToReq(chatOrdersPage.getRecords());
        model.addAttribute("orders",chatOrderReqVos);
        model.addAttribute("page", 1);
        model.addAttribute("totalPages", ((chatOrdersPage.getTotal() / 10) + (chatOrdersPage.getTotal() % 10 > 0 ? 1 : 0)));
        System.out.println("处理订单详情成功，开始跳转:" + ((chatOrdersPage.getTotal() / 10) + (chatOrdersPage.getTotal() % 10 > 0 ? 1 : 0)));
        return "order-query.html";
    }

    @GetMapping("/pandora/share")
    public ResponseEntity<String> redirectToExternalUrl() {
       return ResponseEntity.ok("authenticated");
    }

}
