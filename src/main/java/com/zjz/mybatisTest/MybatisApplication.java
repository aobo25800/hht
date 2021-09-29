package com.zjz.mybatisTest;

import com.zjz.mybatisTest.dao.UserDao;
import com.zjz.mybatisTest.entity.UserEntity;
import com.zjz.mybatisTest.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @author zjz
 * @date 2021/9/27 10:10
 */
public class MybatisApplication {
    public static void main(String[] args) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();

        UserDao mapper = sqlSession.getMapper(UserDao.class);

        List<UserEntity> userEntities = mapper.selectList();

        for (UserEntity user : userEntities) {
            System.out.println(user.getId() + "\t" + user.getName() + "\t" + user.getAge());
        }
    }
}
