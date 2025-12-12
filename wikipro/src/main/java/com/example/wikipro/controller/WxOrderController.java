package com.example.wikipro.controller;

import com.example.wikipro.common.Result;
import com.example.wikipro.dto.CreateOrderDTO;
import com.example.wikipro.entity.WxOrder;
import com.example.wikipro.service.WxOrderService;
import com.example.wikipro.utils.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class WxOrderController {

    @Autowired
    private WxOrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<WxOrder> createOrder(
            @RequestBody CreateOrderDTO createOrderDTO,
            HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录", null);
        }
        return orderService.createOrder(
                userId,
                createOrderDTO.getCartIds(),
                createOrderDTO.getReceiverName(),
                createOrderDTO.getReceiverPhone(),
                createOrderDTO.getReceiverAddress(),
                createOrderDTO.getRemark()
        );
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public Result<List<WxOrder>> getOrderList(
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录", null);
        }
        return orderService.getOrderList(userId, status);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    public Result<WxOrder> getOrderDetail(
            @PathVariable("orderId") Integer orderId,
            HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录", null);
        }
        return orderService.getOrderDetail(orderId, userId);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/status")
    public Result<?> updateOrderStatus(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("status") Integer status,
            HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录");
        }
        return orderService.updateOrderStatus(orderId, userId, status);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderId}")
    public Result<?> cancelOrder(
            @PathVariable("orderId") Integer orderId,
            HttpServletRequest request) {
        Integer userId = UserContextUtil.getUserId(request);
        if (userId == null) {
            return Result.failure("用户未登录");
        }
        return orderService.cancelOrder(orderId, userId);
    }
}

