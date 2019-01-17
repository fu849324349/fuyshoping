package com.neuedu.controller.portal;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    ICartService cartService;

    /**
     * 购物车中添加商品
     */
    @RequestMapping(value = "/add.do")
    public ServerResponse add(HttpSession session,Integer productId, Integer count){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.add(userInfo.getId(),productId,count);
    }

    /**
     * 购物车List
     * @param session
     * @return
     */
    @RequestMapping(value = "/list.do")
    public ServerResponse list(HttpSession session){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.list(userInfo.getId());
    }

    /**
     * 更新购物车某个商品的数量
     * @param session
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping(value = "/update.do")
    public ServerResponse update(HttpSession session,Integer productId, Integer count){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.update(userInfo.getId(),productId,count);
    }

    /**
     * 移除购物车中某个或某些产品
     * @param session
     * @param productIds
     * @return
     */
    @RequestMapping(value = "/delete_product.do")
    public ServerResponse delete_product(HttpSession session,String productIds){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.delete_product(userInfo.getId(),productIds);
    }

    /**
     * 购物车选中某个商品
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "/select.do")
    public ServerResponse select(HttpSession session,Integer productId){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.select(userInfo.getId(),productId,Const.CartCheckedEnum.PRODUCT_CHECKED.getCode());
    }

    /**
     * 取消选择
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "/un_select.do")
    public ServerResponse un_select(HttpSession session,Integer productId){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.select(userInfo.getId(),productId,Const.CartCheckedEnum.PRODUCT_UNCHECKED.getCode());
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping(value = "/select_all.do")
    public ServerResponse select_all(HttpSession session){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.select(userInfo.getId(),null,Const.CartCheckedEnum.PRODUCT_CHECKED.getCode());
    }

    /**
     * 取消全选
     * @param session
     * @return
     */
    @RequestMapping(value = "/un_select_all.do")
    public ServerResponse un_select_all(HttpSession session){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.select(userInfo.getId(),null,Const.CartCheckedEnum.PRODUCT_UNCHECKED.getCode());
    }

    @RequestMapping(value = "/get_cart_product_count.do")
    public ServerResponse get_cart_product_count(HttpSession session){

        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        //判断是否登录
        if (userInfo==null){
            return ServerResponse.creatServerResponseByError("需要登录");
        }
        return cartService.get_cart_product_count(userInfo.getId());
    }

}
