package com.example.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 购物车实体类
 * @TableName wx_cart
 */
@TableName(value = "wx_cart")
@Data
public class WxCart implements Serializable {
    /**
     * 购物车ID
     */
    @TableId(type = IdType.AUTO)
    private Integer cartId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 商品信息（关联查询，不映射到数据库）
     */
    @TableField(exist = false)
    private WxGoods goods;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

