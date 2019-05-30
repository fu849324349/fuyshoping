package com.neuedu.controller;

import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.json.ObjectMapperApi;
import com.neuedu.pojo.UserInfo;
import com.neuedu.redis.RedisApi;
import com.neuedu.redis.RedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpSession;

@RestController
public class TestController {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    RedisProperties redisProperties;



    @RequestMapping(value = "/user/{userid}")
    public ServerResponse<UserInfo> findUser(@PathVariable Integer userid, HttpSession session){

        System.out.println(redisProperties.getMaxIdle());

        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userid);

        if (userInfo != null){
            return ServerResponse.creatServerResponseBySuccess(null,userInfo);
        }else {
            return ServerResponse.creatServerResponseByError("fail");
        }
    }

    @RequestMapping(value = "/config")
    public String testRedisConfig(){
        return redisProperties.getMaxIdle() + "";
    }

    @Autowired
    private JedisPool jedisPool;
    //容器中没有JedisPool的bean
    @RequestMapping(value = "/redis")
    public String getJedis(){
        Jedis jedis = jedisPool.getResource();
        String value = jedis.set("root1", "root2");
        //把连接放回连接池
        jedisPool.returnResource(jedis);

        return value;
    }

    @Autowired
    private RedisApi redisApi;
    @RequestMapping(value = "/key/{key}")
    public String getkey(@PathVariable("key") String key){

        String value = redisApi.get(key);
        return value;
    }
    @Autowired
    ObjectMapperApi objectMapperApi;
    @RequestMapping(value = "/json")
    public ServerResponse<UserInfo> findUserByJson(@PathVariable Integer userid, HttpSession session){

        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userid);

        String json = objectMapperApi.object2(userInfo);
        System.out.println("u==========="+json);
        return ServerResponse.creatServerResponseBySuccess(null,userInfo);
    }


}
