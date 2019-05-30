package com.neuedu.service.impl;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import com.neuedu.utils.MD5Utils;
import com.neuedu.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserServiceImpl implements IUserService {

    //把usermapper的实现类的dao接口注入到Userservice的实现类中
    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * 登录接口
     *
     * @param username
     * @param password
     */
    @Override
    public ServerResponse login(String username, String password) {



        //step1:参数的非空校验
        if(username==null||username.equals("")){
            return ServerResponse.creatServerResponseByError("用户名不能为空");
        }
        if (password==null||password.equals("")){
            return ServerResponse.creatServerResponseByError("密码不能为空");
        }
        //step2：检查用户名是否存在
        int i = userInfoMapper.checkUsername(username);
        if (i==0){
            return ServerResponse.creatServerResponseByError("用户名不存在");
        }
        //step3：根据用户名和密码查找用户信息
        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username, MD5Utils.getMD5Code(password));
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("密码错误");
        }
        //step4：返回结果
        userInfo.setPassword("");
        return ServerResponse.creatServerResponseBySuccess(null,userInfo);
    }

    /**
     * 注册接口
     *
     * @param userInfo
     */
    @Override
    public ServerResponse register(UserInfo userInfo) {

        //step1:参数的非空校验
        if(userInfo==null) {
            return ServerResponse.creatServerResponseByError("参数不能为空");
        }

        //step2：判断用户名是否存在
        int i = userInfoMapper.checkUsername(userInfo.getUsername());
        if (i>0){
            return ServerResponse.creatServerResponseByError("用户已存在");
        }
        //step3：检验邮箱
        int i1 = userInfoMapper.checkEmail(userInfo.getEmail());
        if (i1>0){
            return ServerResponse.creatServerResponseByError("邮箱已存在");
        }
        //step4：注册
        userInfo.setRole(Const.RoleEnum.ROLE_CUSTOMER.getCode());
        //使用MD5加密，使保存到数据库中的密码就是密文，不是明文
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        int insert = userInfoMapper.insert(userInfo);
        if (insert>0){
            return ServerResponse.creatServerResponseBySuccess("注册成功");
        }
        //step5：返回结果
        return ServerResponse.creatServerResponseByError("注册失败");
    }

    /**
     * 根据用户名查询密保问题
     *
     * @param username
     */
    @Override
    public ServerResponse forget_get_question(String username) {

        //step1:参数的非空检验
        if (username==null||username.equals("")){

            return ServerResponse.creatServerResponseByError("用户名不能为空");
        }
        //step2：判断username是否存在
        int i = userInfoMapper.checkUsername(username);
        if (i==0){
            //用户名不存在
            return ServerResponse.creatServerResponseByError("用户名不存在，请重新输入");
        }
        //step3：查询密保问题
        String question = userInfoMapper.selectQuestionUsername(username);
        if (question==null || question.equals("")){
            return ServerResponse.creatServerResponseByError("密保问题为空");
        }
        return ServerResponse.creatServerResponseBySuccess(question);
    }

    /**
     * 根据用户名查询到密保问题之后，提交问题答案
     *
     * @param username
     * @param question
     * @param answer
     */
    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {

        //step1：参数校验
        if (username==null||username.equals("")){

            return ServerResponse.creatServerResponseByError("用户名不能为空");
        }
        if (question==null||question.equals("")){

            return ServerResponse.creatServerResponseByError("问题不能为空");
        }
        if (answer==null||answer.equals("")){

            return ServerResponse.creatServerResponseByError("答案不能为空");
        }
        //step2：根据username，question，answer查询
        int result = userInfoMapper.selectByUsernameAndQuestionAndAnswer(username, question, answer);
        if (result==0){
            return ServerResponse.creatServerResponseByError("答案错误");
        }
        //3：服务端生成token保存并将token返回给客户端
        String forgetToken = UUID.randomUUID().toString();
        // guava cache  缓存  把token保存到guava缓存中
        TokenCache.set(username,forgetToken);

        return ServerResponse.creatServerResponseBySuccess(username,forgetToken);
    }

    /**
     * 忘记密码的重置密码
     *
     * @param username
     * @param passwordNew
     * @param forgetToken
     */
    @Override
    public ServerResponse forget_reset_password(String username, String passwordNew, String forgetToken) {

        //step1：参数校验
        if (username==null||username.equals("")){

            return ServerResponse.creatServerResponseByError("用户名不能为空");
        }
        if (passwordNew==null||passwordNew.equals("")){

            return ServerResponse.creatServerResponseByError("新密码不能为空");
        }
        if (forgetToken==null||forgetToken.equals("")){

            return ServerResponse.creatServerResponseByError("token不能为空");
        }
        //step2：token的校验
        String token = TokenCache.get(username);
        if (token==null){
            return ServerResponse.creatServerResponseByError("token已过期");
        }
        if (!token.equals(forgetToken)){
            return ServerResponse.creatServerResponseByError("无效的token");
        }
        //step3：修改密码
        int i = userInfoMapper.updateUserPassword(username, MD5Utils.getMD5Code(passwordNew));
        if (i>0){
            ServerResponse serverResponse = ServerResponse.creatServerResponseBySuccess();
            return serverResponse;
        }
        return ServerResponse.creatServerResponseByError("密码重置失败");
    }

    /**
     * 检验用户名或邮箱是否有效
     *
     * @param str
     * @param type
     */
    @Override
    public ServerResponse check_valid(String str, String type) {
        //step1:参数的非空校验
        if (str==null|| str.equals("")){
            return ServerResponse.creatServerResponseByError("用户名不能为空");
        }
        if (type==null|| type.equals("")){
            return ServerResponse.creatServerResponseByError("校验类型参数不能为空");
        }

        //step2：type：username--》校验用户名 str
        //type：email--》校验邮箱  str
        if (type.equals("username")){
            int i = userInfoMapper.checkUsername(str);
            if (i>0){
                //用户已存在
                return ServerResponse.creatServerResponseByError("用户名已存在");
            }else{
                return ServerResponse.creatServerResponseBySuccess();
            }
        }else if (type.equals("email")){
            int i = userInfoMapper.checkEmail(str);
            if (i>0){
                //邮箱已存在
                return ServerResponse.creatServerResponseByError("邮箱已存在");
            }else{
                return ServerResponse.creatServerResponseBySuccess();
            }
        }else{
            return ServerResponse.creatServerResponseByError("参数类型错误");
        }
        //step3：返回结果
    }

    /**
     * 登录状态重置密码
     *
     * @param passwordOld
     * @param passwordNew
     */
    @Override
    public ServerResponse reset_password(String username,String passwordOld, String passwordNew) {

        //step1:参数的非空检验
        if (passwordOld==null|| passwordOld.equals("")){
            return ServerResponse.creatServerResponseByError("用户名旧密码不能为空");
        }
        if (passwordNew==null || passwordNew.equals("")){
            return ServerResponse.creatServerResponseByError("用户名新密码不能为空");
        }
        //step2：根据username和passwordOld
        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(passwordOld));
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("旧密码错误");
        }
        //step3：修改密码
        userInfo.setPassword(MD5Utils.getMD5Code(passwordNew));
        int i = userInfoMapper.updateByPrimaryKey(userInfo);
        if (i>0){
            return ServerResponse.creatServerResponseBySuccess();
        }
        return ServerResponse.creatServerResponseByError("密码修改失败");
    }

    /**
     * 登录状态下更新个人信息
     *
     * @param user
     */
    @Override
    public ServerResponse update_infomation(UserInfo user) {

        //1:参数校验
        if (user==null){
            return ServerResponse.creatServerResponseByError("参数不能为空");
        }
        //2：更新用户信息
        int i = userInfoMapper.updateUserBySelectActive(user);
        if (i>0){
            return ServerResponse.creatServerResponseBySuccess();
        }
        return ServerResponse.creatServerResponseByError("更新个人信息失败");
    }

    /**
     * 根据userid查询用户信息
     *
     * @param userId
     */
    @Override
    public UserInfo findUserinfoByUserId(Integer userId) {

        return userInfoMapper.selectByPrimaryKey(userId);

    }

    /**
     * 根据token查询用户信息
     * @param token
     */
    @Override
    public UserInfo getUserInfoByToken(String token) {
//直接在程序中添加出模块功能之外的程序，违反了java中单一执行原则，一个类只能有一种职责，只负责跟该模块相关的业务逻辑的处理
//        System.out.println("====开始执行====");


        return userInfoMapper.getUserInfoByToken(token);
    }


}
