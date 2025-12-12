package com.example.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wikipro.common.Result;
import com.example.wikipro.common.ResultStatus;
import com.example.wikipro.entity.WxCart;
import com.example.wikipro.entity.WxGoods;
import com.example.wikipro.mapper.WxCartMapper;
import com.example.wikipro.mapper.WxGoodsMapper;
import com.example.wikipro.service.WxCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Slf4j
@Service
public class WxCartServiceImpl extends ServiceImpl<WxCartMapper, WxCart> implements WxCartService {

    @Autowired
    private WxCartMapper cartMapper;

    @Autowired
    private WxGoodsMapper goodsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addToCart(Integer userId, Integer goodsId, Integer quantity) {
        // 参数校验
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效");
        }
        if (goodsId == null || goodsId <= 0) {
            return Result.failure("商品ID无效");
        }
        if (quantity == null || quantity <= 0) {
            return Result.failure("商品数量必须大于0");
        }

        // 检查商品是否存在
        WxGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.failure("商品不存在");
        }

        // 检查库存
        if (goods.getStock() < quantity) {
            return Result.failure("商品库存不足");
        }

        // 检查购物车中是否已存在该商品
        LambdaQueryWrapper<WxCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxCart::getUserId, userId)
               .eq(WxCart::getGoodsId, goodsId);
        WxCart existingCart = cartMapper.selectOne(wrapper);

        Date now = new Date();
        if (existingCart != null) {
            // 如果已存在，更新数量
            int newQuantity = existingCart.getQuantity() + quantity;
            // 再次检查库存
            if (goods.getStock() < newQuantity) {
                return Result.failure("商品库存不足，当前库存：" + goods.getStock());
            }
            existingCart.setQuantity(newQuantity);
            existingCart.setUpdateTime(now);
            cartMapper.updateById(existingCart);
            log.info("更新购物车商品数量 - userId: {}, goodsId: {}, quantity: {}", userId, goodsId, newQuantity);
        } else {
            // 如果不存在，创建新记录
            WxCart cart = new WxCart();
            cart.setUserId(userId);
            cart.setGoodsId(goodsId);
            cart.setQuantity(quantity);
            cart.setCreateTime(now);
            cart.setUpdateTime(now);
            cartMapper.insert(cart);
            log.info("添加商品到购物车 - userId: {}, goodsId: {}, quantity: {}", userId, goodsId, quantity);
        }

        return Result.success("添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateQuantity(Integer cartId, Integer userId, Integer quantity) {
        // 参数校验
        if (cartId == null || cartId <= 0) {
            return Result.failure("购物车ID无效");
        }
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效");
        }
        if (quantity == null || quantity <= 0) {
            return Result.failure("商品数量必须大于0");
        }

        // 查询购物车记录
        WxCart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            return Result.failure("购物车记录不存在");
        }

        // 验证用户权限
        if (!cart.getUserId().equals(userId)) {
            return Result.failure("无权操作此购物车记录");
        }

        // 检查商品库存
        WxGoods goods = goodsMapper.selectById(cart.getGoodsId());
        if (goods == null) {
            return Result.failure("商品不存在");
        }
        if (goods.getStock() < quantity) {
            return Result.failure("商品库存不足，当前库存：" + goods.getStock());
        }

        // 更新数量
        cart.setQuantity(quantity);
        cart.setUpdateTime(new Date());
        cartMapper.updateById(cart);

        log.info("更新购物车商品数量 - cartId: {}, quantity: {}", cartId, quantity);
        return Result.success("更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> removeFromCart(Integer cartId, Integer userId) {
        // 参数校验
        if (cartId == null || cartId <= 0) {
            return Result.failure("购物车ID无效");
        }
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效");
        }

        // 查询购物车记录
        WxCart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            return Result.failure("购物车记录不存在");
        }

        // 验证用户权限
        if (!cart.getUserId().equals(userId)) {
            return Result.failure("无权操作此购物车记录");
        }

        // 删除记录
        cartMapper.deleteById(cartId);
        log.info("从购物车删除商品 - cartId: {}, userId: {}", cartId, userId);
        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> clearCart(Integer userId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效");
        }

        // 删除用户的所有购物车记录
        LambdaQueryWrapper<WxCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxCart::getUserId, userId);
        cartMapper.delete(wrapper);

        log.info("清空购物车 - userId: {}", userId);
        return Result.success("清空成功");
    }

    @Override
    public Result<List<WxCart>> getCartList(Integer userId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效", null);
        }

        // 查询用户购物车列表
        LambdaQueryWrapper<WxCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxCart::getUserId, userId)
               .orderByDesc(WxCart::getCreateTime);
        List<WxCart> cartList = cartMapper.selectList(wrapper);

        // 关联查询商品信息
        for (WxCart cart : cartList) {
            WxGoods goods = goodsMapper.selectById(cart.getGoodsId());
            cart.setGoods(goods);
        }

        log.debug("查询购物车列表 - userId: {}, 数量: {}", userId, cartList.size());
        return Result.success(ResultStatus.SUCCESS, cartList);
    }
}

