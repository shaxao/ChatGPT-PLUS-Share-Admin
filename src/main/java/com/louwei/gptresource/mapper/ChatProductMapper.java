package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【chat_product(商品详情表)】的数据库操作Mapper
* @createDate 2023-12-28 15:12:48
* @Entity com.louwei.gptresource.domain.ChatProduct
*/
@Mapper
public interface ChatProductMapper extends BaseMapper<ChatProduct> {

    Integer findProIdByT(String title);
}




