package com.wyj.shiropro.mapper;

import com.wyj.shiropro.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    /**
     * 登录密码验证
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);
    User getUserById(int id);
}