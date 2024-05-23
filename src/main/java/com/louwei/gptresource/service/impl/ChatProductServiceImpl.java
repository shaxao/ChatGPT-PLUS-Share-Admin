package com.louwei.gptresource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatProduct;
import com.louwei.gptresource.domain.ChatProductDetailImg;
import com.louwei.gptresource.enums.AllStatus;
import com.louwei.gptresource.mapper.ChatProductDetailImgMapper;
import com.louwei.gptresource.service.ChatProductService;
import com.louwei.gptresource.mapper.ChatProductMapper;
import com.louwei.gptresource.vo.ChatProductDetailReqVo;
import com.louwei.gptresource.vo.ChatProductReqVo;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.TempVo;
import com.louwei.gptresource.vo.admin.user.AdminProductReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @author Administrator
* @description 针对表【chat_product(商品详情表)】的数据库操作Service实现
* @createDate 2023-12-28 15:12:48
*/
@Service
@Slf4j
public class ChatProductServiceImpl extends ServiceImpl<ChatProductMapper, ChatProduct>
    implements ChatProductService{
    @Autowired
    private ChatProductMapper chatProductMapper;
    @Autowired
    private ChatProductDetailImgMapper chatProductDetailImgMapper;

    @Override
    public List<ChatProductReqVo> findAll() {
        List<ChatProduct> products = chatProductMapper.selectList(new QueryWrapper<ChatProduct>().eq("pro_status",AllStatus.READY.getType()));
        List<ChatProductReqVo> productReqVos = new ArrayList<>();
        for (ChatProduct product:products) {
            ChatProductReqVo chatProductReqVo = new ChatProductReqVo();
            BeanUtil.copyProperties(product,chatProductReqVo);
            productReqVos.add(chatProductReqVo);
        }
        return productReqVos;
    }

    /**
     * 根据ID查询商品详情
     * @param proId
     * @return
     */
    @Override
    public ChatProductDetailReqVo findDetail(Integer proId) {
        ChatProduct product = chatProductMapper.selectById(proId);
        if (product != null){
            ChatProductDetailReqVo chatProductDetailReqVo = new ChatProductDetailReqVo();
            BeanUtil.copyProperties(product,chatProductDetailReqVo);
            return chatProductDetailReqVo;
        }
        return null;
    }

    @Override
    @Transactional
    public ChatProductDetailReqVo findDetailById(Integer productId, Integer quantity) {
        ChatProduct product = chatProductMapper.selectById(productId);
        if (product != null){
            //修改库存
            if (product.getStock() <= 0 || !product.getProStatus().equals(AllStatus.READY.getType())){
                return null;
            }
            Integer stock = product.getStock();
            if (stock - quantity == 0){
                product.setProStatus(AllStatus.NOTREADY.getType());
            }
            product.setStock(stock - quantity);
            chatProductMapper.updateById(product);
            System.out.println("扣减库存成功");
            ChatProductDetailReqVo chatProductDetailReqVo = new ChatProductDetailReqVo();
            BeanUtil.copyProperties(product,chatProductDetailReqVo);
            return chatProductDetailReqVo;
        }
        return null;
    }

    /**
     * 后台商品管理分页以及多条件分页查询
     * @param listQueryVo
     * @return
     */
    @Override
    public AjaxResult selectProductPage(ListQueryVo listQueryVo) {
        log.info("开始执行后台管理商品分页以及多条件查询:" + listQueryVo.getTitle());
        //商品管理的查询主要排序较多  有价格升序降序 库存升序降序  ID升序降序  默认ID升序   创建日期条件 商品标题条件
        QueryWrapper<ChatProduct> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(listQueryVo.getTitle()),"title",listQueryVo.getTitle());
        List<Date> createTime = listQueryVo.getCreateTime();
        if(CollUtil.isNotEmpty(createTime)){
            Date startTime = createTime.get(0);
            Date endTime = createTime.get(1);
            wrapper.ge("create_time",startTime);
            wrapper.le("create_time",endTime);
        }
        if(StrUtil.isNotBlank(listQueryVo.getPriceSort())){
            if(listQueryVo.getPriceSort().equals("+price")){
                wrapper.orderByDesc("price");
            }else {
                wrapper.orderByAsc("price");
            }
        }else if(StrUtil.isNotBlank(listQueryVo.getStockSort())){
            if(listQueryVo.getStockSort().equals("+stock")){
                wrapper.orderByDesc("stock");
            }else {
                wrapper.orderByAsc("stock");
            }
        }else {
            if(StrUtil.isNotBlank(listQueryVo.getSort()) && listQueryVo.getSort().equals("+id")){
                wrapper.orderByDesc("id");
            }else {
                wrapper.orderByAsc("id");
            }
        }
        Page<ChatProduct> page = new Page<>(listQueryVo.getPage(),listQueryVo.getLimit());
        Page<ChatProduct> pages= chatProductMapper.selectPage(page, wrapper);
        List<ChatProduct> chatProducts = pages.getRecords();
        List<AdminProductReqVo> adminProductReqVos = new ArrayList<>();
        for(ChatProduct chatProduct:chatProducts){
            AdminProductReqVo adminProductReqVo = new AdminProductReqVo();
            BigDecimal pric = chatProduct.getPrice();
            int price = pric.intValue();
            adminProductReqVo.setPrice(price);
            BeanUtils.copyProperties(chatProduct,adminProductReqVo);
            adminProductReqVos.add(adminProductReqVo);
        }
        return AjaxResult.success("查询商品成功",adminProductReqVos,pages.getTotal());
    }

    /**
     * 商品数据以及状态局部更新
     * @param tempVo
     * @return
     */
    @Override
    public int updateProductPage(TempVo tempVo) {
        log.info("开始处理后台管理商品数据更新");
        ChatProduct chatProduct = chatProductMapper.selectOne(new QueryWrapper<ChatProduct>().eq("id",tempVo.getId()));
        if(chatProduct == null){
            return 0;
        }
        //如果除了id和状态都为空，那说明只修改状态，直接处理返回，不浪费时间
        if(tempVo.getStock() == null || tempVo.getTitle() == null){
            chatProduct.setProStatus(tempVo.getProStatus());
            int i = chatProductMapper.updateById(chatProduct);
            return i;
        }
        //否则，则是大修改,先更改商品的主图
        BeanUtils.copyProperties(tempVo,chatProduct);
        chatProduct.setPrice(BigDecimal.valueOf(tempVo.getPrice()));
        chatProduct.setImageUrl(tempVo.getImageLink());
        int i = chatProductMapper.updateById(chatProduct);
        //更新商品详情图
        List<String> desImageLink = tempVo.getDesImageLink();
        List<ChatProductDetailImg> detailImgs = chatProductDetailImgMapper.selectList(new QueryWrapper<ChatProductDetailImg>().eq("product_id", chatProduct.getId()));
        //遍历该商品的商品详情图片集合，然后挨个替换，如果数据库数量大于更改的，那么删除多余的
        for (int j = 0; j < detailImgs.size(); j++) {
            ChatProductDetailImg chatProductDetailImg = detailImgs.get(j);
            if(desImageLink.size() < j){
                //删除多余的商品详情图片
                i  = chatProductDetailImgMapper.deleteById(chatProductDetailImg.getId());
            }
            chatProductDetailImg.setImageUrl(desImageLink.get(j));
            chatProductDetailImg.setUpdateTime(new Date());
            chatProductDetailImg.setCreator(tempVo.getCreator());
            //更新商品详情图片
            i = chatProductDetailImgMapper.updateById(chatProductDetailImg);
        }
        return i;
    }

    /**
     * 创建后台管理商品
     * @param tempVo
     * @return
     */
    @Override
    public int createProductPage(TempVo tempVo) {
        log.info("开始处理后台管理商品添加程序");
        ChatProduct chatProduct = chatProductMapper.selectOne(new QueryWrapper<ChatProduct>().eq("title",tempVo.getTitle()));
        if(chatProduct != null) {
            return 0;
        }
        ChatProduct chatProducts = new ChatProduct();
        BeanUtil.copyProperties(tempVo,chatProducts);
        chatProducts.setImageUrl(tempVo.getImageLink());
        chatProducts.setPrice(BigDecimal.valueOf(tempVo.getPrice()));
        AtomicInteger i = new AtomicInteger(chatProductMapper.insert(chatProducts));
        //添加商品详情图集合
        List<String> desImageLinks = tempVo.getDesImageLink();
        desImageLinks.forEach(desImageLink -> {
            ChatProductDetailImg chatProductDetailImg = new ChatProductDetailImg();
            chatProductDetailImg.setImageUrl(desImageLink);
            chatProductDetailImg.setProductId(chatProducts.getId());
            chatProductDetailImg.setCreator(tempVo.getCreator());
            chatProductDetailImg.setCreateTime(new Date());
            i.set(chatProductDetailImgMapper.insert(chatProductDetailImg));
        });
        return i.get();
    }
}




