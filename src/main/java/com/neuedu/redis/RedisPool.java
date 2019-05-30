package com.neuedu.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
@Configuration
public class RedisPool {   //xml  <bean>

    @Autowired
    RedisProperties redisProperties;

    //JedisPool就是java程序连接redis的一个连接池
    // 有了连接池就能获取连接了,就是springIoc的第三种实现方式
    @Bean   //加了这个springIOC容器就会自动实例化一个JedisPool
    public JedisPool jedisPool(){

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxTotal(redisProperties.getMaxTotal());
        jedisPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getMinIdle());
        //获取到连接时检测是否有效
        jedisPoolConfig.setTestOnBorrow(redisProperties.isTestBorrow());
        //返回时检测连接有效性
        jedisPoolConfig.setTestOnReturn(redisProperties.isTestReturn());
        //当连接池中的连接被消耗完毕，值为true：需要等待连接  false：会抛出异常
        //等待时间是2秒，如果有连接就连接，没有就直接返回，相当于是阻塞
        jedisPoolConfig.setBlockWhenExhausted(true);


        return new JedisPool(jedisPoolConfig, redisProperties.getRedisip(),redisProperties.getPort(),
        2000, redisProperties.getRedisPassword(),0);
    }

}
