package com.example.wikipro.service;

import com.example.wikipro.common.Result;
import com.example.wikipro.entity.WxGoods;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【wx_goods】的数据库操作Service
* @createDate 2025-12-11 19:17:29
*/
public interface WxGoodsService extends IService<WxGoods> {

    Result<?> getPageByGoods(int currentPage, int pageSize);

    Result<?> getPageByGoods(int currentPage, int pageSize, String sortField, String sortOrder);

    Result<?> getPageByKey(String searchKey, int currentPage, int pageSize);

    Result<?> getPageByKey(String searchKey, int currentPage, int pageSize, String sortField, String sortOrder);

    Result getCategoryList();

    Result getByCategory(String category);

    Result<WxGoods> getGoodsById(Integer goodsId);

}
