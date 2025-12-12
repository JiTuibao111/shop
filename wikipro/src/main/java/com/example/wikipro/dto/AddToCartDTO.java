package com.example.wikipro.dto;

import lombok.Data;

/**
 * 添加到购物车DTO
 */
@Data
public class AddToCartDTO {
    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 数量
     */
    private Integer quantity;
}

