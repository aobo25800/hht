package com.zjz.mybatisTest.dao;

import com.zjz.mybatisTest.entity.UserEntity;

import java.util.List;

/**
 * @author zjz
 * @date 2021/9/29 14:26
 */
public interface UserDao {
    public List<UserEntity> selectList();
}
