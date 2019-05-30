package com.neuedu.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@Data
public class RedisProperties {

    //最大连接数
    @Value("${redis.max.total}")
    private int maxTotal;
    //最大空闲数redis.ip
    @Value("${redis.max.idle}")
    private int maxIdle;
    //最小空闲数
    @Value("${redis.min.idle}")
    private int minIdle;
    //ip
    @Value("${redis.ip}")
    private String redisip;
    //port
    @Value("${redis.port}")
    private int port;
    //在获取实例时，校验实例是否有效
    @Value("${redis.test.borrow}")
    private boolean testBorrow;
    //在把jedis实例放回连接池时，检验实例是否有效
    @Value("${redis.test.return}")
   private boolean testReturn;
   //连接redis的密码   配置文件中设置的密码
   @Value("${redis.password}")
    private String redisPassword;


}
