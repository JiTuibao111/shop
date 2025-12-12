package com.example.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 * @TableName wx_orders
 */
@TableName(value = "wx_orders")
@Data
public class WxOrder implements Serializable {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Integer orderId;

    /**
     * 订单号（唯一）
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 订单项列表（关联查询，不映射到数据库）
     */
    @TableField(exist = false)
    private List<WxOrderItem> orderItems;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

