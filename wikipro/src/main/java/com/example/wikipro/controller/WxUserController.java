package com.example.wikipro.controller;

import com.example.wikipro.entity.WxUser;
import com.example.wikipro.service.WxUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.wikipro.common.Result;

@Slf4j
@RestController
@RequestMapping("/WxUser")
public class WxUserController {
    @Autowired
    WxUserService wxUserService;

    @PostMapping("/register")
    public Result register(@RequestBody WxUser wxUser) {
       Result register = wxUserService.register(wxUser);
        log.debug("注册返回的数据: {}", register);
        return register;
    }

    @PostMapping("/login")
    public Result login(@RequestBody WxUser wxUser) {
        Result login = wxUserService.login(wxUser);
        log.debug("登录返回的数据: {}", login);
        return login;
    }

    @GetMapping("/findUserInfo")
    public Result<WxUser> findUserInfo(@RequestParam("userId") Long userId){
        if (userId == null || userId <= 0) {
            return (Result<WxUser>) Result.failure("用户ID无效");
        }
        return wxUserService.getUserInfoById(userId);
    }

    @GetMapping("/checkTokenStatus")
    public Result checkTokenStatus(){
        // 如果能够访问到这个接口，说明token验证通过
        // 可以从请求中获取用户信息（由拦截器设置）
        return Result.success("token验证通过，用户已登录");
    }
}