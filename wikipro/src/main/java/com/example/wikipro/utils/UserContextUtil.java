package com.example.wikipro.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 * 用于从请求中获取当前登录用户信息
 */
public class UserContextUtil {

    /**
     * 从请求中获取用户ID
     *
     * @param request HTTP请求
     * @return 用户ID，如果未登录返回null
     */
    public static Integer getUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId instanceof Integer) {
            return (Integer) userId;
        }
        return null;
    }

    /**
     * 从请求中获取用户名
     *
     * @param request HTTP请求
     * @return 用户名，如果未登录返回null
     */
    public static String getUsername(HttpServletRequest request) {
        Object username = request.getAttribute("username");
        if (username instanceof String) {
            return (String) username;
        }
        return null;
    }
}

