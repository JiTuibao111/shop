package com.example.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项实体类
 * @TableName wx_order_item
 */
@TableName(value = "wx_order_item")
@Data
public class WxOrderItem implements Serializable {
    /**
     * 订单项ID
     */
    @TableId(type = IdType.AUTO)
    private Integer itemId;

    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 商品单价（下单时的价格）
     */
    private BigDecimal price;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;

    /**
     * 商品信息（关联查询，不映射到数据库）
     */
    @TableField(exist = false)
    private WxGoods goods;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

