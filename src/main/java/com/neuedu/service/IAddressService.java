package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;

public interface IAddressService {

    /**
     * 添加收货地址
     */
    ServerResponse add(Integer userId, Shipping shipping);

    /**
     * 删除地址
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse del(Integer userId,Integer shippingId);

    /**
     * 登录状态下更新地址
     * @param shipping
     * @return
     */
    ServerResponse update(Shipping shipping);

    /**
     * 查看地址
     * @param shippingId
     * @return
     */
    ServerResponse select(Integer shippingId);

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse list(Integer pageNum,Integer pageSize);

}
