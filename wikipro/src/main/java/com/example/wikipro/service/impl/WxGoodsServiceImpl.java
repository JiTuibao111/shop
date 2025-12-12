package com.example.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wikipro.common.Result;
import com.example.wikipro.common.ResultStatus;
import com.example.wikipro.entity.WxGoods;
import com.example.wikipro.enums.SortField;
import com.example.wikipro.enums.SortOrder;
import com.example.wikipro.mapper.WxGoodsMapper;
import com.example.wikipro.service.WxGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【wx_goods】的数据库操作Service实现
* @createDate 2025-12-11 19:17:29
*/
@Slf4j
@Service
public class WxGoodsServiceImpl extends ServiceImpl<WxGoodsMapper, WxGoods>
    implements WxGoodsService{

    @Override
    public Result<?> getPageByGoods(int currentPage, int pageSize) {
        return getPageByGoods(currentPage, pageSize, null, null);
    }

    @Override
    public Result<?> getPageByGoods(int currentPage, int pageSize, String sortField, String sortOrder) {
        Page<WxGoods> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<WxGoods> wrapper = new LambdaQueryWrapper<>();
        
        // 应用排序
        applySort(wrapper, sortField, sortOrder);
        
        Page<WxGoods> goodsPage = this.page(page, wrapper);
        return Result.success(goodsPage);
    }

    @Override
    public Result<?> getPageByKey(String searchKey, int currentPage, int pageSize) {
        return getPageByKey(searchKey, currentPage, pageSize, null, null);
    }

    @Override
    public Result<?> getPageByKey(String searchKey, int currentPage, int pageSize, String sortField, String sortOrder) {
        Page<WxGoods> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<WxGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(WxGoods::getGoodsName, searchKey);
        
        // 应用排序
        applySort(wrapper, sortField, sortOrder);
        
        Page<WxGoods> goodsPage = this.page(page, wrapper);
        return Result.success(goodsPage);
    }

    @Override
    public Result<?> getCategoryList() {
        // 查询去重的商品分类列表
        List<String> categories = this.lambdaQuery()
                .select(WxGoods::getCategory)
                .groupBy(WxGoods::getCategory)
                .list()
                .stream()
                .map(WxGoods::getCategory)
                .collect(Collectors.toList());
        return Result.success(categories);
    }

    @Override
    public Result<?> getByCategory(String category) {
        List<WxGoods> goods = this.lambdaQuery()
                .eq(WxGoods::getCategory, category)
                .list();
        return Result.success(goods);
    }

    @Override
    public Result<WxGoods> getGoodsById(Integer goodsId) {
        if (goodsId == null || goodsId <= 0) {
            return Result.failure("商品ID无效", (WxGoods) null);
        }
        
        WxGoods goods = this.getById(goodsId);
        if (goods == null) {
            log.warn("商品不存在 - goodsId: {}", goodsId);
            return Result.failure("商品不存在", (WxGoods) null);
        }
        
        log.debug("查询商品详情成功 - goodsId: {}, goodsName: {}", goodsId, goods.getGoodsName());
        return Result.success(ResultStatus.SUCCESS, goods);
    }

    /**
     * 应用排序条件到查询包装器
     *
     * @param wrapper   查询包装器
     * @param sortField 排序字段
     * @param sortOrder 排序方向
     */
    private void applySort(LambdaQueryWrapper<WxGoods> wrapper, String sortField, String sortOrder) {
        SortField field = SortField.fromFieldName(sortField);
        SortOrder order = SortOrder.fromString(sortOrder);
        
        if (field == null) {
            // 如果没有指定排序字段，默认按创建时间降序
            wrapper.orderByDesc(WxGoods::getCreateTime);
            return;
        }
        
        // 根据排序字段和方向应用排序
        switch (field) {
            case PRICE:
                if (order.isAscending()) {
                    wrapper.orderByAsc(WxGoods::getPrice);
                } else {
                    wrapper.orderByDesc(WxGoods::getPrice);
                }
                break;
            case SALES:
                if (order.isAscending()) {
                    wrapper.orderByAsc(WxGoods::getSales);
                } else {
                    wrapper.orderByDesc(WxGoods::getSales);
                }
                break;
            case CREATE_TIME:
                if (order.isAscending()) {
                    wrapper.orderByAsc(WxGoods::getCreateTime);
                } else {
                    wrapper.orderByDesc(WxGoods::getCreateTime);
                }
                break;
            case UPDATE_TIME:
                if (order.isAscending()) {
                    wrapper.orderByAsc(WxGoods::getUpdateTime);
                } else {
                    wrapper.orderByDesc(WxGoods::getUpdateTime);
                }
                break;
            default:
                // 默认按创建时间降序
                wrapper.orderByDesc(WxGoods::getCreateTime);
                break;
        }
    }
}




