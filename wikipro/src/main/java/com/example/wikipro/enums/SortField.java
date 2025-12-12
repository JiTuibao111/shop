package com.example.wikipro.enums;

import lombok.Getter;

/**
 * 商品排序字段枚举
 */
@Getter
public enum SortField {
    /**
     * 价格排序
     */
    PRICE("price", "价格"),

    /**
     * 销量排序
     */
    SALES("sales", "销量"),

    /**
     * 创建时间排序
     */
    CREATE_TIME("createTime", "创建时间"),

    /**
     * 更新时间排序
     */
    UPDATE_TIME("updateTime", "更新时间");

    private final String fieldName;
    private final String description;

    SortField(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }

    /**
     * 根据字段名获取枚举
     *
     * @param fieldName 字段名
     * @return 排序字段枚举，如果不存在返回null
     */
    public static SortField fromFieldName(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return null;
        }
        for (SortField field : values()) {
            if (field.getFieldName().equalsIgnoreCase(fieldName.trim())) {
                return field;
            }
        }
        return null;
    }

    /**
     * 检查字段名是否有效
     *
     * @param fieldName 字段名
     * @return true-有效，false-无效
     */
    public static boolean isValid(String fieldName) {
        return fromFieldName(fieldName) != null;
    }
}

