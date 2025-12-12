package com.example.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wikipro.common.Result;
import com.example.wikipro.common.ResultStatus;
import com.example.wikipro.entity.WxCart;
import com.example.wikipro.entity.WxGoods;
import com.example.wikipro.entity.WxOrder;
import com.example.wikipro.entity.WxOrderItem;
import com.example.wikipro.enums.OrderStatus;
import com.example.wikipro.mapper.WxCartMapper;
import com.example.wikipro.mapper.WxGoodsMapper;
import com.example.wikipro.mapper.WxOrderItemMapper;
import com.example.wikipro.mapper.WxOrderMapper;
import com.example.wikipro.service.WxOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class WxOrderServiceImpl extends ServiceImpl<WxOrderMapper, WxOrder> implements WxOrderService {

    @Autowired
    private WxOrderMapper orderMapper;

    @Autowired
    private WxOrderItemMapper orderItemMapper;

    @Autowired
    private WxCartMapper cartMapper;

    @Autowired
    private WxGoodsMapper goodsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<WxOrder> createOrder(Integer userId, List<Integer> cartIds, 
                                      String receiverName, String receiverPhone, 
                                      String receiverAddress, String remark) {
        // 参数校验
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效", null);
        }
        if (cartIds == null || cartIds.isEmpty()) {
            return Result.failure("购物车ID列表不能为空", null);
        }
        if (receiverName == null || receiverName.trim().isEmpty()) {
            return Result.failure("收货人姓名不能为空", null);
        }
        if (receiverPhone == null || receiverPhone.trim().isEmpty()) {
            return Result.failure("收货人电话不能为空", null);
        }
        if (receiverAddress == null || receiverAddress.trim().isEmpty()) {
            return Result.failure("收货地址不能为空", null);
        }

        // 查询购物车记录
        List<WxCart> cartList = cartIds.stream()
                .map(cartId -> {
                    WxCart cart = cartMapper.selectById(cartId);
                    if (cart == null || !cart.getUserId().equals(userId)) {
                        return null;
                    }
                    return cart;
                })
                .filter(cart -> cart != null)
                .collect(Collectors.toList());

        if (cartList.isEmpty()) {
            return Result.failure("购物车记录不存在或无权访问", null);
        }

        // 计算订单总金额并验证库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WxCart cart : cartList) {
            WxGoods goods = goodsMapper.selectById(cart.getGoodsId());
            if (goods == null) {
                return Result.failure("商品不存在，商品ID: " + cart.getGoodsId(), null);
            }
            if (goods.getStock() < cart.getQuantity()) {
                return Result.failure("商品库存不足，商品: " + goods.getGoodsName() + "，当前库存: " + goods.getStock(), null);
            }
            BigDecimal subtotal = goods.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单
        Date now = new Date();
        WxOrder order = new WxOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setReceiverName(receiverName.trim());
        order.setReceiverPhone(receiverPhone.trim());
        order.setReceiverAddress(receiverAddress.trim());
        order.setRemark(remark != null ? remark.trim() : "");
        order.setCreateTime(now);
        order.setUpdateTime(now);
        orderMapper.insert(order);

        // 创建订单项并扣减库存
        for (WxCart cart : cartList) {
            WxGoods goods = goodsMapper.selectById(cart.getGoodsId());
            
            // 创建订单项
            WxOrderItem orderItem = new WxOrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setGoodsId(cart.getGoodsId());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(goods.getPrice());
            orderItem.setSubtotal(goods.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItemMapper.insert(orderItem);

            // 扣减库存
            goods.setStock(goods.getStock() - cart.getQuantity());
            goods.setSales(goods.getSales() + cart.getQuantity()); // 增加销量
            goods.setUpdateTime(now);
            goodsMapper.updateById(goods);
        }

        // 清空购物车（删除已下单的商品）
        for (Integer cartId : cartIds) {
            cartMapper.deleteById(cartId);
        }

        // 查询完整的订单信息（包含订单项）
        WxOrder completeOrder = getOrderWithItems(order.getOrderId());

        log.info("创建订单成功 - orderId: {}, orderNo: {}, userId: {}, totalAmount: {}", 
                order.getOrderId(), orderNo, userId, totalAmount);
        
        return Result.success(ResultStatus.SUCCESS, completeOrder);
    }

    @Override
    public Result<List<WxOrder>> getOrderList(Integer userId, Integer status) {
        // 参数校验
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效", null);
        }

        // 构建查询条件
        LambdaQueryWrapper<WxOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxOrder::getUserId, userId);
        if (status != null && OrderStatus.isValid(status)) {
            wrapper.eq(WxOrder::getStatus, status);
        }
        wrapper.orderByDesc(WxOrder::getCreateTime);

        List<WxOrder> orderList = orderMapper.selectList(wrapper);

        // 关联查询订单项
        for (WxOrder order : orderList) {
            List<WxOrderItem> orderItems = getOrderItems(order.getOrderId());
            order.setOrderItems(orderItems);
        }

        log.debug("查询订单列表 - userId: {}, status: {}, 数量: {}", userId, status, orderList.size());
        return Result.success(ResultStatus.SUCCESS, orderList);
    }

    @Override
    public Result<WxOrder> getOrderDetail(Integer orderId, Integer userId) {
        // 参数校验
        if (orderId == null || orderId <= 0) {
            return Result.failure("订单ID无效", null);
        }
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效", null);
        }

        // 查询订单
        WxOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.failure("订单不存在", null);
        }

        // 验证用户权限
        if (!order.getUserId().equals(userId)) {
            return Result.failure("无权访问此订单", null);
        }

        // 查询订单项
        WxOrder completeOrder = getOrderWithItems(orderId);

        log.debug("查询订单详情 - orderId: {}, userId: {}", orderId, userId);
        return Result.success(ResultStatus.SUCCESS, completeOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateOrderStatus(Integer orderId, Integer userId, Integer status) {
        // 参数校验
        if (orderId == null || orderId <= 0) {
            return Result.failure("订单ID无效");
        }
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效");
        }
        if (!OrderStatus.isValid(status)) {
            return Result.failure("订单状态无效");
        }

        // 查询订单
        WxOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.failure("订单不存在");
        }

        // 验证用户权限
        if (!order.getUserId().equals(userId)) {
            return Result.failure("无权操作此订单");
        }

        // 更新订单状态
        order.setStatus(status);
        order.setUpdateTime(new Date());
        orderMapper.updateById(order);

        log.info("更新订单状态 - orderId: {}, status: {}", orderId, OrderStatus.fromCode(status).getDescription());
        return Result.success("更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> cancelOrder(Integer orderId, Integer userId) {
        // 参数校验
        if (orderId == null || orderId <= 0) {
            return Result.failure("订单ID无效");
        }
        if (userId == null || userId <= 0) {
            return Result.failure("用户ID无效");
        }

        // 查询订单
        WxOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.failure("订单不存在");
        }

        // 验证用户权限
        if (!order.getUserId().equals(userId)) {
            return Result.failure("无权操作此订单");
        }

        // 只有待支付和已支付的订单可以取消
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode() 
            && order.getStatus() != OrderStatus.PAID.getCode()) {
            return Result.failure("当前订单状态不允许取消");
        }

        // 如果订单已支付，需要恢复库存
        if (order.getStatus() == OrderStatus.PAID.getCode()) {
            // 查询订单项
            List<WxOrderItem> orderItems = getOrderItems(orderId);
            for (WxOrderItem item : orderItems) {
                WxGoods goods = goodsMapper.selectById(item.getGoodsId());
                if (goods != null) {
                    goods.setStock(goods.getStock() + item.getQuantity());
                    goods.setSales(goods.getSales() - item.getQuantity()); // 减少销量
                    goods.setUpdateTime(new Date());
                    goodsMapper.updateById(goods);
                }
            }
        }

        // 更新订单状态为已取消
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setUpdateTime(new Date());
        orderMapper.updateById(order);

        log.info("取消订单 - orderId: {}, userId: {}", orderId, userId);
        return Result.success("订单已取消");
    }

    /**
     * 生成订单号
     * 格式：YYYYMMDDHHmmss + 6位随机数
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        int random = (int) (Math.random() * 1000000);
        return dateStr + String.format("%06d", random);
    }

    /**
     * 获取订单项列表
     */
    private List<WxOrderItem> getOrderItems(Integer orderId) {
        LambdaQueryWrapper<WxOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxOrderItem::getOrderId, orderId);
        List<WxOrderItem> orderItems = orderItemMapper.selectList(wrapper);
        
        // 关联查询商品信息
        for (WxOrderItem item : orderItems) {
            WxGoods goods = goodsMapper.selectById(item.getGoodsId());
            item.setGoods(goods);
        }
        
        return orderItems;
    }

    /**
     * 获取包含订单项的完整订单信息
     */
    private WxOrder getOrderWithItems(Integer orderId) {
        WxOrder order = orderMapper.selectById(orderId);
        if (order != null) {
            List<WxOrderItem> orderItems = getOrderItems(orderId);
            order.setOrderItems(orderItems);
        }
        return order;
    }
}

