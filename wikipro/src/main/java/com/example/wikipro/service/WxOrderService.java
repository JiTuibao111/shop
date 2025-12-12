package com.example.wikipro.service;

import com.example.wikipro.common.Result;
import com.example.wikipro.entity.WxOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 订单服务接口
 */
public interface WxOrderService extends IService<WxOrder> {

    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param cartIds 购物车ID列表（从购物车创建订单）
     * @param receiverName 收货人姓名
     * @param receiverPhone 收货人电话
     * @param receiverAddress 收货地址
     * @param remark 备注
     * @return 结果
     */
    Result<WxOrder> createOrder(Integer userId, List<Integer> cartIds, 
                                String receiverName, String receiverPhone, 
                                String receiverAddress, String remark);

    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    Result<List<WxOrder>> getOrderList(Integer userId, Integer status);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 订单详情
     */
    Result<WxOrder> getOrderDetail(Integer orderId, Integer userId);

    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param status 新状态
     * @return 结果
     */
    Result<?> updateOrderStatus(Integer orderId, Integer userId, Integer status);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 结果
     */
    Result<?> cancelOrder(Integer orderId, Integer userId);
}

