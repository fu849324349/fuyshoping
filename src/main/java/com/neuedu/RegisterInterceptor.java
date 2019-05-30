package com.neuedu;


import com.neuedu.interceptor.AutoLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@SpringBootConfiguration
public class RegisterInterceptor implements WebMvcConfigurer {

    @Autowired
    AutoLoginInterceptor autoLoginInterceptor;


    //通过registry这个类注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //           /* --->  /a  /b
        //           /**   代表portal后边所有的子目录
        //自动登录   /user/get_information
        //           /cart/add.do
        //           /order/creat.do

        List<String> excludeList = new ArrayList<>();
        // 用户模块中不需要拦截的
        excludeList.add("/user/login.do");
        excludeList.add("/user/register.do");
        excludeList.add("/user/forget_get_question.do");
        excludeList.add("/user/forget_check_answer.do");
        excludeList.add("/user/forget_reset_password.do");
        excludeList.add("/user/check_valid.do");
        excludeList.add("/user/logout.do");
        //前台用户商品搜索商品不用拦截的
        excludeList.add("/product/*");
        //后台管理员登录不用拦截
        excludeList.add("/manage/user/login.do");
        //测试不用拦截
     //   excludeList.add("/user/{userid}");
        //把不拦截的放到集合中，先把说有方法都拦截再调用不拦截的
//        registry.addInterceptor(autoLoginInterceptor).addPathPatterns("/**").excludePathPatterns(excludeList);



    }
}
