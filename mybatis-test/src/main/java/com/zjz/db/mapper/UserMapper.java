package com.zjz.db.mapper;

import com.zjz.db.model.User;

import java.util.List;

/**
 * @author zjz
 * @date 2021/11/2 19:55
 */
public interface UserMapper {

    List<User> selectUsers();
}
