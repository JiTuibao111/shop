package com.example.wikipro.mapper;

import com.example.wikipro.entity.WxUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

@Repository
public interface WxUserMapper extends BaseMapper<WxUser> {

}
