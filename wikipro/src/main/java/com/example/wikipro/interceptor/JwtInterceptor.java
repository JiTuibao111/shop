package com.example.wikipro.interceptor;

import com.example.wikipro.common.Result;
import com.example.wikipro.common.ResultStatus;
import com.example.wikipro.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT拦截器
 * 用于验证请求中的token
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = getTokenFromRequest(request);

        // 如果没有token，返回401
        if (token == null || token.isEmpty()) {
            log.warn("请求缺少token: {}", request.getRequestURI());
            return sendErrorResponse(response, ResultStatus.UNAUTHORIZED.getCode(), "未授权，请先登录");
        }

        // 验证token
        if (!JwtUtil.verifyToken(token)) {
            log.warn("token验证失败: {}", request.getRequestURI());
            return sendErrorResponse(response, ResultStatus.UNAUTHORIZED.getCode(), "token无效或已过期");
        }

        // 检查token是否过期
        if (JwtUtil.isTokenExpired(token)) {
            log.warn("token已过期: {}", request.getRequestURI());
            return sendErrorResponse(response, ResultStatus.UNAUTHORIZED.getCode(), "token已过期，请重新登录");
        }

        // 将用户ID和用户名存储到request中，方便后续使用
        Integer userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);

        return true;
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        // 也支持从query参数中获取token（用于某些特殊情况）
        String queryToken = request.getParameter("token");
        return queryToken;
    }

    /**
     * 发送错误响应
     */
    private boolean sendErrorResponse(HttpServletResponse response, Integer code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.failure(code, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
        return false;
    }
}

