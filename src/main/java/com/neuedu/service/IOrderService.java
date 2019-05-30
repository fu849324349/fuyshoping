package com.neuedu.service;

import com.neuedu.common.ServerResponse;

import java.util.Map;

public interface IOrderService {

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse creatOrder(Integer userId,Integer shippingId);

    /**
     * 取消订单
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse cancel(Integer userId,Long orderNo);

    /**
     * 获取购物车的订单明细
     * @param userId
     * @return
     */
    ServerResponse get_order_cart_product(Integer userId);

    /**
     * 订单list
     * @param userId
     * @return
     */
    ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);

    /**
     * 订单详情
     * @param orderNo
     * @return
     */
    ServerResponse detail(Long orderNo);

    /**
     * 支付接口
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse pay(Integer userId,Long orderNo);

    /**
     * 支付宝回调接口
     */
    ServerResponse alipay_callback(Map<String,String> map);

    /**
     * 查看订单的支付状态
     * @param orderNo
     * @return
     */
    ServerResponse query_order_pay_status(Long orderNo);

    /**
     * 根据时间查询订单
     * @param time
     */
    void closeOrder(String time);

}
