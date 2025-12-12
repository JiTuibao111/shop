package com.example.wikipro.enums;

import lombok.Getter;

/**
 * 排序方向枚举
 */
@Getter
public enum SortOrder {
    /**
     * 升序
     */
    ASC("asc", "升序"),

    /**
     * 降序
     */
    DESC("desc", "降序");

    private final String order;
    private final String description;

    SortOrder(String order, String description) {
        this.order = order;
        this.description = description;
    }

    /**
     * 根据字符串获取排序方向
     *
     * @param order 排序方向字符串
     * @return 排序方向枚举，如果无效返回ASC
     */
    public static SortOrder fromString(String order) {
        if (order == null || order.trim().isEmpty()) {
            return ASC;
        }
        String orderLower = order.trim().toLowerCase();
        for (SortOrder sortOrder : values()) {
            if (sortOrder.getOrder().equals(orderLower)) {
                return sortOrder;
            }
        }
        return ASC; // 默认升序
    }

    /**
     * 是否为升序
     *
     * @return true-升序，false-降序
     */
    public boolean isAscending() {
        return this == ASC;
    }
}

