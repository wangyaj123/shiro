package com.wyj.shiropro.service;

import com.wyj.shiropro.model.User;


public interface UserService {

    User findByUsername(String username);
}
