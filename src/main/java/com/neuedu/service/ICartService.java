package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface ICartService {

    /**
     * 购物车中添加商品
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse add(Integer userId,Integer productId,Integer count);

    /**
     * 购物车List
     * @param userId
     * @return
     */
    ServerResponse list(Integer userId);

    /**
     *更新购物车某个商品的数量
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    ServerResponse update(Integer userId,Integer productId, Integer count);

    /**
     * 移除购物车中某些/某个产品
     * @param userId
     * @param productIds
     * @return
     */
    ServerResponse delete_product(Integer userId,String productIds);

    /**
     * 购物车选中某个商品
     * @param userId
     * @param productId
     * @return
     */
    ServerResponse select(Integer userId,Integer productId,Integer check);

    /**
     * 购物车中产品的数量
     * @param userId
     * @return
     */
    ServerResponse get_cart_product_count(Integer userId);

}
