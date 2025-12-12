package com.example.wikipro.service;

import com.example.wikipro.common.Result;
import com.example.wikipro.entity.WxCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface WxCartService extends IService<WxCart> {

    /**
     * 添加商品到购物车
     *
     * @param userId  用户ID
     * @param goodsId 商品ID
     * @param quantity 数量
     * @return 结果
     */
    Result<?> addToCart(Integer userId, Integer goodsId, Integer quantity);

    /**
     * 更新购物车商品数量
     *
     * @param cartId  购物车ID
     * @param userId  用户ID
     * @param quantity 数量
     * @return 结果
     */
    Result<?> updateQuantity(Integer cartId, Integer userId, Integer quantity);

    /**
     * 从购物车删除商品
     *
     * @param cartId 购物车ID
     * @param userId 用户ID
     * @return 结果
     */
    Result<?> removeFromCart(Integer cartId, Integer userId);

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 结果
     */
    Result<?> clearCart(Integer userId);

    /**
     * 获取用户购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    Result<List<WxCart>> getCartList(Integer userId);
}

