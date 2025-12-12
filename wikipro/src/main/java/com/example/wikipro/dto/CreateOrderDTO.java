package com.example.wikipro.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class CreateOrderDTO {
    /**
     * 购物车ID列表
     */
    private List<Integer> cartIds;

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
}

