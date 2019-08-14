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

    @Override
    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    @Override
    public User getUserById(int id) {
        return userMapper.getUserById(id);
    }
}
