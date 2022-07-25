package com.zjz.db;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;


/**
 * @author zjz
 * @date 2021/12/7 13:33
 */
public class RedisApplication {
    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://@192.168.0.195:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        syncCommands.set("key", "Hello, Redis!");
        String zjz = syncCommands.get("zjz");
        System.out.println(zjz);
//
//        syncCommands.expire("zjz", 30L);

//        Long lpush = syncCommands.lpush("zjzlist", "zjz1", "zjz2", "zjz3");
//        System.out.println(lpush);
//        List<String> zjzlist = syncCommands.lrange("zjzlist", 0, 6);
//        System.out.println(zjzlist);
//
//        connection.close();
//        redisClient.shutdown();
    }
}
