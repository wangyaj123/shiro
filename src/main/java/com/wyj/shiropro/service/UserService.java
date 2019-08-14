package com.wyj.shiropro.service;

import com.wyj.shiropro.model.User;


public interface UserService {

    User findByUsername(String username);
    User login(String username,String password);
    User getUserById(int id);
}
