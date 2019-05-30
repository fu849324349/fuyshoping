package com.neuedu.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectMapperApi {

    @Autowired
    ObjectMapper objectMapper;

    /**
     * java对象转字符串
     */
    public <T> String object2(T t){
        if(t==null){
            return null;
        }
        try {
            return t instanceof String ? (String)t : objectMapper.writeValueAsString(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> String object2Pretty(T t){
        if(t==null){
            return null;
        }
        try {
            return t instanceof String ? (String)t : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





}
