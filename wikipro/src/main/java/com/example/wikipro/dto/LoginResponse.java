package com.example.wikipro.dto;

import com.example.wikipro.entity.WxUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 * 包含token和用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    /**
     * JWT token
     */
    private String token;

    /**
     * 用户信息（不包含密码）
     */
    private WxUser user;
}

