package com.example.ziwanaicodebackend.mapper;

import com.mybatisflex.core.BaseMapper;
import com.example.ziwanaicodebackend.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 映射层。
 *
 * @author <a href="https://github.com/ziwanqing">紫菀</a>
 */
public interface UserMapper extends BaseMapper<User> {

    User selectByAccount(@Param("userAccount") String userAccount);


    @Update("UPDATE user SET isDelete = 0, userPassword = #{password}, " +
            "updateTime = CURRENT_TIMESTAMP WHERE id = #{id}")
    int restoreUser(@Param("id") Long id, @Param("password") String password);



}
