package com.wyj.shiropro.service;

import com.wyj.shiropro.mapper.UserMapper;
import com.wyj.shiropro.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: WangYajing
 * @Description:
 * @Modified By:
 */
@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
