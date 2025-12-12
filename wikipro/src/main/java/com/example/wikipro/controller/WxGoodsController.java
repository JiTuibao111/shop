package com.example.wikipro.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.wikipro.common.Result;
import com.example.wikipro.entity.WxGoods;
import com.example.wikipro.service.WxGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/goods")
public class WxGoodsController {
    @Autowired
    WxGoodsService wxGoodsService;

    @GetMapping("/getPageByGoods")
    public Result getPageByGoods(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "6") Integer pageSize,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        
        // 参数校验
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 6;
        }

        log.debug("分页查询商品 - 当前页: {}, 每页大小: {}, 排序字段: {}, 排序方向: {}", 
                currentPage, pageSize, sortField, sortOrder);

        return wxGoodsService.getPageByGoods(currentPage, pageSize, sortField, sortOrder);
    }

    @GetMapping("/getPageQueryByGoods")
    public Result getPageQueryByGoods(
            @RequestParam(required = false) String searchKey,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {

        // 参数校验
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 6;
        }

        Result result;
        if (StringUtils.isBlank(searchKey)) {
            log.debug("分页查询商品 - 当前页: {}, 每页大小: {}, 排序字段: {}, 排序方向: {}", 
                    currentPage, pageSize, sortField, sortOrder);
            result = wxGoodsService.getPageByGoods(currentPage, pageSize, sortField, sortOrder);
        } else {
            log.debug("搜索商品 - 关键字: {}, 当前页: {}, 每页大小: {}, 排序字段: {}, 排序方向: {}", 
                    searchKey, currentPage, pageSize, sortField, sortOrder);
            result = wxGoodsService.getPageByKey(searchKey.trim(), currentPage, pageSize, sortField, sortOrder);
        }

        return result;
    }

    @GetMapping("/findCategoryList")
    public Result findCategoryList() {
        return wxGoodsService.getCategoryList();
    }

    @GetMapping("/findByCategory")
    public Result getByCategory(@RequestParam("category") String category) {
        return wxGoodsService.getByCategory(category);
    }

    @GetMapping("/getById/{goodsId}")
    public Result<WxGoods> getGoodsById(@PathVariable("goodsId") Integer goodsId) {
        log.debug("查询商品详情 - goodsId: {}", goodsId);
        return wxGoodsService.getGoodsById(goodsId);
    }

}
