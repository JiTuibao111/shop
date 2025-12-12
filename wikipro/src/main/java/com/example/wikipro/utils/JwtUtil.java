package com.example.wikipro.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * JWT工具类
 * 用于生成、验证和解析JWT token
 */
@Slf4j
public class JwtUtil {

    /**
     * token过期时间：7天
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    /**
     * JWT密钥（生产环境应该从配置文件读取）
     */
    private static final String SECRET = "wikipro-secret-key-2024";

    /**
     * 生成JWT token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return token字符串
     */
    public static String generateToken(Integer userId, String username) {
        try {
            Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withExpiresAt(expireDate)
                    .withIssuedAt(new Date())
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("JWT token生成失败", e);
            return null;
        }
    }

    /**
     * 验证token是否有效
     *
     * @param token token字符串
     * @return true-有效，false-无效
     */
    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从token中获取用户ID
     *
     * @param token token字符串
     * @return 用户ID，如果token无效返回null
     */
    public static Integer getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asInt();
        } catch (Exception e) {
            log.warn("从token中获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取用户名
     *
     * @param token token字符串
     * @return 用户名，如果token无效返回null
     */
    public static String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (Exception e) {
            log.warn("从token中获取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查token是否过期
     *
     * @param token token字符串
     * @return true-已过期，false-未过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Date expiresAt = jwt.getExpiresAt();
            return expiresAt != null && expiresAt.before(new Date());
        } catch (Exception e) {
            log.warn("检查token过期状态失败: {}", e.getMessage());
            return true;
        }
    }
}

