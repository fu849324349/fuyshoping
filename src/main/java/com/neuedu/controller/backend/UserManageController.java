package com.neuedu.controller.backend;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 后台用户控制器类
 */
@RestController
@RequestMapping(value = "/manage/user")
public class UserManageController {

    @Autowired
    IUserService iUserService;
    /**
     * 管理员登录
     */
    @RequestMapping(value = "/login.do")
    public ServerResponse login(HttpSession session, String username, String password){

        ServerResponse serverResponse = iUserService.login(username, password);
        //登录成功，并把用户信息放到session中
        if (serverResponse.isSuccess()){
            UserInfo userInfo = (UserInfo)serverResponse.getData();
            if (userInfo.getRole()==Const.RoleEnum.ROLE_CUSTOMER.getCode()){
                return ServerResponse.creatServerResponseByError("无权限登录");
            }
            session.setAttribute(Const.CURRENTUSER,userInfo);
        }
        return serverResponse;
    }



}
