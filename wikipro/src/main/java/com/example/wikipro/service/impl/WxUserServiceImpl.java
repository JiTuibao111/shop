package com.example.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wikipro.dto.LoginResponse;
import com.example.wikipro.entity.WxUser;
import com.example.wikipro.mapper.WxUserMapper;
import com.example.wikipro.service.WxUserService;
import com.example.wikipro.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.wikipro.common.Result;

@Slf4j
@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {
    @Autowired
    WxUserMapper wxUserMapper;

    /**
     * BCrypt密码加密器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result register(WxUser wxUser) {
        // 参数验证
        if (wxUser.getUsername() == null || wxUser.getUsername().trim().isEmpty()) {
            return Result.failure("用户名不能为空");
        }
        if (wxUser.getPassword() == null || wxUser.getPassword().trim().isEmpty()) {
            return Result.failure("密码不能为空");
        }
        
        // 检查用户名是否已存在
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", wxUser.getUsername());
        WxUser selectOne = wxUserMapper.selectOne(queryWrapper);
        if (selectOne != null) {
            log.warn("注册失败：用户名已存在 - {}", wxUser.getUsername());
            return Result.failure("用户已存在");
        }
        
        // 使用BCrypt加密密码
        String encodedPassword = passwordEncoder.encode(wxUser.getPassword());
        wxUser.setPassword(encodedPassword);
        
        int row = wxUserMapper.insert(wxUser);
        if (row > 0) {
            log.info("用户注册成功 - {}", wxUser.getUsername());
            return Result.success("注册成功");
        }
        log.error("用户注册失败 - {}", wxUser.getUsername());
        return Result.failure("注册失败");
    }

    @Override
    public Result login(WxUser wxUser){
        // 参数验证
        if (wxUser.getUsername() == null || wxUser.getUsername().trim().isEmpty()) {
            return Result.failure("用户名不能为空");
        }
        if (wxUser.getPassword() == null || wxUser.getPassword().trim().isEmpty()) {
            return Result.failure("密码不能为空");
        }
        
        // 查询用户
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", wxUser.getUsername());
        WxUser user = wxUserMapper.selectOne(queryWrapper);
        
        if (user == null) {
            log.warn("登录失败：用户不存在 - {}", wxUser.getUsername());
            return Result.failure("用户名或密码错误");
        }
        
        // 使用BCrypt验证密码
        if (!passwordEncoder.matches(wxUser.getPassword(), user.getPassword())) {
            log.warn("登录失败：密码错误 - {}", wxUser.getUsername());
            return Result.failure("用户名或密码错误");
        }
        
        // 生成JWT token
        String token = JwtUtil.generateToken(user.getUserId(), user.getUsername());
        if (token == null) {
            log.error("token生成失败 - {}", wxUser.getUsername());
            return Result.failure("登录失败，请稍后重试");
        }
        
        log.info("用户登录成功 - {}", wxUser.getUsername());
        
        // 清除敏感信息
        user.setPassword(null);
        
        // 构建登录响应
        LoginResponse loginResponse = new LoginResponse(token, user);
        return Result.success("登录成功", loginResponse);
    }

    @Override
    public Result<WxUser> getUserInfoById(Long userId) {
        WxUser user = wxUserMapper.selectById(userId);
        if (user != null) {
            return Result.success(user);
        }
        return Result.failure("用户不存在", (WxUser) null);
    }
}

