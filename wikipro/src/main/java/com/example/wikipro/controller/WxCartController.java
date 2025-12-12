package com.example.wikipro.controller;

import com.example.wikipro.common.Result;
import com.example.wikipro.dto.AddToCartDTO;
import com.example.wikipro.dto.UpdateCartDTO;
import com.example.wikipro.entity.WxCart;
import com.example.wikipro.service.WxCartService;
import com.example.wikipro.utils.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/cart")
public class WxCartController {

    @Autowired
    private WxCartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Result<?> addToCart(
            @RequestBody AddToCartDTO addToCartDTO,
            HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录");
        }
        
        if (addToCartDTO == null || addToCartDTO.getGoodsId() == null || addToCartDTO.getQuantity() == null) {
            return Result.failure("商品ID和数量不能为空");
        }
        
        return cartService.addToCart(userId, addToCartDTO.getGoodsId(), addToCartDTO.getQuantity());
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public Result<?> updateQuantity(
            @RequestBody UpdateCartDTO updateCartDTO,
            HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录");
        }
        
        if (updateCartDTO == null || updateCartDTO.getCartId() == null || updateCartDTO.getQuantity() == null) {
            return Result.failure("购物车ID和数量不能为空");
        }
        
        return cartService.updateQuantity(updateCartDTO.getCartId(), userId, updateCartDTO.getQuantity());
    }

    /**
     * 从购物车删除商品
     */
    @DeleteMapping("/remove/{cartId}")
    public Result<?> removeFromCart(
            @PathVariable("cartId") Integer cartId,
            HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录");
        }
        return cartService.removeFromCart(cartId, userId);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<?> clearCart(HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录");
        }
        return cartService.clearCart(userId);
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public Result<List<WxCart>> getCartList(HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录", null);
        }
        return cartService.getCartList(userId);
    }
}

