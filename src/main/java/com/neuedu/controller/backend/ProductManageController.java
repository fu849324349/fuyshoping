package com.neuedu.controller.backend;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/product")
public class ProductManageController {

    @Autowired
    IProductService productService;

    /**
     * 新增或是更新商品
     */
    @RequestMapping(value = "/save.do")
    public ServerResponse saveOrUpdate(HttpSession session, Product product){

        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        //判断用户是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NEET_LONIG.getCode(),Const.ReponseCodeEnm.NEET_LONIG.getDesc());
        }
        //判断用户是否有管理员权限
        if (userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NO_PRIVILEGE.getCode(),Const.ReponseCodeEnm.NO_PRIVILEGE.getDesc());
        }
        return productService.saveOrUpdate(product);
    }

    /**
     * 产品的上下架
     */
    @RequestMapping(value = "/set_sale_status.do")
    public ServerResponse set_sale_status(HttpSession session, Integer productId,Integer status){

        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        //判断用户是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NEET_LONIG.getCode(),Const.ReponseCodeEnm.NEET_LONIG.getDesc());
        }
        //判断用户是否有管理员权限
        if (userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NO_PRIVILEGE.getCode(),Const.ReponseCodeEnm.NO_PRIVILEGE.getDesc());
        }
        return productService.set_sale_status(productId,status);
    }

    /**
     * 查看商品详情
     */
    @RequestMapping(value = "/detail.do")
    public ServerResponse detail(HttpSession session, Integer productId){

        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        //判断用户是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NEET_LONIG.getCode(),Const.ReponseCodeEnm.NEET_LONIG.getDesc());
        }
        //判断用户是否有管理员权限
        if (userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NO_PRIVILEGE.getCode(),Const.ReponseCodeEnm.NO_PRIVILEGE.getDesc());
        }
        return productService.detail(productId);
    }
    /**
     *查看商品列表
     */
    @RequestMapping(value = "/list.do/id")
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize, @PathVariable Integer id){

        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        //判断用户是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NEET_LONIG.getCode(),Const.ReponseCodeEnm.NEET_LONIG.getDesc());
        }
        //判断用户是否有管理员权限
        if (userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NO_PRIVILEGE.getCode(),Const.ReponseCodeEnm.NO_PRIVILEGE.getDesc());
        }
        return productService.list(pageNum,pageSize);
    }

    /**
     * 商品搜索
     */
    @RequestMapping(value = "/search.do")
    public ServerResponse search(HttpSession session,
                                 @RequestParam(value = "productId",required = false)Integer productId,
                                 @RequestParam(value = "productName",required = false)String productName,
                               @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize){

        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        //判断用户是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NEET_LONIG.getCode(),Const.ReponseCodeEnm.NEET_LONIG.getDesc());
        }
        //判断用户是否有管理员权限
        if (userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.creatServerResponseByError(Const.ReponseCodeEnm.NO_PRIVILEGE.getCode(),Const.ReponseCodeEnm.NO_PRIVILEGE.getDesc());
        }
        return productService.search(productId,productName,pageNum,pageSize);
    }


}
