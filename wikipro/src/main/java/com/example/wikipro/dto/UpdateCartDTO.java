package com.example.wikipro.dto;

import lombok.Data;

/**
 * 更新购物车DTO
 */
@Data
public class UpdateCartDTO {
    /**
     * 购物车ID
     */
    private Integer cartId;

    /**
     * 数量
     */
    private Integer quantity;
}

