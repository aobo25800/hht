package com.zjz.db;

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

    private static final SqlSession session;

    static {
        // 根据 mybatis-config.xml 配置的信息得到 sqlSessionFactory
        String resource = "./mybatis-config/MybatisConfig.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 然后根据 sqlSessionFactory 得到 session
        session = sqlSessionFactory.openSession();
    }

    public static void main(String[] args) throws IOException {
        List<Long> list = new ArrayList<>();
        list.add(131L);

    }


}
