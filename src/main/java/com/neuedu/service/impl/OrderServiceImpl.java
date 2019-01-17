package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.*;
import com.neuedu.pojo.*;
import com.neuedu.service.IOrderService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.PropertiesUtils;
import com.neuedu.vo.CartOrderItemVO;
import com.neuedu.vo.OrderItemVO;
import com.neuedu.vo.OrderVO;
import com.neuedu.vo.ShippingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements IOrderService {


    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    ShippingMapper shippingMapper;
    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse creatOrder(Integer userId, Integer shippingId) {

        //step1:参数非空校验
        if (shippingId==null){
            return ServerResponse.creatServerResponseByError("地址参数不能为空");
        }
        //step2:根据userId查询购物车中已选中的商品--》List<Cart>
        List<Cart> cartList = cartMapper.findCartListByUserIdAndCheck(userId);
        //step3:List<Cart>--->List<OrderItem>
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        //step4:创建订单order并将其保存到数据库
          //计算订单的价格
        BigDecimal orderTotalPrice = new BigDecimal("0");
        List<OrderItem> orderItemList =(List<OrderItem>) serverResponse.getData();
        if (orderItemList==null||orderItemList.size()==0){
            return ServerResponse.creatServerResponseByError("购物车为空");
        }

        orderTotalPrice = getOrderPrice(orderItemList);
        Order order = creatOrder(userId, shippingId, orderTotalPrice);
        if (order==null){
            return ServerResponse.creatServerResponseByError("订单创建失败");
        }
        //step5：将List<OrderItem>保存到数据库
        for (OrderItem orderItem:orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
          //批量插入
        orderItemMapper.insertBatch(orderItemList);
        //step6：扣商品的库存
        reduceProductStock(orderItemList);
        //step7：清空已下单的商品
        cleanCart(cartList);
        //step8:返回，OrderVO
        OrderVO orderVO = assembleOrderVO(order, orderItemList, shippingId);


        return ServerResponse.creatServerResponseBySuccess(null,orderVO);
    }



    /**
     * 构建OrderVO
     * @param order
     * @param orderItemList
     * @param shippingId
     * @return
     */
    private OrderVO assembleOrderVO(Order order,List<OrderItem> orderItemList,Integer shippingId){
        OrderVO orderVO = new OrderVO();
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        for (OrderItem orderItem:orderItemList) {
            OrderItemVO orderItemVO = assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping != null){
            orderVO.setShippingId(shippingId);
            ShippingVO shippingVO = assmbleSHippingVO(shipping);
            orderVO.setReceiverName(shipping.getReceiverName());
            orderVO.setShippingVo(shippingVO);
        }
        orderVO.setStatus(order.getStatus());
        //涉及到枚举的遍历
        Const.OrderStatusEnum orderStatusEnum = Const.OrderStatusEnum.codeOf(order.getStatus());
        if (orderStatusEnum != null){
            orderVO.setStatusDesc(orderStatusEnum.getDesc());
        }
        orderVO.setPostage(0);
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        Const.PaymentEnum paymentEnum = Const.PaymentEnum.codeOf(order.getPaymentType());
        if (paymentEnum != null) {
            orderVO.setPaymentTypeDesc(paymentEnum.getDesc());
        }
        orderVO.setOrderNo(order.getOrderNo());

        return orderVO;
    }

    /**
     * 创建shippingVO
     * @param shipping
     * @return
     */
    private ShippingVO assmbleSHippingVO(Shipping shipping){
        ShippingVO shippingVO = new ShippingVO();
        if (shipping!=null){
            shippingVO.setReceiverAddress(shipping.getReceiverAddress());
            shippingVO.setReceiverCity(shipping.getReceiverCity());
            shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVO.setReceiverMobile(shipping.getReceiverMobile());
            shippingVO.setReceiverName(shipping.getReceiverName());
            shippingVO.setReceiverPhone(shipping.getReceiverPhone());
            shippingVO.setReceiverProvince(shipping.getReceiverProvince());
            shippingVO.setReceiverZip(shipping.getReceiverZip());
        }


        return shippingVO;
    }


    /**
     * 构建OrderItemVO
     * @param orderItem
     * @return
     */
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO = new OrderItemVO();
        if (orderItem!=null){

            orderItemVO.setQuantity(orderItem.getQuantity());
            orderItemVO.setCreatTime(DateUtils.dateToStr(orderItem.getCreateTime()));
            orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVO.setOrderNO(orderItem.getOrderNo());
            orderItemVO.setProductId(orderItem.getProductId());
            orderItemVO.setProductImage(orderItem.getProductImage());
            orderItemVO.setProductName(orderItem.getProductName());
            orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        }



        return orderItemVO;
    }


    /**
     * 清空购物车 中已选择的商品
     */
     private void cleanCart(List<Cart> cartList){

         if (cartList!=null && cartList.size()>0){
             cartMapper.batchDelete(cartList);
         }
     }

    /**
     * 扣库存
     * @param orderItemList
     */
    private void reduceProductStock(List<OrderItem> orderItemList){
        if (orderItemList!=null && orderItemList.size()>0){
            for (OrderItem orderItem: orderItemList) {
                Integer productId = orderItem.getProductId();
                Integer quantity = orderItem.getQuantity();
                Product product = productMapper.selectByPrimaryKey(productId);
                product.setStock(product.getStock()-quantity);
                productMapper.updateByPrimaryKey(product);
            }
        }
    }


    /**
     * 计算订单的总价格
     * @param orderItemList
     * @return
     */
    private BigDecimal getOrderPrice(List<OrderItem> orderItemList){

        BigDecimal bigDecimal = new BigDecimal("0");
        for (OrderItem orderItem:orderItemList) {
            BigDecimalUtils.add(bigDecimal.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return bigDecimal;
    }

    /**
     * List<Cart>--->List<OrderItem>
     * @param userId
     * @param cartList
     * @return
     */
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){

        if (cartList==null || cartList.size()==0){
            return ServerResponse.creatServerResponseByError("购物车空");
        }
        List<OrderItem> orderItemList = Lists.newArrayList();
        for (Cart cart:cartList) {

            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product==null){
                return ServerResponse.creatServerResponseByError("id为"+cart.getProductId()+"的商品不存在");
            }
            if (product.getStatus()!= Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
                //商品状态已下架
                return ServerResponse.creatServerResponseByError("id为"+cart.getProductId()+"的商品已下架");
            }
            if (product.getStock()<cart.getQuantity()){
                return ServerResponse.creatServerResponseByError("id为"+ cart+"的商品库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(product.getPrice());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.creatServerResponseBySuccess(null,orderItemList);
    }

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    private Order creatOrder(Integer userId, Integer shippingId, BigDecimal orderTotalPrice){
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setStatus(Const.OrderStatusEnum.ORDER_UN_PAY.getCode());
        //订单金额
        order.setPayment(orderTotalPrice);
        order.setPostage(0);
        order.setPaymentType(Const.PaymentEnum.ONLINE.getCode());

        //保存订单
        int insert = orderMapper.insert(order);
        if (insert>0){
            return order;
        }

        return null;
    }

    /**
     * 生成订单编号
     */
    private Long generateOrderNo(){

        /*时间戳，精确到毫秒+100内的随机数（适用于用户量不多的）*/
        return System.currentTimeMillis()+new Random().nextInt(100);
    }

    /**
     * 取消订单
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        //step1：参数的非空校验
        if (orderNo==null){
            return ServerResponse.creatServerResponseByError("参数不能为空");
        }
        //step2：根据userId和orderNo查询订单
        Order order = orderMapper.findOrderByUserIdAndOrderNo(userId, orderNo);
        if (order==null){
            return ServerResponse.creatServerResponseByError("订单不存在");
        }
        //step3：判断订单状态并且取消（只有未付款的订单才能取消订单）
        if (order.getStatus()!=Const.OrderStatusEnum.ORDER_UN_PAY.getCode()){
            return ServerResponse.creatServerResponseByError("订单不可取消");
        }
        //step4：返回结果
        order.setStatus(Const.OrderStatusEnum.OEDER_CANCELED.getCode());
        int result = orderMapper.updateByPrimaryKey(order);
        if (result>0){
            return ServerResponse.creatServerResponseBySuccess();
        }

        return ServerResponse.creatServerResponseByError("订单取消失败");
    }

    /**
     * 获取购物车的订单明细
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse get_order_cart_product(Integer userId) {
        //step1：查询购物车
        List<Cart> cartList = cartMapper.findCartListByUserIdAndCheck(userId);
        //step2:List<Cart>----->List<OrderItem>
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        //step3:组装VO
        CartOrderItemVO cartOrderItemVO = new CartOrderItemVO();
        cartOrderItemVO.setImageHost(PropertiesUtils.readByKey("inageHost"));
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        if (orderItemList==null || orderItemList.size()==0){
            return ServerResponse.creatServerResponseByError("购物车空");
        }
        for (OrderItem orderItem: orderItemList) {
            orderItemVOList.add(assembleOrderItemVO(orderItem));
        }
        cartOrderItemVO.setOrderItemVOList(orderItemVOList);
        cartOrderItemVO.setTotalPrice(getOrderPrice(orderItemList));
        //step4：返回结果
        return ServerResponse.creatServerResponseBySuccess(null,cartOrderItemVO);
    }

    /**
     * 订单list
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse list(Integer userId,Integer pageNum,Integer pageSize) {
    //如果传userId就是用户登录，只能查userId的订单信息，
        // 不传的话就默认管理员，可以查看所有订单的信息
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = Lists.newArrayList();
        if (userId==null){
            //查询所有
            orderList = orderMapper.selectAll();
        }else{
            //查询当前用户
            orderList=orderMapper.findOrderByUserId(userId);
        }
        if (orderList==null || orderList.size()==0){
            return ServerResponse.creatServerResponseByError("未查询到订到信息");
        }
        List<OrderVO> orderVOList = Lists.newArrayList();
        for (Order order: orderList) {
            List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(order.getOrderNo());
            OrderVO orderVO = assembleOrderVO(order, orderItemList, order.getShippingId());
            orderVOList.add(orderVO);
        }

        PageInfo pageInfo = new PageInfo(orderVOList);
        return ServerResponse.creatServerResponseBySuccess(null,pageInfo);
    }

    /**
     * 订单详情
     *
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse detail(Long orderNo) {
        //step1：参数的非空校验
        if (orderNo==null){
            return ServerResponse.creatServerResponseByError("参数不能为空");
        }
        //step2：根据订单编号查询订单
        Order order = orderMapper.findOrderByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.creatServerResponseByError("订单不存在");
        }
        //step3：获取orderVO
        List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(order.getOrderNo());
        OrderVO orderVO = assembleOrderVO(order, orderItemList, order.getShippingId());
        //step4：返回结果
        return ServerResponse.creatServerResponseBySuccess(null,orderVO);
    }


}
