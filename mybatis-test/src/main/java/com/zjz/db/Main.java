package com.zjz.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.zjz.db.model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author zjz
 * @date 2021/11/2 19:34
 */
public class Main {
    public static void main(String[] args) throws IOException {
        // 根据 mybatis-config.xml 配置的信息得到 sqlSessionFactory
        String resource = "./mybatis-config/MybatisConfig.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 然后根据 sqlSessionFactory 得到 session
        SqlSession session = sqlSessionFactory.openSession();
        // 最后通过 session 的 selectList() 方法调用 sql 语句 listStudent
        List<User> userList = session.selectList("selectUsers");
        System.out.println(userList);
        new DruidDataSource()
    }


}
