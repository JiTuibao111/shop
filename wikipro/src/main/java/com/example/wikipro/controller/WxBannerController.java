package com.example.wikipro.controller;

import com.example.wikipro.common.Result;
import com.example.wikipro.entity.WxBanner;
import com.example.wikipro.service.WxBannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/banner")
public class WxBannerController {
    @Autowired
    WxBannerService wxBannerService;
    
    @GetMapping("/findBanner")
    public Result findBanner() {
        List<WxBanner> list = wxBannerService.list();
        log.debug("查询轮播图列表 - 数量: {}", list.size());
        return Result.success(list);
    }
}
