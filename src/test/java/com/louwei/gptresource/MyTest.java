package com.louwei.gptresource;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.louwei.gptresource.common.PagePath;
import com.louwei.gptresource.domain.*;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.enums.OrderStatus;
import com.louwei.gptresource.mapper.*;
import com.louwei.gptresource.pojo.MailMessages;
import com.louwei.gptresource.service.ChatPaymentService;
import com.louwei.gptresource.service.ChatShareTokenService;
import com.louwei.gptresource.service.ChatUsersService;
import com.louwei.gptresource.service.impl.ChatPaymentServiceImpl;
import com.louwei.gptresource.utils.VerifyCodeUtils;
import com.louwei.gptresource.vo.ListQueryVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SpringBootTest
public class MyTest {
    @Autowired
    private ChatPaymentService chatPaymentService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ChatShareTokenService chatShareTokenService;

    @Test
    public void apiPay() {
        Map<String, String> params = new TreeMap<>();
        params.put("pid", "1019");
        params.put("type", "wechat");
        params.put("out_trade_no", "1715563425370");
        params.put("name", "0天测试商品");
        params.put("money", "0.1");
        params.put("notify_url", "http://prod-cn.your-api-server.com/pay/order/notify");
        params.put("device", "pc");
        params.put("clientip", "0:0:0:0:0:0:0:1");
        String signature = chatPaymentService.generateSignature(params, "2U52jUh7131r0qz712zxJ5j59b27B52u");
        System.out.println(signature);
    }

    @Test
    public void addRedisAcc() {
//        redisTemplate.opsForValue().set("accessToken:userCount", String.valueOf(0));
//        try {
//            redisTemplate.opsForValue().increment("accessToken:userCount");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(redisTemplate.opsForValue().get("accessToken:userCount"));
//        String tokenUse = chatShareTokenService.findTokenUse();
//        System.out.println(tokenUse);
    }
//    @Autowired
//    private MailMessages mailMessages;
//    @Autowired
//    private ChatUsersMapper chatUsersMapper;
//    @Autowired
//    private ChatUsersService chatUsersService;
//    @Autowired
//    private ChatPermissionMapper chatPermissionMapper;
//    @Autowired
//    private ChatOrdersMapper chatOrdersMapper;
//    @Autowired
//    private ChatProductDetailImgMapper chatProductDetailImgMapper;
//    @Autowired
//    private ChatUrlMapper chatUrlMapper;
//    @Autowired
//    private ChatProductMapper chatProductMapper;
//    @Autowired
//    private FastFileStorageClient fastFileStorageClient;
//
//    @Test
//    public void gptTest(){
//        String code = VerifyCodeUtils.createCode();
//        mailMessages.createMessages(code,"3803217870@qq.com");
//        mailMessages.send();
//    }
//
//    @Test
//    public void urlTest(){
//        List<ChatUrl> chatUrls = chatUrlMapper.selectList(new QueryWrapper<ChatUrl>().isNotNull("id"));
//        for (ChatUrl chatUrl:chatUrls){
//            System.out.println("id:" + chatUrl.getId()+"，路径："+ chatUrl.getRoutePath());
//        }
//        System.out.println(chatUrls.get(3).getRoutePath());
//    }

//    @Test
//    public void dbTest(){
//        ChatUsers chatUsers = new ChatUsers();
//        chatUsers.setUserPhone("15330745815");
//        int insert = chatUsersMapper.insert(chatUsers);
//        System.out.println(insert);
//    }

//    @Test
//    void resTest(){
//        ChatUsers chatUsers = chatUsersService.findByEmail("3803217870@qq.com");
//        //ChatUsers chatUsers1 = new ChatUsers();
//        //chatUsers.setUserPhone("15330745815");
//        chatUsers.setDeleted(0);
//        boolean b = chatUsersService.updateById(chatUsers);
//
//    }

//    @Test
//    void passTest(){
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encodePassword = passwordEncoder.encode("Lw2220413");
//        System.out.println(encodePassword);
//        System.out.println(passwordEncoder.matches("Lw2220413",encodePassword));
//    }

//    @Test
//    void perTest(){
//        List<ChatPermission> muhuo = chatPermissionMapper.findPermissionByUsername("mkhjnjn");
//        muhuo.forEach(System.out::println);
//    }

//    @Test
//    void roleUserTest(){
////        boolean b = chatRoleMapper.saveRoleUsers(15, 3);
////        System.out.println(b);
//        String orderNo = "1704424772334";
//        QueryWrapper<ChatOrders> chatOrdersQueryWrapper = new QueryWrapper<>();
//        chatOrdersQueryWrapper.eq("order_no",orderNo);
//        ChatOrders chatOrders = chatOrdersMapper.selectOne(chatOrdersQueryWrapper);
//        System.out.println(chatOrders.toString());
//        System.out.println("/index.html".equals(PagePath.INDEX.getPath()));//False
//    }

//    @Test
//    void cancelOrderTest(){
//        String orderNo = "1704424772334";
//        QueryWrapper<ChatOrders> wrapper = new QueryWrapper<>();
//        wrapper.eq("order_no",orderNo);
//        ChatOrders chatOrders = chatOrdersMapper.selectOne(wrapper);
//        System.out.println("chatOrders:" + chatOrders.toString());
//        chatOrders.setOrderStatus(OrderStatus.CANCEL.getType());
//        Date now  = new Date();
//        chatOrders.setExpireTime(now);
//        int update = chatOrdersMapper.updateById(chatOrders);
//        System.out.println("取消成功:" + update);
//        System.out.println("订单不存在" + chatOrders.toString());
//    }

//    @Test
//    void proImgs(){
//        List<ChatProductDetailImg> productDetailImgs = chatProductDetailImgMapper.selectList(new QueryWrapper<ChatProductDetailImg>().eq("product_id", 1));
//        for (ChatProductDetailImg c:productDetailImgs) {
//            System.out.println(c.getImageUrl());
//        }
//    }

//    @Test
//    void adminUserTest(){
//        QueryWrapper<ChatUsers> wrapper = new QueryWrapper<>();
//        ListQueryVo listQueryVo = new ListQueryVo();
//        listQueryVo.setLimit(1);
//        listQueryVo.setPage(20);
//        listQueryVo.setUserEmail("3803217870@qq.com");
//        listQueryVo.setImportance(3);
//        //1.仅仅分页查询  默认id升序
//        //2.用户邮箱分页查询
//        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getUserEmail()), "user_email", listQueryVo.getUserEmail());
//        //3.创还能日期分类查询
//        wrapper.eq(Boolean.parseBoolean(String.valueOf(listQueryVo.getCreateTime() !=  null)),"create_time",listQueryVo.getCreateTime());
//        //4.用户等级分类查询
//        if (listQueryVo.getImportance() != null){
//            wrapper.eq("user_status", 2 < listQueryVo.getImportance() ? "普通用户" : "会员");
//        }
//        //5.id升序降序分
//        if (StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("+id")) {
//            wrapper.orderByAsc("id");
//        } else if (StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("-id")){
//            wrapper.orderByDesc("id");
//        }
//        Page<ChatUsers> page = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
//        Page<ChatUsers> pages = chatUsersMapper.selectPage(page, wrapper);
//        List<ChatUsers> records = pages.getRecords();
//        records.forEach(System.out::println);
//        long total = pages.getTotal();
//        System.out.println("total:" + total);
//    }

//    @Test
//    void ordrTest(){
////        ChatOrders chatOrders = chatOrdersMapper.selectById("1704618527941");
////        System.out.println(chatOrders.toString());
////        Integer userIdByEmail = chatUsersMapper.findUserIdByEmail("3803217870@qq.com");
////        System.out.println(userIdByEmail);
////        Integer proIdByT = chatProductMapper.findProIdByT("1天测试商品");
////        System.out.println("商品ID：" + proIdByT);
//    }

//    @Test
//    void productTest(){
//        ChatProduct chatProduct = chatProductMapper.selectById(6);
//        if(chatProduct != null) {
//            System.out.println("商品已存在");;
//        }
//        System.out.println("商品不存在");
//        chatProduct.setStock(2);
//        System.out.println(chatProduct.getStock());
//    }

//    @Test
//    void fdfsTest(){
//        File file = new File("C:\\Users\\Administrator\\Pictures\\Camera Roll\\儿童手绘2.png");
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            StorePath png = fastFileStorageClient.uploadFile(fileInputStream, file.length(), "png", null);
//            //全路径:group1/M00/00/00/L2N-bWWf9qWAL-4cACKD3BtFS08462.png
//            System.out.println("全路径:" + png.getFullPath());
//            //卷名:group1,文件名:M00/00/00/L2N-bWWf9qWAL-4cACKD3BtFS08462.png
//            System.out.println("卷名:" + png.getGroup() + ",文件名:" + png.getPath());
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    @Test
//    void fdfDow(){
//        int id = 15;
//        // 获取该用户已支付的过期时间最晚的订单信息
//        ChatOrders chatOrders = chatOrdersMapper.selectOne(new QueryWrapper<ChatOrders>()
//                .eq("user_id", id)
//                .eq("order_status",OrderStatus.SUCCESS.getType())
//                .orderByDesc("expire_time"));
//        Date now = new Date();
//        if(now.compareTo(chatOrders.getExpireTime()) > 0){
//            // 说明该用户已经过期
//            chatOrders.setOrderStatus(OrderStatus.EXPIRE.getType());
//            chatOrdersMapper.updateById(chatOrders);
//            // 用户权限降级
//            chatUsersMapper.updateUserStatus(id);
//            chatUsersMapper.updateUserRole(id);
//            System.out.println(AllStatus.NORMALUSER.getType());
//        }else {
//            System.out.println(AllStatus.VIP.getType());
//        }
//    }

//    @Test
//    void findUsers(){
//        ChatUsers chatUsers = chatUsersMapper.selectOne(new QueryWrapper<ChatUsers>().eq("user_name", "admin"));
//        if (chatUsers == null) {
//            System.out.println("未查询到用户");
//        } else {
//            System.out.println("用户信息："+chatUsers.toString());
//        }
//    }

//    @Test
//    void fdfsDel(){
//        fastFileStorageClient.deleteFile("group1/M00/00/00/L2N-bWWf9qWAL-4cACKD3BtFS08462.png");
//    }
}
