package com.neuedu.interceptor;

import com.google.gson.Gson;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AutoLoginInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;



    //在请求调用Hand/controller之前，调用这个方法
    //返回true代表可以通过拦截器，到达controller，返回false代表拦截了请求，不会被controller处理了
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        System.out.println("====preHandle====");

        //获取cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie: cookies) {
                String name = cookie.getName();
                if (name.equals("token")){
                    //拿到要写到客户端的token
                    String value = cookie.getValue();
                    //根据token查询用户信息
                    UserInfo userInfo = userService.getUserInfoByToken(value);
                    if (userInfo != null){
                        request.getSession().setAttribute("currentuser",userInfo);
                        return true;
                    }
                }
            }
        }
        //重置响应
        response.reset();
        response.setContentType("text/json;charaet=utf-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ServerResponse serverResponse = ServerResponse.creatServerResponseByError(100, "需要登录");
        Gson gson = new Gson();
        String json = gson.toJson(serverResponse);
        printWriter.write(json);
        printWriter.flush();
        printWriter.close();
        return false;
    }

    //只有preHandle返回true的时候，响应的时候，通过拦截器 就会调这个方法
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        System.out.println("=======postHandle=======");
    }

    //整个请求 结束会调用这个方法
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("======afterCompletion========");
    }
}
