package com.louwei.gptresource.service;

import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.domain.ChatProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import com.louwei.gptresource.vo.ChatProductDetailReqVo;
import com.louwei.gptresource.vo.ChatProductReqVo;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.TempVo;

import java.util.List;

/**
* @author Administrator
* @description 针对表【chat_product(商品详情表)】的数据库操作Service
* @createDate 2023-12-28 15:12:48
*/
public interface ChatProductService extends IService<ChatProduct> {

    List<ChatProductReqVo> findAll();

    ChatProductDetailReqVo findDetail(Integer proId);

    ChatProductDetailReqVo findDetailById(Integer productId, Integer quantity);

    AjaxResult selectProductPage(ListQueryVo listQueryVo);

    int updateProductPage(TempVo tempVo);

    int createProductPage(TempVo tempVo);
}
