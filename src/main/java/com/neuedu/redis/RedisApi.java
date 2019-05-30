package com.neuedu.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Component
public class RedisApi {

    @Autowired
    private JedisPool jedisPool;

    /**
     * set(key,value)给数据库中设置key value
     * @param key
     * @param value
     * @return
     */
    public String set(String key,String value){

        String result = null;
        Jedis jedis = null;
        try{
            jedis =jedisPool.getResource();
            result = jedis.set(key, value);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
        }finally {
            if (jedis != null){
                jedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    /**
     * 设置过期时间   setex(String key,int second,String value)
     * @param key
     * @param second
     * @param value
     * @return
     */
    public String setex(String key,int second,String value){

        String result = null;
        Jedis jedis = null;
        try{
            jedis =jedisPool.getResource();
            result = jedis.setex(key,second,value);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
        }finally {
            if (jedis != null){
                jedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    /**
     * 根据key获取value   get（String key）
     * @param key
     * @return
     */
    public String get(String key){

        String result = null;
        Jedis jedis = null;
        try{
            jedis =jedisPool.getResource();
            result = jedis.get(key);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
        }finally {
            if (jedis != null){
                jedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    /**
     * 根据key删除value    del（String key）
     * @param key
     * @return
     */
    public Long del(String key){

        Long result = null;
        Jedis jedis = null;
        try{
            jedis =jedisPool.getResource();
            result = jedis.del(key);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
        }finally {
            if (jedis != null){
                jedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    /**
     * 设置过期时间   expire（String key，int second（毫秒数））
     * @param key
     * @param second
     * @return
     */
    public Long set(String key,int second){

        Long result = null;
        Jedis jedis = null;
        try{
            jedis =jedisPool.getResource();
            result = jedis.expire(key,second);
        }catch (Exception e){
            jedisPool.returnBrokenResource(jedis);
        }finally {
            if (jedis != null){
                jedisPool.returnResource(jedis);
            }
        }
        return result;
    }

}
