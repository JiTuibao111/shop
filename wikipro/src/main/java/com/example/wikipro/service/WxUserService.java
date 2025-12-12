package com.example.wikipro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wikipro.entity.WxUser;
import com.example.wikipro.common.Result;


public interface WxUserService extends IService<WxUser> {
    Result register(WxUser wxUser);
    Result login(WxUser wxUser);

    Result<WxUser> getUserInfoById(Long userId);
}
