package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.pojo.Shipping;
import com.neuedu.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    ShippingMapper shippingMapper;

    /**
     * 添加收货地址
     * @param userId
     * @param shipping
     */
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {

        //1:参数的非空校验
        if (shipping==null){
            return ServerResponse.creatServerResponseByError("参数错误");
        }
        //2：添加
        shipping.setUserId(userId);
        shippingMapper.insert(shipping);
        //3：返回结果
        Map<String,Integer> map= Maps.newHashMap();
        map.put("shipping",shipping.getId());
        return ServerResponse.creatServerResponseBySuccess(null,map);
    }

    /**
     * 删除地址
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {

        //1：参数的非空校验
        if (shippingId==null){
            return ServerResponse.creatServerResponseByError("参数错误");
        }
        //2：删除
        int result = shippingMapper.deleteByUserIdAndShipping(userId, shippingId);
        //3：返回结果
        if (result>0){
            return ServerResponse.creatServerResponseBySuccess();
        }
        return ServerResponse.creatServerResponseByError("删除失败");
    }

    /**
     * 登录状态下更新地址
     *
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse update(Shipping shipping) {

        //1：参数的校验
        if (shipping==null){
            return ServerResponse.creatServerResponseByError("参数错误");
        }
        //2：更新
        int result = shippingMapper.updateBySelectiveKey(shipping);
        //3：返回结果
        if (result>0){
            return ServerResponse.creatServerResponseBySuccess();
        }
        return ServerResponse.creatServerResponseByError("更新失败");
    }

    /**
     * 查看地址
     *
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse select(Integer shippingId) {
        //1:参数非空校验
        if (shippingId==null){
            return ServerResponse.creatServerResponseByError("参数错误");
        }
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        return ServerResponse.creatServerResponseBySuccess(null,shipping);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectAll();
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.creatServerResponseBySuccess(null,pageInfo);
    }
}
