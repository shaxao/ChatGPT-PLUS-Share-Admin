package com.louwei.gptresource.mapper;

import com.louwei.gptresource.domain.ChatUsers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author Administrator
* @description 针对表【chat_users(用户表)】的数据库操作Mapper
* @createDate 2023-12-25 16:01:13
* @Entity com.louwei.gptresource.domain.ChatUsers
*/
@Mapper
public interface ChatUsersMapper extends BaseMapper<ChatUsers> {

    Integer updateUserStatus(@Param("userId") Integer userId,@Param("userStatus") String userStatus);

    Integer updateUserRole(@Param("userId") Integer userId,@Param("rid") Integer rid);

    Integer findUserIdByEmail(String userEmail);

}




