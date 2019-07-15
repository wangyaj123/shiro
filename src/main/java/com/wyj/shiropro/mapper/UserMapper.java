package com.wyj.shiropro.mapper;

import com.wyj.shiropro.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);
}